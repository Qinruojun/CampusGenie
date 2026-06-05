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
    public String askWithContext(String question, String context) {
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
            
            // 4. 解析 Python 返回的结果 (假设 Python 返回 {"answer": "xxxx"})
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (String) response.getBody().get("answer");
            }
        } catch (Exception e) {
            log.error("调用 Python AI 接口失败: ", e);
            return "抱歉，AI 思考时遇到了问题（可能 Python 后端未启动）。";
        }
        
        return "抱歉，暂时无法获取答案。";
    }
}

