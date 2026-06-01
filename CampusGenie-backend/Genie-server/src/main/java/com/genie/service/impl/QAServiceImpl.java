package com.genie.service.impl;

import com.genie.dto.AskRequestDTO;
import com.genie.service.QAService;
import com.genie.vo.AnswerVO;
import org.springframework.stereotype.Service;

@Service
public class QAServiceImpl implements QAService {
    @Override
    public AnswerVO getAnswer(AskRequestDTO askRequestDTO){
        return null;
    }
}
