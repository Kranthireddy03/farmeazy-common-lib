package com.farmeazy.common.outbox;

import com.farmeazy.common.event.FarmeazyEvent;
import com.farmeazy.common.messaging.MessageTopics;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionalOutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @org.springframework.beans.factory.annotation.Value("${spring.application.name:UNKNOWN_SERVICE}")
    private String applicationName;

    /**
     * Atomically records the event into the outbox table and attempts immediate dispatch to RabbitMQ.
     * If the broker is unreachable, the event remains PENDING in the outbox table to be retried safely.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public <T> OutboxEvent saveAndPublish(FarmeazyEvent<T> event, String routingKey, String aggregateType, String aggregateId) {
        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(event);
        } catch (Exception e) {
            log.error("Failed to serialize outbox event: {}", event.getEventId(), e);
            throw new RuntimeException("Outbox serialization failure", e);
        }

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .eventId(event.getEventId())
                .serviceName(applicationName)
                .eventType(event.getEventType())
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .routingKey(routingKey)
                .payload(payloadJson)
                .status(OutboxStatus.PENDING)
                .createdAt(Instant.now())
                .build();

        outboxEvent = outboxEventRepository.save(outboxEvent);

        // Dispatch only committed records. Publishing here could deliver a notification or
        // financial event even if the enclosing business transaction subsequently rolls back.
        // The scheduled dispatcher reads this record after commit.

        return outboxEvent;
    }

    /**
     * Safe dispatch method that never rolls back the caller transaction if RabbitMQ is temporarily down.
     */
    public void tryPublishEvent(OutboxEvent outboxEvent, Object eventPayload) {
        try {
            rabbitTemplate.convertAndSend(MessageTopics.EXCHANGE_EVENTS, outboxEvent.getRoutingKey(), eventPayload);
            outboxEvent.setStatus(OutboxStatus.PUBLISHED);
            outboxEvent.setPublishedAt(Instant.now());
            outboxEvent.setErrorMessage(null);
            outboxEventRepository.save(outboxEvent);
            log.info("Successfully published outbox event: id={}, type={}, routingKey={}",
                    outboxEvent.getEventId(), outboxEvent.getEventType(), outboxEvent.getRoutingKey());
        } catch (Exception e) {
            outboxEvent.setRetryCount(outboxEvent.getRetryCount() + 1);
            outboxEvent.setErrorMessage(e.getMessage() != null && e.getMessage().length() > 900
                    ? e.getMessage().substring(0, 900) : e.getMessage());
            outboxEventRepository.save(outboxEvent);
            log.warn("RabbitMQ dispatch deferred for outbox event {}: {}. Kept in PENDING state.",
                    outboxEvent.getEventId(), e.getMessage());
        }
    }

    /**
     * Periodic background poller to publish pending outbox events when RabbitMQ is recovered.
     */
    @Scheduled(fixedDelay = 10000, initialDelay = 15000)
    @Transactional
    public void retryPendingOutboxEvents() {
        try {
            List<OutboxEvent> pending = outboxEventRepository.findPendingEventsForUpdate(PageRequest.of(0, 50));
            if (!pending.isEmpty()) {
                log.debug("Found {} pending outbox events to dispatch", pending.size());
                for (OutboxEvent outboxEvent : pending) {
                    try {
                        Object event = objectMapper.readValue(outboxEvent.getPayload(), FarmeazyEvent.class);
                        tryPublishEvent(outboxEvent, event);
                    } catch (Exception parseEx) {
                        outboxEvent.setRetryCount(outboxEvent.getRetryCount() + 1);
                        outboxEvent.setStatus(OutboxStatus.FAILED);
                        outboxEvent.setErrorMessage("Payload parse error: " + parseEx.getMessage());
                        outboxEventRepository.save(outboxEvent);
                    }
                }
            }
        } catch (Exception ex) {
            log.debug("Outbox retry scheduled cycle skipped or deferred: {}", ex.getMessage());
        }
    }

    /**
     * Checks if an event has already been processed by this service (Consumer Idempotency).
     */
    @Transactional(readOnly = true)
    public boolean isEventProcessed(String eventId) {
        if (eventId == null || eventId.isBlank()) {
            return false;
        }
        return processedEventRepository.existsByEventId(eventId);
    }

    /**
     * Marks an event as processed to prevent duplicate executions.
     */
    @Transactional
    public void markEventProcessed(String eventId, String eventType, String sourceService) {
        if (eventId == null || eventId.isBlank()) {
            return;
        }
        if (!processedEventRepository.existsByEventId(eventId)) {
            ProcessedEvent record = ProcessedEvent.builder()
                    .eventId(eventId)
                    .eventType(eventType)
                    .sourceService(sourceService)
                    .processedAt(Instant.now())
                    .build();
            processedEventRepository.save(record);
        }
    }
}
