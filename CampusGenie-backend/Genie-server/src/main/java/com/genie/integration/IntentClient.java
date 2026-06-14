package com.genie.integration;

import com.genie.integration.dto.IntentClientResponse;
import com.genie.properties.IntentProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class IntentClient {
    private final IntentProperties intentProperties;
    private final RestTemplateBuilder restTemplateBuilder;

    public IntentClientResponse recognize(String query) {
        RestTemplate restTemplate = restTemplateBuilder
                .connectTimeout(Duration.ofMillis(intentProperties.getTimeout()))
                .readTimeout(Duration.ofMillis(intentProperties.getTimeout()))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(intentProperties.getToken());

        HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("question", query), headers);
        return restTemplate.postForObject(
                intentProperties.getServiceUrl(),
                request,
                IntentClientResponse.class
        );
    }
}
