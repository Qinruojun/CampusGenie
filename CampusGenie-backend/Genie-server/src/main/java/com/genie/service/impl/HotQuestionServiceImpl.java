package com.genie.service.impl;

import com.genie.entity.HotQuestion;
import com.genie.entity.KnowledgeBase;
import com.genie.entity.QueryLog;
import com.genie.mapper.HotQuestionMapper;
import com.genie.mapper.KnowledgeBaseMapper;
import com.genie.mapper.QueryLogMapper;
import com.genie.service.HotQuestionService;
import com.genie.vo.HotQuestionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class HotQuestionServiceImpl  implements HotQuestionService {
    @Autowired
    private HotQuestionMapper hotQuestionMapper;
    @Autowired
    private QueryLogMapper queryLogMapper;
    @Autowired
    private KnowledgeBaseMapper knowledgeBaseMapper;
    
    @Override
    @Transactional
    public void buildHotQuestions(){
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        
        List<HotQuestion> hotQuestions = queryLogMapper.getQueryLogsByTime_to_HotQuestion(startDate, endDate);

        
        if (hotQuestions == null || hotQuestions.isEmpty()) {
            log.info("没有生成热点问题");
            return;
        }
        
        Integer currentVersion = hotQuestionMapper.getMaxVersion() + 1;
        log.info("开始生成热点问题，版本号: {}", currentVersion);
        
        for (int i = 0; i < hotQuestions.size(); i++) {
            HotQuestion hotQuestion = hotQuestions.get(i);
            //TODO后续考虑知识草稿引入
            KnowledgeBase knowledge = knowledgeBaseMapper.selectById(hotQuestion.getKnowledgeId());
            if (knowledge != null) {
                hotQuestion.setNormalizedAnswer(knowledge.getAnswer());
            } else {
                hotQuestion.setNormalizedAnswer("暂无答案");
                log.warn("知识库ID {} 未找到对应答案", hotQuestion.getKnowledgeId());
            }

            hotQuestion.setDisplayQuestion(hotQuestion.getNormalizedQuestion());
            hotQuestion.setStatStartDate(LocalDate.from(startDate));
            hotQuestion.setStatEndDate(LocalDate.from(endDate));
            hotQuestion.setRankNo(i + 1);
            hotQuestion.setVersion(currentVersion);
            hotQuestion.setUpdatedTime(LocalDateTime.now());
        }
        
        hotQuestionMapper.insertBatch(hotQuestions);
        log.info("热点问题生成完成，共{}条，版本号: {}", hotQuestions.size(), currentVersion);
    }

    @Override
    public List<HotQuestionVO> getHotQuestions() {
        //获取最新版本号的的十条问题
        List<HotQuestion> hotQuestions = hotQuestionMapper.selectList(hotQuestionMapper.getMaxVersion());
        List<HotQuestionVO> hotQuestionVOS = new ArrayList<>();
        for (HotQuestion hotQuestion : hotQuestions) {
            HotQuestionVO hotQuestionVO = new HotQuestionVO();
            hotQuestionVO.setQuestion(hotQuestion.getDisplayQuestion());
            hotQuestionVO.setRank(hotQuestion.getRankNo());
            hotQuestionVO.setQueryCount(hotQuestion.getQueryCount());
            hotQuestionVO.setAnswer(hotQuestion.getNormalizedAnswer());
            //趋势判断，先获取上个版本是否有这条热点
            Integer  lastRank = hotQuestionMapper.selectByKnowledgeId_hitPlace_version( hotQuestion.getKnowledgeId(), hotQuestion.getHitPlace(), hotQuestion.getVersion() - 1);
            if (lastRank == null|| lastRank < hotQuestion.getRankNo()) {
                hotQuestionVO.setTrend("up");
            } else if (lastRank.equals(hotQuestion.getRankNo())) {
                hotQuestionVO.setTrend("flat");
            } else   {
                hotQuestionVO.setTrend("down");
            }
            hotQuestionVOS.add(hotQuestionVO);




        }
        return hotQuestionVOS;
    }
}
