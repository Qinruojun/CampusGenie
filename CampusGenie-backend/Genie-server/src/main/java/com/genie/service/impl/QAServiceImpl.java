package com.genie.service.impl;

import com.genie.context.BaseContext;
import com.genie.dto.AskRequestDTO;
import com.genie.entity.QueryLog;
import com.genie.mapper.QueryLogMapper;
import com.genie.service.LlmService;
import com.genie.service.QAService;
import com.genie.service.KnowledgeSearchService;
import com.genie.vo.AnswerVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service

public class QAServiceImpl implements QAService {
    @Autowired
    private LlmService llmService; // 负责调用 Python AI 接口
    @Autowired
    private QueryLogMapper queryLogMapper;

    @Override
    @Transactional
    public AnswerVO getAnswer(AskRequestDTO askRequestDTO) {
        long startTime = System.currentTimeMillis();
        String question = askRequestDTO.getQuestion();
        log.info("接收到用户提问: {}", question);
        
        // 构造测试数据用于前后端联调
        AnswerVO answerVO = new AnswerVO();
        answerVO.setQuestion(question);
        answerVO.setUpdatedTime(LocalDateTime.now());

        // 1. 调用大模型获取复合结果
        Map<String, Object> pythonResult = llmService.ask(question);

        if (pythonResult != null) {
            String answer = (String) pythonResult.get("answer");
            Object kbIdObj = pythonResult.get("knowledge_id"); // 获取 ID

            answerVO.setAnswer(answer);

            // 如果 ID 不为空，说明命中了本地知识库
            if (kbIdObj != null) {
                // 将获取到的 ID 塞进 VO 返回给前端
                answerVO.setKnowledgeId(Long.valueOf(kbIdObj.toString()));
                answerVO.setSource("系统知识库");
            } else {
                answerVO.setSource("搜索引擎");
            }
        } else {
            answerVO.setAnswer("抱歉，后端 AI 服务响应异常。");
            answerVO.setSource("系统错误");
        }
        
        long endTime = System.currentTimeMillis();
        log.info("耗时: {}ms", endTime - startTime);

        saveQueryLogAsync(question, question, hit, hit_place, 
                         answerVO.getKnowledgeId(), 
                         Math.toIntExact(endTime - startTime));

        return answerVO;
    }

    @Async("queryLogExecutor")
    public void saveQueryLogAsync(String queryText, String normalizedQuery, 
                                  int hit, int hitPlace, 
                                  Long knowledgeId, Integer responseTime) {
        try {
            QueryLog queryLog = new QueryLog();
            queryLog.setQueryText(queryText);
            queryLog.setNormalizedQuery(normalizedQuery);
            queryLog.setHit(hit);
            if(hitPlace != -1) {
                queryLog.setHitPlace(hitPlace);
                queryLog.setKnowledgeId(knowledgeId);
            }
            queryLog.setSessionId(BaseContext.getCurrentUserId().toString());
            queryLog.setResponseTime(responseTime);
            queryLog.setQueryTime(LocalDateTime.now());
            queryLogMapper.insert(queryLog);
            log.debug("查询日志异步保存成功");
        } catch (Exception e) {
            log.error("查询日志异步保存失败: {}", e.getMessage(), e);
        }
    }
}
