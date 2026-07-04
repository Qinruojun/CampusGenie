package com.genie.service;

import com.genie.dto.KnowledgeDTO;
import com.genie.dto.KnowledgeDraftPageQueryDTO;
import com.genie.result.PageResult;
import com.genie.vo.KnowledgeDraftStatisticsVO;
import jakarta.validation.Valid;

public interface KnowledgeDraftService {
    void reject(Long id);

    void editKnowledge(@Valid KnowledgeDTO knowledgeDTO);

    void approve(Long id);

    PageResult pageQuery(@Valid KnowledgeDraftPageQueryDTO knowledgeDraftPageQueryDTO);

    KnowledgeDraftStatisticsVO getStatistics();

    com.genie.vo.KnowledgeDraftVO getDetailById(Long id);
}
