package com.farmeazy.common.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Universal event envelope for all FarmEazy asynchronous microservice events.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FarmeazyEvent<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String eventId = UUID.randomUUID().toString();
    private String eventType;
    private String sourceService;
    private Instant timestamp = Instant.now();
    private T payload;

    public FarmeazyEvent() {
    }

    public FarmeazyEvent(String eventId, String eventType, String sourceService, Instant timestamp, T payload) {
        this.eventId = eventId != null ? eventId : UUID.randomUUID().toString();
        this.eventType = eventType;
        this.sourceService = sourceService;
        this.timestamp = timestamp != null ? timestamp : Instant.now();
        this.payload = payload;
    }

    public static <T> Builder<T> builder() {
        return new Builder<T>();
    }

    public static class Builder<T> {
        private String eventId = UUID.randomUUID().toString();
        private String eventType;
        private String sourceService;
        private Instant timestamp = Instant.now();
        private T payload;

        public Builder<T> eventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder<T> eventType(String eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder<T> sourceService(String sourceService) {
            this.sourceService = sourceService;
            return this;
        }

        public Builder<T> timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder<T> payload(T payload) {
            this.payload = payload;
            return this;
        }

        public FarmeazyEvent<T> build() {
            return new FarmeazyEvent<T>(eventId, eventType, sourceService, timestamp, payload);
        }
    }

    public static <T> FarmeazyEvent<T> of(String eventType, String sourceService, T payload) {
        return FarmeazyEvent.<T>builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .sourceService(sourceService)
                .timestamp(Instant.now())
                .payload(payload)
                .build();
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getSourceService() {
        return sourceService;
    }

    public void setSourceService(String sourceService) {
        this.sourceService = sourceService;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FarmeazyEvent<?> that = (FarmeazyEvent<?>) o;
        return Objects.equals(eventId, that.eventId) &&
                Objects.equals(eventType, that.eventType) &&
                Objects.equals(sourceService, that.sourceService) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, eventType, sourceService, timestamp, payload);
    }

    @Override
    public String toString() {
        return "FarmeazyEvent{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", sourceService='" + sourceService + '\'' +
                ", timestamp=" + timestamp +
                ", payload=" + payload +
                '}';
    }
}
