package com.genie.service.impl;

import com.genie.constant.ActionTypeConstant;
import com.genie.constant.TargetTypeConstant;
import com.genie.context.BaseContext;
import com.genie.dto.ImportOptionDTO;
import com.genie.dto.KnowledgeDTO;
import com.genie.entity.AdminLog;
import com.genie.entity.KnowledgeBase;
import com.genie.mapper.AdminLogMapper;
import com.genie.mapper.KnowledgeBaseMapper;
import com.genie.service.CategoryService;
import com.genie.service.KnowledgeImportService;
import com.genie.parser.KnowledgeParser;
import com.genie.parser.ParserFactory;
import com.genie.vo.ImportErrorVO;
import com.genie.vo.ImportResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class KnowledgeImportServiceImpl implements KnowledgeImportService {

    @Autowired
    private ParserFactory parserFactory;

    @Autowired
    private KnowledgeBaseMapper knowledgeBaseMapper;
    @Autowired
    private AdminLogMapper adminLogMapper;


    @Override
    @Transactional
    public ImportResultVO importKnowledge(MultipartFile file, ImportOptionDTO option) {
        long startTime = System.currentTimeMillis();

        // 1. 解析文件
        KnowledgeParser parser = parserFactory.getParser(file);
        List<KnowledgeDTO> dtoList = parser.parse(file);
        int totalCount = dtoList.size();
        log.info("文件解析完成，共 {} 条数据，策略: {}", totalCount, option.getStrategy());

        List<ImportErrorVO> errors = new ArrayList<>();
        int successCount = 0;

        Long firstInsertId = null;
        Long lastInsertId = null;

        // 2. 根据策略执行
        if ("ALL".equals(option.getStrategy())) {
            // ========== ALL 策略：全部或全不 ==========
            log.info("使用 ALL 策略，开始预校验...");

            // 预校验所有行，收集错误
            List<PreValidationError> preErrors = preValidateAll(dtoList, option);

            if (!preErrors.isEmpty()) {
                // 有错误，全部回滚
                for (PreValidationError pe : preErrors) {
                    errors.add(buildError(pe.rowNum, pe.question, pe.reason));
                }
                successCount = 0;
                log.warn("ALL 策略预校验失败，共 {} 条错误，全部回滚", preErrors.size());
            } else {
                // 全部通过，批量插入
                log.info("ALL 策略预校验通过，开始批量插入...");
                List<Long> insertedIds = batchInsertAll(dtoList);
                successCount = insertedIds.size();
                
                if (!insertedIds.isEmpty()) {
                    firstInsertId = insertedIds.get(0);
                    lastInsertId = insertedIds.get(insertedIds.size() - 1);
                }
                
                log.info("ALL 策略批量插入完成，成功 {} 条，ID范围：{}-{}", 
                        successCount, firstInsertId, lastInsertId);
            }

        } else {
            // ========== ROW 策略：逐行事务（默认） ==========
            log.info("使用 ROW 策略，开始逐行处理...");

            for (int i = 0; i < dtoList.size(); i++) {
                KnowledgeDTO dto = dtoList.get(i);
                int rowNum = i + 2;

                try {
                    // 校验并处理单行
                    List<String> validateErrors = validateAndProcessRow(dto, option);

                    if (!validateErrors.isEmpty()) {
                        errors.add(buildError(rowNum, dto.getQuestion(), String.join("; ", validateErrors)));
                        continue;
                    }

                    //TODO 检查重复


                    // 保存
                    KnowledgeBase entity = convertToEntity(dto);
                    knowledgeBaseMapper.insert(entity);
                    
                    if (firstInsertId == null) {
                        firstInsertId = entity.getId();
                    }
                    lastInsertId = entity.getId();
                    
                    successCount++;

                } catch (Exception e) {
                    log.error("第 {} 行导入失败", rowNum, e);
                    errors.add(buildError(rowNum, dto.getQuestion(), "系统异常：" + e.getMessage()));
                }
            }

            if (firstInsertId != null && lastInsertId != null) {
                log.info("ROW 策略处理完成，成功 {} 条，失败 {} 条，ID范围：{}-{}", 
                        successCount, errors.size(), firstInsertId, lastInsertId);
            } else {
                log.info("ROW 策略处理完成，成功 {} 条，失败 {} 条", successCount, errors.size());
            }
        }

        long endTime = System.currentTimeMillis();
        log.info("导入完成 - 总: {}, 成功: {}, 失败: {}, 耗时: {}ms",
                totalCount, successCount, errors.size(), (endTime - startTime));
        
        StringBuilder detailBuilder = new StringBuilder();
        detailBuilder.append("导入文件：").append(file.getOriginalFilename());
        if (firstInsertId != null && lastInsertId != null) {
            detailBuilder.append("，ID范围：").append(firstInsertId).append("-").append(lastInsertId);
        }
        detailBuilder.append("，成功：").append(successCount).append("条");
        if (!errors.isEmpty()) {
            detailBuilder.append("，失败：").append(errors.size()).append("条");
        }
        
        AdminLog adminLog = AdminLog.builder()
                .adminName(BaseContext.getCurrentUsername())
                .actionType(ActionTypeConstant.BATCH_IMPORT)
                .targetType(TargetTypeConstant.KNOWLEDGE_BASE)
                .detail(detailBuilder.toString())
                .createdTime(LocalDateTime.now())
                .build();
        adminLogMapper.insert(adminLog);
                
        return buildResult(totalCount, successCount, errors);
    }

    /**
     * ALL 策略：预校验所有行
     */
    private List<PreValidationError> preValidateAll(List<KnowledgeDTO> dtoList, ImportOptionDTO option) {
        List<PreValidationError> errors = new ArrayList<>();

        for (int i = 0; i < dtoList.size(); i++) {
            KnowledgeDTO dto = dtoList.get(i);
            int rowNum = i + 2;

            // 复制一份用于校验（避免污染原数据）
            KnowledgeDTO tempDto = new KnowledgeDTO();
            tempDto.setQuestion(dto.getQuestion());
            tempDto.setAnswer(dto.getAnswer());
            tempDto.setCategoryId(dto.getCategoryId());
            tempDto.setSource(dto.getSource());
            tempDto.setStatus(dto.getStatus());

            List<String> validateErrors = validateAndProcessRow(tempDto, option);

            if (!validateErrors.isEmpty()) {
                errors.add(new PreValidationError(rowNum, dto.getQuestion(),
                        String.join("; ", validateErrors)));
                continue;
            }

            // 预校验通过，将分类ID设置回原对象
            dto.setCategoryId(tempDto.getCategoryId());
        }

        return errors;
    }

    /**
     * ALL 策略：批量插入所有行
     */
    private List<Long> batchInsertAll(List<KnowledgeDTO> dtoList) {
        List<Long> insertedIds = new ArrayList<>();
        for (KnowledgeDTO dto : dtoList) {
            KnowledgeBase entity = convertToEntity(dto);
            knowledgeBaseMapper.insert(entity);
            insertedIds.add(entity.getId());
        }
        return insertedIds;
    }

    /**
     * 校验单行数据，并处理分类
     */
    private List<String> validateAndProcessRow(KnowledgeDTO dto, ImportOptionDTO option) {
        List<String> errors = new ArrayList<>();

        // 1. 问题校验
        if (dto.getQuestion() == null || dto.getQuestion().trim().isEmpty()) {
            errors.add("问题不能为空");
        } else if (dto.getQuestion().length() > 200) {
            errors.add("问题长度超过200字符");
        }

        // 2. 答案校验
        if (dto.getAnswer() == null || dto.getAnswer().trim().isEmpty()) {
            errors.add("答案不能为空");
        } else if (dto.getAnswer().length() > 5000) {
            errors.add("答案长度超过5000字符");
        }

        // 3. 分类处理
            if (dto.getCategoryId() == null) {
                errors.add("分类不存在");
            }


        // 4. 来源长度校验
        if (dto.getSource() != null && dto.getSource().length() > 100) {
            errors.add("来源字段超过100字符");
        }

        // 5. 状态默认值
        if (dto.getStatus() == null) {
            dto.setStatus(1);
        } else if (dto.getStatus() != 0 && dto.getStatus() != 1) {
            errors.add("状态值无效，只能为 0 或 1");
        }

        return errors;
    }

    /**
     * 预校验错误内部类
     */
    private static class PreValidationError {
        final int rowNum;
        final String question;
        final String reason;

        PreValidationError(int rowNum, String question, String reason) {
            this.rowNum = rowNum;
            this.question = question;
            this.reason = reason;
        }
    }

    /**
     * 构建错误记录
     */
    private ImportErrorVO buildError(Integer rowNum, String question, String reason) {
        return ImportErrorVO.builder()
                .rowNo(rowNum)
                .question(question != null && question.length() > 50 ? question.substring(0, 50) + "..." : question)
                .reason(reason)
                .build();
    }

    /**
     * DTO 转 Entity
     */
    private KnowledgeBase convertToEntity(KnowledgeDTO dto) {
        KnowledgeBase entity = new KnowledgeBase();
        entity.setQuestion(dto.getQuestion());
        entity.setAnswer(dto.getAnswer());
        entity.setCategoryId(dto.getCategoryId());
        entity.setSource(dto.getSource());
        entity.setStatus(dto.getStatus());
        entity.setCreatedTime(LocalDateTime.now());
        entity.setUpdatedTime(LocalDateTime.now());
        entity.setCreatedBy(BaseContext.getCurrentUsername());
        entity.setUpdatedBy( BaseContext.getCurrentUsername());
        return entity;
    }

    /**
     * 构建返回结果
     */
    private ImportResultVO buildResult(int totalCount, int successCount, List<ImportErrorVO> errors) {
        ImportResultVO result =  ImportResultVO.builder()
                .totalCount(totalCount)
                .successCount(successCount)
                .failCount(errors.size())
                .summary(errors.isEmpty() ? "成功导入 " + successCount + " 条" : "成功导入 " + successCount + " 条，失败 " + errors.size() + " 条")
                .errors(errors)
                .importTime(LocalDateTime.now())
                .build();
        return result;
    }
}