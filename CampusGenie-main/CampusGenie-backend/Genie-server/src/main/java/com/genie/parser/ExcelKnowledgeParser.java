package com.genie.parser;

import com.genie.dto.KnowledgeDTO;
import com.genie.exception.ExcelParseException;
import com.genie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ExcelKnowledgeParser implements KnowledgeParser {
    @Autowired
    private CategoryService categoryService;
    @Override
    public List<KnowledgeDTO> parse(MultipartFile file) {
        List<KnowledgeDTO> list = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // 从第2行开始读取（第1行是表头）
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                KnowledgeDTO dto = new KnowledgeDTO();
                dto.setQuestion(getCellValue(row.getCell(0)));   // A列：问题
                dto.setAnswer(getCellValue(row.getCell(1)));     // B列：答案
                dto.setCategoryId(categoryService.getIdByName(getCellValue(row.getCell(2))));// C列：分类
                dto.setSource(getCellValue(row.getCell(3)));     // D列：来源
                dto.setStatus(1);
                list.add(dto);
            }
            
            log.info("Excel解析完成，共 {} 条数据", list.size());
            
        } catch (Exception e) {
            log.error("Excel解析失败", e);
            throw new ExcelParseException( "Excel解析失败");
        }
        
        return list;
    }
    
    private String getCellValue(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> null;
        };
    }
    

    
    @Override
    public boolean supports(String fileType) {
        return "xlsx".equalsIgnoreCase(fileType) || "xls".equalsIgnoreCase(fileType);
    }
}