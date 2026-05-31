package com.genie.service.impl;

import com.genie.constant.ActionTypeConstant;
import com.genie.constant.TargetTypeConstant;
import com.genie.context.BaseContext;
import com.genie.dto.KnowledgeDTO;
import com.genie.entity.AdminLog;
import com.genie.entity.KnowledgeBase;
import com.genie.mapper.AdminLogMapper;
import com.genie.mapper.KnowledgeBaseMapper;
import com.genie.service.KnowledgeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class KnowledgeServiceImpl implements KnowledgeService {
    @Autowired
    private KnowledgeBaseMapper knowledgeBaseMapper;
    @Autowired
    private AdminLogMapper adminLogMapper;

    @Override
    @Transactional
    public void newKnowledge(KnowledgeDTO knowledgeDTO) {
        //TODO 缺少向量检索看是否有相似问题回答待解决
        //插入知识条目
        KnowledgeBase knowledgeBase = new KnowledgeBase();
        BeanUtils.copyProperties(knowledgeDTO,knowledgeBase);
        knowledgeBase.setCreatedBy(BaseContext.getCurrentUsername());
        knowledgeBase.setUpdatedBy(BaseContext.getCurrentUsername());
        knowledgeBase.setCreatedTime(LocalDateTime.now());
        knowledgeBase.setUpdatedTime(LocalDateTime.now());
        knowledgeBaseMapper.insert(knowledgeBase);
        //创建管理员操作日志
        AdminLog adminLog = AdminLog.builder()
                .adminName(BaseContext.getCurrentUsername())
                .actionType(ActionTypeConstant.INSERT)
                .targetType(TargetTypeConstant.KNOWLEDGE_BASE)
                .targetId(knowledgeBase.getId())
                .createdTime(LocalDateTime.now())
                .build();
        adminLogMapper.insert(adminLog);
    }
}
