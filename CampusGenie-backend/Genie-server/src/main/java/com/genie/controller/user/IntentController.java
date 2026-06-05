package com.genie.controller.user;

import com.genie.constant.CodeConstant;
import com.genie.dto.IntentRecognizeDTO;
import com.genie.result.Result;
import com.genie.service.IntentService;
import com.genie.vo.IntentResultVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user/intent")
@RequiredArgsConstructor
public class IntentController {
    private final IntentService intentService;

    @PostMapping("/recognize")
    public Result<IntentResultVO> recognize(@Valid @RequestBody IntentRecognizeDTO intentRecognizeDTO) {
        IntentResultVO result = intentService.recognize(intentRecognizeDTO);
        return Result.success(result, CodeConstant.SUCCESS, "意图识别完成");
    }
}
