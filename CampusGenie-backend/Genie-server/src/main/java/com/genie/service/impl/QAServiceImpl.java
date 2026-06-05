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

@Slf4j
@Service

public class QAServiceImpl implements QAService {
    @Autowired
    private KnowledgeSearchService knowledgeSearchService;

    @Autowired
    private LlmService llmService;
    @Autowired
    private QueryLogMapper queryLogMapper;

    @Override
    @Transactional
    public AnswerVO getAnswer(AskRequestDTO askRequestDTO) {
        long startTime = System.currentTimeMillis();
        String question = askRequestDTO.getQuestion();
        log.info("接收到用户提问: {}", question);

        String context = knowledgeSearchService.searchKnowledge(question);

        String aiAnswer = llmService.askWithContext(question, context);

        AnswerVO answerVO = new AnswerVO();
        answerVO.setQuestion(question);
        answerVO.setAnswer(aiAnswer);
        answerVO.setUpdatedTime(LocalDateTime.now());
//
//        answerVO.setKnowledgeId(knowledgeBase.getId());
//
//        if (answerVO.getKnowledgeId() != null) {
//            String dbSource = result.getDbSource();
//            answerVO.setSource(dbSource != null ? dbSource : "系统知识库");
//        } else {
//            answerVO.setSource("搜索引擎");
//        }
        
        int hit=0;
        int hit_place=-1;
        String source=answerVO.getSource();
        if("系统知识库".equals( source)||"搜索引擎".equals(source))
        {
            hit=1;
            hit_place= "系统知识库".equals(source)? 0:1;
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
