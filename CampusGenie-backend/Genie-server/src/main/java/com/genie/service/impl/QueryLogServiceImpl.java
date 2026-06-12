package com.genie.service.impl;


import com.genie.entity.QueryLog;
import com.genie.mapper.QueryLogMapper;
import com.genie.service.QueryLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class QueryLogServiceImpl  implements QueryLogService {
    @Autowired
    private QueryLogMapper queryLogMapper;

    @Override
    @Async("queryLogExecutor")
    public void saveQueryLogAsync(String queryText, String normalizedQuery,
                                  int hit, int hitPlace,
                                  Long knowledgeId, Integer responseTime,
                                  String sessionId) {
        try {
            QueryLog queryLog = new QueryLog();
            queryLog.setQueryText(queryText);
            queryLog.setNormalizedQuery(normalizedQuery);
            queryLog.setHit(hit);
            if (hitPlace != -1) {
                queryLog.setHitPlace(hitPlace);
                queryLog.setKnowledgeId(knowledgeId);
            }
            queryLog.setSessionId(sessionId);
            queryLog.setResponseTime(responseTime);
            queryLog.setQueryTime(LocalDateTime.now());
            queryLogMapper.insert(queryLog);
            log.debug("查询日志异步保存成功");
        } catch (Exception e) {
            log.error("查询日志异步保存失败: {}", e.getMessage(), e);
        }
    }
}
