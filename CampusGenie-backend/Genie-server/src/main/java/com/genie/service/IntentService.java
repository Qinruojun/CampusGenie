package com.genie.service;

import com.genie.dto.IntentBatchRecognizeDTO;
import com.genie.dto.IntentRecognizeDTO;
import com.genie.vo.IntentResultVO;

import java.util.List;

public interface IntentService {
    IntentResultVO recognize(IntentRecognizeDTO intentRecognizeDTO);

    List<IntentResultVO> batchRecognize(IntentBatchRecognizeDTO intentBatchRecognizeDTO);
}
