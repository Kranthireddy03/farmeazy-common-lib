package com.farmeazy.common.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Universal event envelope for all FarmEazy asynchronous microservice events.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FarmeazyEvent<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    private String eventId = UUID.randomUUID().toString();

    private String eventType;

    private String sourceService;

    @Builder.Default
    private Instant timestamp = Instant.now();

    private T payload;

    public static <T> FarmeazyEvent<T> of(String eventType, String sourceService, T payload) {
        return FarmeazyEvent.<T>builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .sourceService(sourceService)
                .timestamp(Instant.now())
                .payload(payload)
                .build();
    }
}
