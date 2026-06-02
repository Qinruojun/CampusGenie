package com.genie.service.impl;

import com.genie.constant.ActionTypeConstant;
import com.genie.constant.TargetTypeConstant;
import com.genie.context.BaseContext;
import com.genie.dto.KnowledgeDTO;
import com.genie.dto.KnowledgePageQueryDTO;
import com.genie.entity.AdminLog;
import com.genie.entity.KnowledgeBase;
import com.genie.exception.KnowledgeBaseStatusException;
import com.genie.mapper.AdminLogMapper;
import com.genie.mapper.KnowledgeBaseMapper;
import com.genie.result.PageResult;
import com.genie.service.KnowledgeService;
import com.genie.vo.KnowledgeVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import jakarta.validation.Valid;
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

    @Override
    @Transactional
    public void editKnowledge(KnowledgeDTO knowledgeDTO) {
        //获取旧知识条目
        KnowledgeBase oldKnowledgeBase = knowledgeBaseMapper.selectById(knowledgeDTO.getId());
        //编写管理员操作前后知识条目细节（json格式）{

        String before = "{\"question\":\"" + oldKnowledgeBase.getQuestion()
                + "\",\"answer\":\"" + oldKnowledgeBase.getAnswer()
                + "\",\"categoryId\":" + oldKnowledgeBase.getCategoryId()
                + ",\"source\":\"" + oldKnowledgeBase.getSource()
                + "\",\"status\":" + oldKnowledgeBase
                .getStatus() + "}";
        String after = "{\"question\":\"" + knowledgeDTO.getQuestion()
                + "\",\"answer\":\"" + knowledgeDTO.getAnswer()
                + "\",\"categoryId\":" + knowledgeDTO.getCategoryId()
                + ",\"source\":\"" + knowledgeDTO.getSource()
                + "\",\"status\":" + knowledgeDTO
                .getStatus() + "}";
        String details = "{\"before\":" + before + ",\"after\":" + after + "}";


        //编辑知识条目并调用mapper更新
        KnowledgeBase knowledgeBase = new KnowledgeBase();
        BeanUtils.copyProperties(knowledgeDTO,knowledgeBase);
        knowledgeBase.setUpdatedBy(BaseContext.getCurrentUsername());
        knowledgeBase.setUpdatedTime(LocalDateTime.now());
        knowledgeBaseMapper.update(knowledgeBase);
        //创建管理员操作日志
        AdminLog adminLog = AdminLog.builder()
                .adminName(BaseContext.getCurrentUsername())
                .actionType(ActionTypeConstant.UPDATE)
                .targetType(TargetTypeConstant.KNOWLEDGE_BASE)
                .targetId(knowledgeBase.getId())
                .detail(details)
                .createdTime(LocalDateTime.now())
                .build();
        adminLogMapper.insert(adminLog);
    }

    @Override
    @Transactional
    public void deleteKnowledge(Long id) {
        //删除知识条目并调用mapper更新
        knowledgeBaseMapper.deleteById(id);
        //创建管理员操作日志
        AdminLog adminLog = AdminLog.builder()
                .adminName(BaseContext.getCurrentUsername())
                .actionType(ActionTypeConstant.DELETE)
                .targetType(TargetTypeConstant.KNOWLEDGE_BASE)
                .targetId(id)
                .createdTime(LocalDateTime.now())
                .build();
                adminLogMapper.insert(adminLog);
    }

    @Override
    public void changeStatus(Long id, Integer status) {
        //获取要修改的知识条目的状态并判断是否与知识库存储状态一致，不一致报错，一致修改
        KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectByIdAndStatus(id,status);
        if(knowledgeBase== null){
            throw new KnowledgeBaseStatusException("知识条目状态异常");
        }
        //修改状态
        knowledgeBase.setStatus(1-status);
        knowledgeBaseMapper.update(knowledgeBase);
        //创建管理员操作日志
        AdminLog adminLog = AdminLog.builder()
                .adminName(BaseContext.getCurrentUsername())
                .actionType(ActionTypeConstant.UPDATE)
                .targetType(TargetTypeConstant.KNOWLEDGE_BASE)
                .targetId(knowledgeBase.getId())
                .detail("{\"before\":{\"status\":" + status + "},\"after\":{\"status\":" + (1-status) + "}}")
                .createdTime(LocalDateTime.now())
                .build();
        adminLogMapper.insert(adminLog);


    }

    @Override
    public PageResult page(@Valid  KnowledgePageQueryDTO knowledgePageQueryDTO) {
        PageHelper.startPage(knowledgePageQueryDTO.getPage(),knowledgePageQueryDTO.getPageSize());
        Page<KnowledgeVO> page = knowledgeBaseMapper.pageQuery(knowledgePageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());

    }
}
