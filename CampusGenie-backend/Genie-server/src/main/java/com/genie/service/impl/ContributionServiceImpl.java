package com.genie.service.impl;

import com.genie.constant.ActionTypeConstant;
import com.genie.constant.RedisConstant;
import com.genie.constant.StatusConstant;
import com.genie.constant.TargetTypeConstant;
import com.genie.context.BaseContext;
import com.genie.dto.*;
import com.genie.entity.KnowledgeBase;
import com.genie.entity.ReviewLog;
import com.genie.entity.UserContribution;
import com.genie.exception.ContributionAlreadyExistsException;
import com.genie.exception.ContributionAlreadyReviewedException;
import com.genie.exception.EmptyContributionListException;
import com.genie.exception.InvalidActionException;
import com.genie.exception.RejectReasonRequiredException;
import com.genie.exception.UserNotLoginException;
import com.genie.mapper.KnowledgeBaseMapper;
import com.genie.mapper.ReviewLogMapper;
import com.genie.mapper.UserContributionMapper;
import com.genie.result.PageResult;
import com.genie.service.ContributionService;
import com.genie.service.RateLimitService;
import com.genie.vo.AdminContributionVO;
import com.genie.vo.BatchReviewVO;
import com.genie.vo.UserContributionVO;
import com.genie.vo.ContributionStatisticsVO;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ContributionServiceImpl implements ContributionService {
    @Autowired
    private UserContributionMapper userContributionMapper;
    @Autowired
    private KnowledgeBaseMapper knowledgeBaseMapper;
    @Autowired
    private ReviewLogMapper reviewLogMapper;
    @Autowired
    private RateLimitService rateLimitService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void initPendingReviewCount() {
        try {
            Integer count = userContributionMapper.countByStatus(StatusConstant.WAIT_FOR_REVIEW);
            redisTemplate.opsForValue().set(RedisConstant.PENDING_REVIEW_COUNT, count != null ? count : 0);
            log.info("初始化待审核数量: {}", count);
        } catch (Exception e) {
            log.error("初始化待审核数量失败", e);
        }
    }

    private void incrementPendingCount() {
        try {
            redisTemplate.opsForValue().increment(RedisConstant.PENDING_REVIEW_COUNT, 1);
        } catch (Exception e) {
            log.error("增加待审核数量失败", e);
        }
    }

    private void decrementPendingCount() {
        try {
            redisTemplate.opsForValue().increment(RedisConstant.PENDING_REVIEW_COUNT, -1);
        } catch (Exception e) {
            log.error("减少待审核数量失败", e);
        }
    }

    @Override
    public Integer getPendingReviewCount() {
        try {
            Object value = redisTemplate.opsForValue().get(RedisConstant.PENDING_REVIEW_COUNT);
            if (value != null) {
                if (value instanceof Integer) {
                    return (Integer) value;
                } else if (value instanceof Long) {
                    return ((Long) value).intValue();
                } else {
                    return Integer.parseInt(value.toString());
                }
            }
        } catch (Exception e) {
            log.error("获取待审核数量失败，降级查询数据库", e);
        }
        return userContributionMapper.countByStatus(StatusConstant.WAIT_FOR_REVIEW);
    }

    @Override
    public void contribute(ContributionSubmitDTO contributionSubmitDTO){

        Long userId = BaseContext.getCurrentUserId();
        rateLimitService.checkContributeLimit(userId);



        String question = contributionSubmitDTO.getQuestion().trim();
        Integer existingCount = userContributionMapper.countByUserIdAndQuestion(userId, question);
        if (existingCount != null && existingCount > 0) {
            throw new ContributionAlreadyExistsException("该问题已提交，请勿重复提交");
        }

        contributionSubmitDTO.setQuestion(question);
        //创建实体并插入数据库
        UserContribution userContribution = new UserContribution();
        BeanUtils.copyProperties(contributionSubmitDTO,userContribution);
        userContribution.setUserId(userId);
        userContribution.setStatus(StatusConstant.WAIT_FOR_REVIEW);
        userContribution.setCreatedTime(LocalDateTime.now());
        userContributionMapper.insert(userContribution);
        incrementPendingCount();
    }

    @Override
    public PageResult pageQueryByUser(ContributionPageQueryDTO contributionPageQueryDTO) {
        PageHelper.startPage(contributionPageQueryDTO.getPage(),contributionPageQueryDTO.getPageSize());
        Long userId = BaseContext.getCurrentUserId();
        if (userId == null) {
            throw new UserNotLoginException("请先登录后再查看我的贡献");
        }
        Page<UserContributionVO> page = userContributionMapper.pageQueryByUser(userId,contributionPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());

    }

    @Override
    public PageResult pageQueryByAdmin(AdminContributionPageQueryDTO adminContributionPageQueryDTO) {
        PageHelper.startPage(adminContributionPageQueryDTO.getPage(),adminContributionPageQueryDTO.getPageSize());
        Page<AdminContributionVO> page = userContributionMapper.pageQueryByAdmin(adminContributionPageQueryDTO);
        //对用户名字脱敏 如张三->张**
        for (AdminContributionVO adminContributionVO : page) {
            String username = adminContributionVO.getUsername();
            adminContributionVO.setUsername(username.substring(0,1)+"**");

        }
        return new PageResult(page.getTotal(),page.getResult());

    }

    @Override
    @Transactional
    public void approve(Long id, ApproveDTO approveDTO) {
        //获取并修改用户贡献
        UserContribution userContribution = userContributionMapper.selectById(id);
        //判断状态是否为待审核
        if (!userContribution.getStatus().equals(StatusConstant.WAIT_FOR_REVIEW)){
            throw new ContributionAlreadyReviewedException( "该贡献已审核");
        }
        //更新状态及审核人等信息
        userContribution.setStatus(StatusConstant.REVIEW_PASS);
        userContribution.setReviewedBy(BaseContext.getCurrentUsername());
        userContribution.setReviewedTime(LocalDateTime.now());
        userContributionMapper.update(userContribution);
        decrementPendingCount();
        //合并知识库数据Knowledge_Base
        KnowledgeBase knowledgeBase=KnowledgeBase.builder()
                .question(approveDTO.getEditedQuestion()!=  null? approveDTO.getEditedQuestion() : userContribution.getQuestion())
                .answer( approveDTO.getEditedAnswer()!=  null? approveDTO.getEditedAnswer() : userContribution.getAnswer())
                .categoryId(userContribution.getCategoryId())
                .source("用户贡献")
                .status(StatusConstant.PUBLISHED)
                .contributionId(id)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .createdBy(BaseContext.getCurrentUsername())
                .updatedBy(BaseContext.getCurrentUsername())
                .build();
        knowledgeBaseMapper.insert(knowledgeBase);
        //记录审核日志
        ReviewLog reviewLog=ReviewLog.builder()
                .contributionType( TargetTypeConstant.Review_TYPE_CONTRIBUTION)
                .contributionId(id)
                .reviewer(BaseContext.getCurrentUsername())
                .action( ActionTypeConstant.REVIEW_PASS)
                .originalQuestion(userContribution.getQuestion())
                .finalQuestion(approveDTO.getEditedQuestion() ==  null? userContribution.getQuestion() : approveDTO.getEditedQuestion())
                .originalAnswer(userContribution.getAnswer())
                .finalAnswer(approveDTO.getEditedAnswer() ==  null? userContribution.getAnswer() : approveDTO.getEditedAnswer())
                .createdTime(LocalDateTime.now())
                .build();
        reviewLogMapper.insert(reviewLog);
    }

    @Override
    public void reject(Long id, RejectDTO rejectDTO) {
        //获取并修改用户贡献
        UserContribution userContribution = userContributionMapper.selectById(id);

        //判断状态是否为待审核
        if (!userContribution.getStatus().equals(StatusConstant.WAIT_FOR_REVIEW)){
            throw new ContributionAlreadyReviewedException( "该贡献已审核");
        }
        //更新状态及审核人等信息
        userContribution.setStatus(StatusConstant.REVIEW_REJECT);
        userContribution.setReviewedBy(BaseContext.getCurrentUsername());
        userContribution.setReviewedTime(LocalDateTime.now());
        userContribution.setRejectReason(rejectDTO.getRejectReason());
        userContributionMapper.update(userContribution);
        decrementPendingCount();

        //记录审核日志
        ReviewLog reviewLog=ReviewLog.builder()
                .contributionType( TargetTypeConstant.Review_TYPE_CONTRIBUTION)
                .contributionId(id)
                .reviewer(BaseContext.getCurrentUsername())
                .action( ActionTypeConstant.REVIEW_REJECT)
                .originalQuestion(userContribution.getQuestion())
                .finalQuestion(userContribution.getQuestion())
                .originalAnswer(userContribution.getAnswer())
                .finalAnswer(userContribution.getAnswer())
                .rejectReason(rejectDTO.getRejectReason())
                .createdTime(LocalDateTime.now())
                .build();
                reviewLogMapper.insert(reviewLog);
    }

    @Override
    public void delete(Long id) {
        //获得这个贡献并判断状态是否为未审核
        UserContribution userContribution = userContributionMapper.selectById(id);
        if (!StatusConstant.WAIT_FOR_REVIEW.equals(userContribution.getStatus()))
            throw new ContributionAlreadyReviewedException( "该贡献已审核");
            userContributionMapper.deleteById(id);

    }

    @Override
    @Transactional

    public BatchReviewVO batchReview(BatchReviewDTO dto) {
        /**
         * 批量审核用户贡献
         */
            List<Long> ids = dto.getContributionIds();
            Integer action = dto.getAction();
            String rejectReason = dto.getRejectReason();
            // 参数校验
            if (ids == null || ids.isEmpty()) {
                throw new EmptyContributionListException("请选择要审核的贡献");
            }
            if (action == 2 && (rejectReason == null || rejectReason.trim().isEmpty())) {
                throw new RejectReasonRequiredException("驳回理由不能为空");
            }

            int successCount = 0;
            List<Long> failIds = new ArrayList<>();
            Map<Long, String> failReasons = new HashMap<>();
            //构建空ApproveDTO对象
            ApproveDTO approveDTO = new ApproveDTO();
            //构建RejectDTO对象
            RejectDTO rejectDTO = new RejectDTO();
            rejectDTO.setRejectReason(rejectReason);

            for (Long id : ids) {
                try {
                    if (action == 1) {
                        // 批量通过
                        approve(id, approveDTO);
                    } else if (action == 2) {
                        // 批量驳回
                        reject(id, rejectDTO);
                    } else {
                        throw new InvalidActionException("审核动作无效");
                    }
                    successCount++;
                } catch (ContributionAlreadyReviewedException e) {
                    failIds.add(id);
                    failReasons.put(id, e.getMessage());
                }
            }

            return new BatchReviewVO(successCount, failIds.size(), failIds, failReasons);
        }

    @Override
    public ContributionStatisticsVO getStatistics() {
        Integer pendingCount = userContributionMapper.countByStatus(StatusConstant.WAIT_FOR_REVIEW);
        Integer approvedCount = userContributionMapper.countByStatus(StatusConstant.REVIEW_PASS);
        Integer rejectedCount = userContributionMapper.countByStatus(StatusConstant.REVIEW_REJECT);
        
        return new ContributionStatisticsVO(pendingCount, approvedCount, rejectedCount);
    }

}
