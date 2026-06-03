package com.genie.service;

import com.genie.vo.HotQuestionVO;

import java.util.List;

public interface HotQuestionService {
    List<HotQuestionVO> getHotQuestions();//返回10个热点问题列表
}
