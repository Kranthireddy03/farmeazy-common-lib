package com.farmeazy.common.outbox;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "transactional_outbox", indexes = {
        @Index(name = "idx_outbox_status_service", columnList = "status, service_name"),
        @Index(name = "idx_outbox_created", columnList = "created_at")
})
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true, length = 64)
    private String eventId;

    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "aggregate_type", length = 100)
    private String aggregateType;

    @Column(name = "aggregate_id", length = 100)
    private String aggregateId;

    @Column(name = "routing_key", nullable = false, length = 100)
    private String routingKey;

    @Lob
    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OutboxStatus status = OutboxStatus.PENDING;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    public OutboxEvent() {
    }

    public OutboxEvent(Long id, String eventId, String serviceName, String eventType,
                       String aggregateType, String aggregateId, String routingKey,
                       String payload, OutboxStatus status, Integer retryCount,
                       Instant createdAt, Instant publishedAt, String errorMessage) {
        this.id = id;
        this.eventId = eventId;
        this.serviceName = serviceName;
        this.eventType = eventType;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.routingKey = routingKey;
        this.payload = payload;
        this.status = status != null ? status : OutboxStatus.PENDING;
        this.retryCount = retryCount != null ? retryCount : 0;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.publishedAt = publishedAt;
        this.errorMessage = errorMessage;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String eventId;
        private String serviceName;
        private String eventType;
        private String aggregateType;
        private String aggregateId;
        private String routingKey;
        private String payload;
        private OutboxStatus status = OutboxStatus.PENDING;
        private Integer retryCount = 0;
        private Instant createdAt = Instant.now();
        private Instant publishedAt;
        private String errorMessage;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder eventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder serviceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public Builder eventType(String eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder aggregateType(String aggregateType) {
            this.aggregateType = aggregateType;
            return this;
        }

        public Builder aggregateId(String aggregateId) {
            this.aggregateId = aggregateId;
            return this;
        }

        public Builder routingKey(String routingKey) {
            this.routingKey = routingKey;
            return this;
        }

        public Builder payload(String payload) {
            this.payload = payload;
            return this;
        }

        public Builder status(OutboxStatus status) {
            this.status = status;
            return this;
        }

        public Builder retryCount(Integer retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder publishedAt(Instant publishedAt) {
            this.publishedAt = publishedAt;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public OutboxEvent build() {
            return new OutboxEvent(id, eventId, serviceName, eventType, aggregateType,
                    aggregateId, routingKey, payload, status, retryCount, createdAt, publishedAt, errorMessage);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public OutboxStatus getStatus() {
        return status;
    }

    public void setStatus(OutboxStatus status) {
        this.status = status;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OutboxEvent that = (OutboxEvent) o;
        return Objects.equals(id, that.id) || (eventId != null && Objects.equals(eventId, that.eventId));
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId != null ? eventId : id);
    }

    @Override
    public String toString() {
        return "OutboxEvent{" +
                "id=" + id +
                ", eventId='" + eventId + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", eventType='" + eventType + '\'' +
                ", status=" + status +
                ", retryCount=" + retryCount +
                ", createdAt=" + createdAt +
                '}';
    }
}
