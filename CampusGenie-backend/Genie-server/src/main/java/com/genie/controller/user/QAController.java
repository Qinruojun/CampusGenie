package com.genie.controller.user;

import com.genie.constant.CodeConstant;
import com.genie.constant.MessageConstant;
import com.genie.dto.AskRequestDTO;
import com.genie.result.Result;
import com.genie.service.QAService;
import com.genie.vo.AnswerVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
public class QAController {
    @Autowired
    private QAService qaService;
    @PostMapping("/qa")
    public Result ask(@Valid @RequestBody AskRequestDTO askRequestDTO) {
        AnswerVO answerVO=qaService.getAnswer(askRequestDTO);

        return Result.success(answerVO.getAnswer(), CodeConstant.SUCCESS, "获取答案成功");
    }
}
