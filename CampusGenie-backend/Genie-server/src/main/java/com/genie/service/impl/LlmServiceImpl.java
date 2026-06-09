package com.genie.service.impl;

import com.genie.service.LlmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class LlmServiceImpl implements LlmService {
    @Autowired
    private RestTemplate restTemplate;

    // Python 接口运行在本地的 8000 端口
    private final String PYTHON_AI_API_URL = "http://localhost:8000/api/qa/ask";

    @Override
    public Map<String, Object> ask(String question) {
        // 1. 构造请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 2. 构造传给 Python 的 JSON 数据
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("question", question);

        // 3. 发送 POST 请求到 Python 后端
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(PYTHON_AI_API_URL, request, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // 直接返回 Python 的完整 JSON 数据（包含 answer, cost_time, knowledge_id）
                return response.getBody();
            }
        } catch (Exception e) {
            log.error("调用 Python AI 接口失败: ", e);
        }
        
        return null;
    }
}
