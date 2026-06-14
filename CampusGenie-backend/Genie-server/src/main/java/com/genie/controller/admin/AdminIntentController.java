package com.genie.controller.admin;

import com.genie.constant.CodeConstant;
import com.genie.dto.IntentBatchRecognizeDTO;
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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/intent")
@RequiredArgsConstructor
public class AdminIntentController {
    private final IntentService intentService;

    @PostMapping("/batch-recognize")
    public Result<List<IntentResultVO>> batchRecognize(@Valid @RequestBody IntentBatchRecognizeDTO intentBatchRecognizeDTO) {
        List<IntentResultVO> results = intentService.batchRecognize(intentBatchRecognizeDTO);
        return Result.success(results, CodeConstant.SUCCESS, "批量识别完成");
    }
}
