package com.genie.service;

import com.genie.vo.HotQuestionVO;

import java.util.List;

public interface HotQuestionService {
    void buildHotQuestions();//建立热点前10问题

    List<HotQuestionVO> getHotQuestions();
}
