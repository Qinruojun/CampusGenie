package com.genie.service;

public interface QueryLogService {
    void saveQueryLogAsync(String queryText, String normalizedQuery,
                           int hit, int hitPlace,
                           Long knowledgeId, Integer responseTime,
                           String sessionId);
}
