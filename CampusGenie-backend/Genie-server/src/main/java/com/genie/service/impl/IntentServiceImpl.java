package com.genie.service.impl;

import com.genie.config.IntentSchemaLoader;
import com.genie.dto.IntentBatchRecognizeDTO;
import com.genie.dto.IntentRecognizeDTO;
import com.genie.entity.IntentSchemaItem;
import com.genie.integration.IntentClient;
import com.genie.integration.dto.IntentClientResponse;
import com.genie.properties.IntentProperties;
import com.genie.service.IntentService;
import com.genie.vo.IntentResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IntentServiceImpl implements IntentService {
    private static final String STATUS_SUCCESS = "success";
    private static final String STATUS_LOW_CONFIDENCE = "low_confidence";
    private static final String STATUS_FALLBACK = "fallback";
    private static final String STATUS_FAILED = "failed";

    private final IntentClient intentClient;
    private final IntentSchemaLoader intentSchemaLoader;
    private final IntentProperties intentProperties;

    @Override
    public IntentResultVO recognize(IntentRecognizeDTO intentRecognizeDTO) {
        String query = intentRecognizeDTO.getQuery().trim();

        IntentClientResponse clientResponse;
        try {
            clientResponse = intentClient.recognize(query);
        } catch (Exception e) {
            log.warn("意图识别服务调用失败，query={}", query, e);
            return buildUnknownResult(query, STATUS_FAILED, BigDecimal.ZERO, "意图识别服务不可用，已降级为通用检索");
        }

        if (clientResponse == null) {
            return buildUnknownResult(query, STATUS_FAILED, BigDecimal.ZERO, "意图识别服务返回为空，已降级为通用检索");
        }

        if (!intentSchemaLoader.isLoaded()) {
            return buildUnknownResult(query, STATUS_FAILED, BigDecimal.ZERO, "意图类别配置不可用，已降级为通用检索");
        }

        Integer labelId = resolveLabelId(clientResponse);
        BigDecimal confidence = Optional.ofNullable(clientResponse.getConfidence()).orElse(BigDecimal.ZERO);
        Optional<IntentSchemaItem> schemaItem = intentSchemaLoader.getByLabelId(labelId);

        if (schemaItem.isEmpty()) {
            log.warn("模型返回非法意图类别，query={}, labelId={}, response={}", query, labelId, clientResponse);
            return buildUnknownResult(query, STATUS_FALLBACK, confidence, "模型返回类别无效，已降级为其他未知");
        }

        if (confidence.compareTo(intentProperties.getThreshold().getLow()) < 0) {
            return buildUnknownResult(query, STATUS_LOW_CONFIDENCE, confidence, "置信度过低，不限定知识库分区");
        }

        IntentResultVO result = buildResult(query, schemaItem.get(), confidence);
        result.setStatus(resolveStatus(confidence));
        result.setMessage(resolveMessage(result.getStatus()));
        return result;
    }

    @Override
    public List<IntentResultVO> batchRecognize(IntentBatchRecognizeDTO intentBatchRecognizeDTO) {
        return intentBatchRecognizeDTO.getItems().stream()
                .map(this::recognize)
                .toList();
    }

    private Integer resolveLabelId(IntentClientResponse clientResponse) {
        if (clientResponse.getCategoryId() != null) {
            return clientResponse.getCategoryId();
        }
        return clientResponse.getIntentId();
    }

    private String resolveStatus(BigDecimal confidence) {
        BigDecimal high = intentProperties.getThreshold().getHigh();
        if (confidence.compareTo(high) >= 0) {
            return STATUS_SUCCESS;
        }
        return STATUS_LOW_CONFIDENCE;
    }

    private String resolveMessage(String status) {
        if (STATUS_SUCCESS.equals(status)) {
            return "识别成功";
        }
        return "置信度较低，不强制限定知识库分区";
    }

    private IntentResultVO buildUnknownResult(String query, String status, BigDecimal confidence, String message) {
        IntentSchemaItem unknown = intentSchemaLoader.getOtherUnknown().orElseGet(this::defaultUnknownSchema);
        IntentResultVO result = buildResult(query, unknown, confidence);
        result.setStatus(status);
        result.setMessage(message);
        return result;
    }

    private IntentSchemaItem defaultUnknownSchema() {
        IntentSchemaItem unknown = new IntentSchemaItem();
        unknown.setLabelId(9);
        unknown.setIntentCode("other_unknown");
        unknown.setIntent("其他未知");
        unknown.setDescription("不属于以上类别或模型无法判断的问题");
        return unknown;
    }

    private IntentResultVO buildResult(String query, IntentSchemaItem schemaItem, BigDecimal confidence) {
        IntentResultVO result = new IntentResultVO();
        result.setQuery(query);
        result.setIntent(schemaItem.getIntent());
        result.setIntentCode(schemaItem.getIntentCode());
        result.setLabelId(schemaItem.getLabelId());
        result.setConfidence(confidence);
        return result;
    }
}
