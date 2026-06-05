package com.genie.service;

import com.genie.dto.ImportOptionDTO;
import com.genie.vo.ImportResultVO;
import org.springframework.web.multipart.MultipartFile;

public interface KnowledgeImportService {
    ImportResultVO importKnowledge(MultipartFile file, ImportOptionDTO option);
}
