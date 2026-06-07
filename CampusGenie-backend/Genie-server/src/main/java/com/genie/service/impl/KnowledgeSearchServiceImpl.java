package com.genie.service.impl;


import com.genie.mapper.KnowledgeBaseMapper;
import com.genie.entity.KnowledgeBase;
import com.genie.service.KnowledgeSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class KnowledgeSearchServiceImpl implements KnowledgeSearchService {
    
    @Autowired
    private KnowledgeBaseMapper knowledgeBaseMapper;

    @Override
    public String searchKnowledge(String question) {
        return null;
    }
}
