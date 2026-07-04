package com.genie.service.impl;

import com.genie.constant.RedisConstant;
import com.genie.entity.HotQuestion;
import com.genie.entity.KnowledgeBase;
import com.genie.mapper.HotQuestionMapper;
import com.genie.mapper.KnowledgeBaseMapper;
import com.genie.mapper.QueryLogMapper;
import com.genie.service.HotQuestionService;
import com.genie.vo.HotQuestionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class HotQuestionServiceImpl  implements HotQuestionService {
    @Autowired
    private HotQuestionMapper hotQuestionMapper;
    @Autowired
    private QueryLogMapper queryLogMapper;
    @Autowired
    private KnowledgeBaseMapper knowledgeBaseMapper;
    @Autowired
    private RedisTemplate<String,Object>redisTemplate;
    
    @Override
    @Transactional
    public void buildHotQuestions(){
        LocalDateTime startDate = LocalDateTime.now().minusDays(4);
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
            KnowledgeBase knowledge = hotQuestion.getKnowledgeId() == null
                    ? null
                    : knowledgeBaseMapper.selectById(hotQuestion.getKnowledgeId());
            if (knowledge != null) {
                hotQuestion.setNormalizedAnswer(knowledge.getAnswer());
            } else {
                hotQuestion.setNormalizedAnswer("暂无答案");
                if (hotQuestion.getKnowledgeId() != null) {
                    log.warn("知识库ID {} 未找到对应答案", hotQuestion.getKnowledgeId());
                }
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
        // 统计完成后，刷新缓存
        refreshCache();
    }

    @Override
    public List<HotQuestionVO> getHotQuestions() {
        //1.从Redis 缓存读取
        try {
            List<HotQuestionVO> cachedList = (List<HotQuestionVO>) redisTemplate.opsForValue()
                    .get(RedisConstant.HOT_QUESTIONS);
            if (cachedList != null && !cachedList.isEmpty()) {
                log.debug("热点问题从 Redis 缓存读取");
                // 延长TTL（滑动过期）
                redisTemplate.expire(
                        RedisConstant.HOT_QUESTIONS,
                        RedisConstant.HOT_QUESTIONS_TTL,
                        TimeUnit.MINUTES
                );
                return cachedList;
            }
        } catch (Exception e) {
            log.warn("Redis 读取失败，降级到数据库查询", e);
        }
        // 2. 缓存未命中，从数据库查询
        log.debug("热点问题缓存未命中，从数据库查询");
        List<HotQuestionVO> list = getHotQuestionsFromDB();
        if (list != null && !list.isEmpty()) {
            try {
                redisTemplate.opsForValue().set(
                        RedisConstant.HOT_QUESTIONS,
                        list, RedisConstant.HOT_QUESTIONS_TTL,
                        TimeUnit.MINUTES
                );
                log.info("热点问题缓存写入成功，共{}条", list.size());
            } catch (Exception e) {
                log.warn("Redis 写入失败", e);
            }
        }

        return list;

    }
    /**
     * 从数据库查询热点问题
     */
    private List<HotQuestionVO> getHotQuestionsFromDB() {
        List<HotQuestion> hotQuestions = hotQuestionMapper.selectList(hotQuestionMapper.getMaxVersion());
        List<HotQuestionVO> hotQuestionVOS = new ArrayList<>();
        for (HotQuestion hotQuestion : hotQuestions) {
            HotQuestionVO hotQuestionVO = new HotQuestionVO();
            hotQuestionVO.setQuestion(hotQuestion.getDisplayQuestion());
            hotQuestionVO.setRank(hotQuestion.getRankNo());
            hotQuestionVO.setQueryCount(hotQuestion.getQueryCount());
            hotQuestionVO.setAnswer(hotQuestion.getNormalizedAnswer());

            //趋势判断，先获取上个版本是否有这条热点
            //Integer  lastRank = hotQuestionMapper.selectByKnowledgeId_hitPlace_version( hotQuestion.getKnowledgeId(), hotQuestion.getHitPlace(), hotQuestion.getVersion() - 1);
            Integer lastRank =hotQuestionMapper.selectByNormalized_question_version(hotQuestion.getNormalizedQuestion(),hotQuestion.getVersion()-1);
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
    public void refreshCache() {
        List<HotQuestionVO> list = getHotQuestionsFromDB();
        if (list != null && !list.isEmpty()) {
            try {
                redisTemplate.opsForValue().set(
                        RedisConstant.HOT_QUESTIONS,
                        list,
                        RedisConstant.HOT_QUESTIONS_TTL,
                        TimeUnit.MINUTES
                );
                log.info("热点问题缓存刷新成功，共{}条", list.size());
            } catch (Exception e) {
                log.warn("Redis 写入失败", e);
            }
        } else {
            // 如果数据库无数据，删除缓存
            try {
                redisTemplate.delete(RedisConstant.HOT_QUESTIONS);
                log.info("热点问题缓存已清除（数据库无数据）");
            } catch (Exception e) {
                log.warn("Redis 删除失败", e);
            }
        }
    }



}
