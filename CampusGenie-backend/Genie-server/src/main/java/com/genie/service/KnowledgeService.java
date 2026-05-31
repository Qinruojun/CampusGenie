package com.genie.service;

import com.genie.dto.KnowledgeDTO;
import jakarta.validation.Valid;

public interface KnowledgeService {


    void newKnowledge(@Valid KnowledgeDTO knowledgeDTO);
}
