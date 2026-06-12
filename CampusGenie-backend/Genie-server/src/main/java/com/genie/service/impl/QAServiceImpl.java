package com.genie.service.impl;

import com.genie.context.BaseContext;
import com.genie.dto.AskRequestDTO;
import com.genie.mapper.KnowledgeDraftMapper;
import com.genie.service.LlmService;
import com.genie.service.QAService;
import com.genie.service.QueryLogService;
import com.genie.vo.AnswerVO;
import com.genie.entity.KnowledgeDraft;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private QueryLogService queryLogService;

    @Autowired
    private KnowledgeDraftMapper knowledgeDraftMapper;

    @Override
    @Transactional
    public AnswerVO getAnswer(AskRequestDTO askRequestDTO) {
        long startTime = System.currentTimeMillis();
        String question = askRequestDTO.getQuestion().trim();
        log.info("接收到用户提问: {}", question);
        
        // 构造测试数据用于前后端联调
        AnswerVO answerVO = new AnswerVO();
        answerVO.setQuestion(question);
        answerVO.setUpdatedTime(LocalDateTime.now());

        // 1. 调用大模型获取复合结果
        Map<String, Object> pythonResult = llmService.ask(question);
        Integer knowledgeDraftId=null;

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
                //TODO 目前知识草稿入库是没有分类的，考虑后续意图识别分支，以及后续是否区分为知识草稿，知识库，搜索引擎三者，现在只区分知识库和搜索引擎
                //创建新的知识草稿并插入
                KnowledgeDraft knowledgeDraft = KnowledgeDraft.builder()
                        .question(question)
                        .answer(answer)
                        .source("搜索引擎")
                        .status(0)
                        .createdTime(LocalDateTime.now())
                        .build();
                knowledgeDraftId= knowledgeDraftMapper.insert(knowledgeDraft);

            }
        }else {
            answerVO.setAnswer("抱歉，后端 AI 服务响应异常。");
            answerVO.setSource("系统错误");
        }

        int hit=0;
        int hit_place=-1;
        String source=answerVO.getSource();
        if("系统知识库".equals( source))
        {
            hit=1;
            hit_place= 0;
        }
        //TODO 缺少知识草稿的检索目前默认一定会命中（直接当成命中新生成的知识草稿的了）
        else if("搜索引擎".equals( source))
        {
            hit=1;
            hit_place= 1;
        }
        
        long endTime = System.currentTimeMillis();
        log.info("耗时: {}ms", endTime - startTime);

       Long knowledgeId=Long.valueOf(0);
        if(answerVO.getKnowledgeId()==null){
            if (knowledgeDraftId != null)
                knowledgeId=Long.valueOf(knowledgeDraftId);
        }
        else knowledgeId=answerVO.getKnowledgeId();


        Long currentUserId = BaseContext.getCurrentUserId();
        String sessionId = currentUserId == null ? "anonymous" : currentUserId.toString();
        queryLogService.saveQueryLogAsync(question, question, hit, hit_place,
                answerVO.getKnowledgeId(),
                Math.toIntExact(endTime - startTime),
                sessionId);

        return answerVO;
    }
}
