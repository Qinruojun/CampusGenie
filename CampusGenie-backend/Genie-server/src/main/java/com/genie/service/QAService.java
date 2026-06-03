package com.genie.service;

import com.genie.dto.AskRequestDTO;
import com.genie.vo.AnswerVO;

public interface QAService {
    AnswerVO getAnswer(AskRequestDTO askRequestDTO);
}
