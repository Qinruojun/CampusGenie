package com.genie.controller.user;

import com.genie.constant.CodeConstant;
import com.genie.dto.AskRequestDTO;
import com.genie.dto.QaMessageSendDTO;
import com.genie.result.Result;
import com.genie.service.QAService;
import com.genie.service.QaHistoryService;
import com.genie.vo.AnswerVO;
import com.genie.vo.QaConversationVO;
import com.genie.vo.QaMessageVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class QAController {
    @Autowired
    private QAService qaService;
    @Autowired
    private QaHistoryService qaHistoryService;

    @PostMapping("/qa")
    public Result ask(@Valid @RequestBody AskRequestDTO askRequestDTO) {
        AnswerVO answerVO=qaService.getAnswer(askRequestDTO);

        return Result.success(answerVO, CodeConstant.SUCCESS, "获取答案成功");
    }

    @GetMapping("/qa/conversations")
    public Result<List<QaConversationVO>> listConversations() {
        return Result.success(qaHistoryService.listConversations(), CodeConstant.SUCCESS, "查询历史对话成功");
    }

    @PostMapping("/qa/conversations")
    public Result<QaConversationVO> createConversation() {
        return Result.success(qaHistoryService.createConversation(), CodeConstant.SUCCESS, "创建对话成功");
    }

    @GetMapping("/qa/conversations/{id}/messages")
    public Result<List<QaMessageVO>> listMessages(@PathVariable Long id) {
        return Result.success(qaHistoryService.listMessages(id), CodeConstant.SUCCESS, "查询对话消息成功");
    }

    @PostMapping("/qa/conversations/{id}/messages")
    public Result<AnswerVO> sendMessage(@PathVariable Long id, @Valid @RequestBody QaMessageSendDTO qaMessageSendDTO) {
        return Result.success(qaHistoryService.sendMessage(id, qaMessageSendDTO), CodeConstant.SUCCESS, "获取答案成功");
    }
}
