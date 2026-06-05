package com.genie.service;
import java.util.Map;

public interface LlmService {
    Map<String, Object> ask(String question);
}
