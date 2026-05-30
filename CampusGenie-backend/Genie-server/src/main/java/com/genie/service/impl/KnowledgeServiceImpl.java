package com.genie.service.impl;

import com.genie.dto.KnowledgeDTO;
import com.genie.entity.KnowledgeBase;
import com.genie.mapper.KnowledgeBaseMapper;
import com.genie.service.KnowledgeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeServiceImpl implements KnowledgeService {
    @Autowired
    private KnowledgeBaseMapper knowledgeBaseMapper;

    @Override
    public void newKnowledge(KnowledgeDTO knowledgeDTO) {

    }

    @Override
    public void editKnowledge(KnowledgeDTO knowledgeDTO) {

    }

    @Override
    public void deleteKnowledge(long id) {

    }

    @Override
    public void enableKnowledge(long id) {

    }

    @Override
    public void disableKnowledge(long id) {

    }
}
