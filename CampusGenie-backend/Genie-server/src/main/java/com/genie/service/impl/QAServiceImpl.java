package com.genie.service.impl;

import com.genie.dto.AskRequestDTO;
import com.genie.service.QAService;

import com.genie.service.KnowledgeSearchService;
import com.genie.service.LlmService;
import com.genie.vo.AnswerVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Slf4j
@Service
public class QAServiceImpl implements QAService {
    @Autowired
    private KnowledgeSearchService knowledgeSearchService; // 负责查数据库或ElasticSearch

    @Autowired
    private LlmService llmService; // 负责调用 Python AI 接口

    @Override
    public AnswerVO getAnswer(AskRequestDTO askRequestDTO) {
        String question = askRequestDTO.getQuestion();
        log.info("接收到用户提问: {}", question);
        
        // 1. 数据库检索 
        // 假设 searchKnowledge 方法返回一段拼接好的相关知识文本
        String context = knowledgeSearchService.searchKnowledge(question);

        // 2. 调用大模型/Python AI 后端生成答案
        // 将“用户问题”和“检索到的背景知识”一起发给Python接口 (RAG技术)
        String aiAnswer = llmService.askWithContext(question, context);
        
        // 构造测试数据用于前后端联调
        AnswerVO answerVO = new AnswerVO();
        answerVO.setQuestion(question);
        answerVO.setAnswer(aiAnswer);
        answerVO.setSource("系统知识库");
        answerVO.setUpdatedTime(LocalDateTime.now());
        
        return answerVO;
    }
}

