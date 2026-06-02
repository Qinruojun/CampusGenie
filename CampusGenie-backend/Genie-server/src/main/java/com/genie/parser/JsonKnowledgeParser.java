package com.genie.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genie.dto.KnowledgeDTO;
import com.genie.exception.JsonParseException;
import com.genie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class JsonKnowledgeParser implements KnowledgeParser {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private CategoryService categoryService;

    @Override
    public List<KnowledgeDTO> parse(MultipartFile file) {
        List<KnowledgeDTO> list = new ArrayList<>();
        
        try {
            JsonNode root = objectMapper.readTree(file.getInputStream());
            
            // 支持数组格式：[{...}, {...}]
            if (root.isArray()) {
                for (JsonNode node : root) {
                    KnowledgeDTO dto = new KnowledgeDTO();
                    dto.setQuestion(getText(node, "question"));
                    dto.setAnswer(getText(node, "answer"));
                    dto.setCategoryId( categoryService.getIdByName(getText(node, "category")));
                    dto.setSource(getText(node, "source"));
                    dto.setStatus(1);
                    list.add(dto);
                }
            }
            
            log.info("JSON解析完成，共 {} 条数据", list.size());
            
        } catch (Exception e) {
            log.error("JSON解析失败", e);
            throw new JsonParseException("JSON解析失败");
        }
        
        return list;
    }
    
    private String getText(JsonNode node, String field) {
        return node.has(field) ? node.get(field).asText() : null;
    }
    
    @Override
    public boolean supports(String fileType) {
        return "json".equalsIgnoreCase(fileType);
    }
}