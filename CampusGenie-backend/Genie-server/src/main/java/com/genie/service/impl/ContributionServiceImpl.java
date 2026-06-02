package com.genie.service.impl;

import com.genie.constant.ActionTypeConstant;
import com.genie.constant.StatusConstant;
import com.genie.constant.TargetTypeConstant;
import com.genie.context.BaseContext;
import com.genie.dto.*;
import com.genie.entity.KnowledgeBase;
import com.genie.entity.ReviewLog;
import com.genie.entity.UserContribution;
import com.genie.exception.ContributionAlreadyReviewedException;
import com.genie.mapper.KnowledgeBaseMapper;
import com.genie.mapper.ReviewLogMapper;
import com.genie.mapper.UserContributionMapper;
import com.genie.result.PageResult;
import com.genie.service.ContributionService;
import com.genie.vo.AdminContributionVO;
import com.genie.vo.UserContributionVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContributionServiceImpl implements ContributionService {
    @Autowired
    private UserContributionMapper userContributionMapper;
    @Autowired
    private KnowledgeBaseMapper knowledgeBaseMapper;
    @Autowired
    private ReviewLogMapper reviewLogMapper;
    @Override
    public void contribute(ContributionSubmitDTO contributionSubmitDTO){
        //TODO 是否还需校验（敏感词？非空上层校验了）
        //创建实体并插入数据库
        UserContribution userContribution = new UserContribution();
        BeanUtils.copyProperties(contributionSubmitDTO,userContribution);
        userContribution.setUserId(BaseContext.getCurrentUserId());
        userContribution.setStatus(StatusConstant.WAIT_FOR_REVIEW);
        userContribution.setCreatedTime(LocalDateTime.now());
        userContributionMapper.insert(userContribution);
    }

    @Override
    public PageResult pageQueryByUser(ContributionPageQueryDTO contributionPageQueryDTO) {
        PageHelper.startPage(contributionPageQueryDTO.getPage(),contributionPageQueryDTO.getPageSize());
        Long userId = BaseContext.getCurrentUserId();
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
}
