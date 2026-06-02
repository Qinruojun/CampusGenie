package com.genie.parser;

import com.genie.dto.KnowledgeDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface KnowledgeParser {
    List<KnowledgeDTO> parse(MultipartFile file);
    boolean supports(String fileType);
}