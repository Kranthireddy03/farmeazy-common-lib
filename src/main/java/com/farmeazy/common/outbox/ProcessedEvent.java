package com.farmeazy.common.outbox;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "processed_events", indexes = {
        @Index(name = "idx_processed_created", columnList = "processed_at")
})
public class ProcessedEvent {

    @Id
    @Column(name = "event_id", nullable = false, length = 64)
    private String eventId;

    @Column(name = "event_type", length = 100)
    private String eventType;

    @Column(name = "source_service", length = 100)
    private String sourceService;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt = Instant.now();

    public ProcessedEvent() {
    }

    public ProcessedEvent(String eventId, String eventType, String sourceService, Instant processedAt) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.sourceService = sourceService;
        this.processedAt = processedAt != null ? processedAt : Instant.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String eventId;
        private String eventType;
        private String sourceService;
        private Instant processedAt = Instant.now();

        public Builder eventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder eventType(String eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder sourceService(String sourceService) {
            this.sourceService = sourceService;
            return this;
        }

        public Builder processedAt(Instant processedAt) {
            this.processedAt = processedAt;
            return this;
        }

        public ProcessedEvent build() {
            return new ProcessedEvent(eventId, eventType, sourceService, processedAt);
        }
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

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessedEvent that = (ProcessedEvent) o;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(eventId);
    }

    @Override
    public String toString() {
        return "ProcessedEvent{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", sourceService='" + sourceService + '\'' +
                ", processedAt=" + processedAt +
                '}';
    }
}
