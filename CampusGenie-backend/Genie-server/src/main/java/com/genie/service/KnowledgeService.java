package com.genie.service;

import com.genie.dto.KnowledgeDTO;
import com.genie.dto.KnowledgePageQueryDTO;
import com.genie.result.PageResult;
import com.genie.vo.BatchDeleteVO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.io.IOException;
import java.util.List;

public interface KnowledgeService {


    void newKnowledge(@Valid KnowledgeDTO knowledgeDTO);

    void editKnowledge(@Valid KnowledgeDTO knowledgeDTO);

    void deleteKnowledge(Long id);

    void changeStatus(Long id, Integer status);

    PageResult page(@Valid KnowledgePageQueryDTO knowledgePageQueryDTO);

    BatchDeleteVO batchDelete(List<Long> ids);

    void exportToExcel(String keyword, Integer categoryId, Integer status, HttpServletResponse response) throws IOException;



    void downloadExcelTemplate(HttpServletResponse response) throws IOException;

    void downloadJsonTemplate(HttpServletResponse response) throws IOException;
}
