package com.genie.service.impl;

import com.genie.constant.ActionTypeConstant;
import com.genie.constant.StatusConstant;
import com.genie.constant.TargetTypeConstant;
import com.genie.context.BaseContext;
import com.genie.dto.KnowledgeDTO;
import com.genie.dto.KnowledgeDraftPageQueryDTO;
import com.genie.entity.*;
import com.genie.exception.ContributionAlreadyReviewedException;
import com.genie.exception.KnowledgeDraftAlreadyReviewedException;
import com.genie.mapper.AdminLogMapper;
import com.genie.mapper.KnowledgeBaseMapper;
import com.genie.mapper.KnowledgeDraftMapper;
import com.genie.mapper.ReviewLogMapper;
import com.genie.result.PageResult;
import com.genie.service.KnowledgeDraftService;
import com.genie.vo.AdminContributionVO;
import com.genie.vo.KnowledgeDraftStatisticsVO;
import com.genie.vo.KnowledgeDraftVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class KnowledgeDraftServiceImpl  implements KnowledgeDraftService {
    @Autowired
    private KnowledgeDraftMapper knowledgeDraftMapper;
    @Autowired
    private ReviewLogMapper reviewLogMapper;
    @Autowired
    private AdminLogMapper adminLogMapper;
    @Autowired
    private KnowledgeBaseMapper knowledgeBaseMapper;
    @Override
    @Transactional
    public void reject(Long id) {
        //获得这个贡献并判断状态是否为未审核
        KnowledgeDraft knowledgeDraft = knowledgeDraftMapper.selectById(id);
        if (!StatusConstant.WAIT_FOR_REVIEW.equals(knowledgeDraft.getStatus()))
            throw new ContributionAlreadyReviewedException( "该知识草稿已审核");
        knowledgeDraftMapper.deleteById(id);
        //记录审核日记
        ReviewLog reviewLog = ReviewLog.builder()
                .contributionType(TargetTypeConstant.Review_TYPE_KNOWLEDGE_DRAFT)
                .contributionId(id)
                .reviewer(knowledgeDraft.getReviewedBy())
                .action(ActionTypeConstant.REVIEW_REJECT)
                .originalQuestion(knowledgeDraft.getQuestion())
                .originalAnswer(knowledgeDraft.getAnswer())
                .createdTime(LocalDateTime.now())
                .build();
        reviewLogMapper.insert(reviewLog);

    }

    @Override
    @Transactional
    public void editKnowledge(KnowledgeDTO knowledgeDTO) {
        //获取旧草稿
        KnowledgeDraft oldKnowledgeDraft = knowledgeDraftMapper.selectById(knowledgeDTO.getId());
        //编写管理员操作前后知识条目细节（json格式）{

        String before = "{\"question\":\"" + oldKnowledgeDraft.getQuestion()
                + "\",\"answer\":\"" + oldKnowledgeDraft.getAnswer()
                + "\",\"categoryId\":" + oldKnowledgeDraft.getCategoryId()
                + ",\"source\":\"" + oldKnowledgeDraft.getSource()
                + "\",\"status\":" + oldKnowledgeDraft
                .getStatus() + "}";
        String after = "{\"question\":\"" + knowledgeDTO.getQuestion()
                + "\",\"answer\":\"" + knowledgeDTO.getAnswer()
                + "\",\"categoryId\":" + knowledgeDTO.getCategoryId()
                + ",\"source\":\"" + knowledgeDTO.getSource()
                + "\",\"status\":" + knowledgeDTO
                .getStatus() + "}";
        String details = "{\"before\":" + before + ",\"after\":" + after + "}";


        //编辑知识草稿并调用mapper更新
        KnowledgeDraft knowledgeDraft = new KnowledgeDraft();
        BeanUtils.copyProperties(knowledgeDTO,knowledgeDraft);
        knowledgeDraftMapper.update(knowledgeDraft);
        //创建管理员操作日志
        AdminLog adminLog = AdminLog.builder()
                .adminName(BaseContext.getCurrentUsername())
                .actionType(ActionTypeConstant.UPDATE)
                .targetType(TargetTypeConstant.KNOWLEDGE_DRAFT)
                .targetId(knowledgeDraft.getId())
                .detail(details)
                .createdTime(LocalDateTime.now())
                .build();
        adminLogMapper.insert(adminLog);
    }

    @Override
    @Transactional
    public void approve(Long id) {
        //获取知识草稿
        KnowledgeDraft knowledgeDraft = knowledgeDraftMapper.selectById(id);
        //判断状态是否为待审核
        if (!knowledgeDraft.getStatus().equals(StatusConstant.WAIT_FOR_REVIEW)){
            throw new KnowledgeDraftAlreadyReviewedException("该知识草稿已审核");
        }
        //更新状态及审核人等信息
        knowledgeDraft.setStatus(StatusConstant.REVIEW_PASS);
        knowledgeDraft.setReviewedBy(BaseContext.getCurrentUsername());
        knowledgeDraft.setReviewedTime(LocalDateTime.now());
        knowledgeDraftMapper.update( knowledgeDraft);
        //合并知识库数据Knowledge_Base
        KnowledgeBase knowledgeBase=KnowledgeBase.builder()
                .question(knowledgeDraft.getQuestion())
                .answer( knowledgeDraft.getAnswer())
                .categoryId(knowledgeDraft.getCategoryId())
                .source("知识草稿")
                .status(StatusConstant.PUBLISHED)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .createdBy(BaseContext.getCurrentUsername())
                .updatedBy(BaseContext.getCurrentUsername())
                .build();
        knowledgeBaseMapper.insert(knowledgeBase);
        //记录审核日志
        ReviewLog reviewLog=ReviewLog.builder()
                .contributionType( TargetTypeConstant.Review_TYPE_KNOWLEDGE_DRAFT)
                .contributionId(id)
                .reviewer(BaseContext.getCurrentUsername())
                .action( ActionTypeConstant.REVIEW_PASS)
                .finalQuestion(knowledgeDraft.getQuestion())
                .finalAnswer(knowledgeDraft.getQuestion())
                .createdTime(LocalDateTime.now())
                .build();
        reviewLogMapper.insert(reviewLog);
    }

    @Override
    public PageResult pageQuery(KnowledgeDraftPageQueryDTO knowledgeDraftPageQueryDTO) {
        PageHelper.startPage(knowledgeDraftPageQueryDTO.getPage(),knowledgeDraftPageQueryDTO.getPageSize());
        Page<KnowledgeDraftVO> page = knowledgeDraftMapper.pageQuery(knowledgeDraftPageQueryDTO);

        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public KnowledgeDraftStatisticsVO getStatistics() {
        Integer pendingCount = knowledgeDraftMapper.countByStatus(StatusConstant.WAIT_FOR_REVIEW);
        Integer approvedCount = knowledgeDraftMapper.countByStatus(StatusConstant.REVIEW_PASS);

        LocalDateTime startOfWeek = LocalDateTime.now().with(java.time.DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0).withNano(0);
        Integer weeklyUpdateCount = knowledgeDraftMapper.countApprovedByReviewedTimeAfter(startOfWeek);

        LocalDateTime startOfLastWeek = startOfWeek.minusWeeks(1);
        LocalDateTime endOfLastWeek = startOfWeek.withHour(0).withMinute(0).withSecond(0).withNano(0);
        Integer lastWeekUpdateCount = knowledgeDraftMapper.countApprovedByReviewedTimeBetween(startOfLastWeek, endOfLastWeek);

        return new KnowledgeDraftStatisticsVO(pendingCount, approvedCount, weeklyUpdateCount, lastWeekUpdateCount);
    }

    @Override
    public KnowledgeDraftVO getDetailById(Long id) {
        return knowledgeDraftMapper.selectDetailById(id);
    }

}
