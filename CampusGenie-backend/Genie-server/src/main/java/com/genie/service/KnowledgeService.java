package com.genie.service;

import com.genie.dto.KnowledgeDTO;
import com.genie.dto.KnowledgePageQueryDTO;
import com.genie.result.PageResult;
import jakarta.validation.Valid;

public interface KnowledgeService {


    void newKnowledge(@Valid KnowledgeDTO knowledgeDTO);

    void editKnowledge(@Valid KnowledgeDTO knowledgeDTO);

    void deleteKnowledge(Long id);

    void changeStatus(Long id, Integer status);

    PageResult page(@Valid KnowledgePageQueryDTO knowledgePageQueryDTO);
}
