package com.genie.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genie.entity.IntentSchemaItem;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class IntentSchemaLoader {
    private static final int OTHER_UNKNOWN_LABEL_ID = 9;

    private final ObjectMapper objectMapper;

    private Map<Integer, IntentSchemaItem> byLabelId = Collections.emptyMap();

    @PostConstruct
    public void load() {
        ClassPathResource resource = new ClassPathResource("intent_schema.json");
        try (InputStream inputStream = resource.getInputStream()) {
            List<IntentSchemaItem> items = objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<IntentSchemaItem>>() {
                    }
            );
            byLabelId = items.stream()
                    .collect(Collectors.toUnmodifiableMap(IntentSchemaItem::getLabelId, Function.identity()));
            log.info("意图类别配置加载完成，共 {} 项", byLabelId.size());
        } catch (Exception e) {
            byLabelId = Collections.emptyMap();
            log.error("意图类别配置加载失败", e);
        }
    }

    public Optional<IntentSchemaItem> getByLabelId(Integer labelId) {
        if (labelId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(byLabelId.get(labelId));
    }

    public Optional<IntentSchemaItem> getOtherUnknown() {
        return getByLabelId(OTHER_UNKNOWN_LABEL_ID);
    }

    public boolean isLoaded() {
        return !byLabelId.isEmpty();
    }
}
