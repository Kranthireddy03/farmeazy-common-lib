package com.farmeazy.common.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnClass(RabbitTemplate.class)
public class RabbitMQConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        template.setChannelTransacted(false);
        return template;
    }

    // =========================================================================
    // EXCHANGES
    // =========================================================================

    @Bean
    public TopicExchange eventsExchange() {
        return ExchangeBuilder.topicExchange(MessageTopics.EXCHANGE_EVENTS)
                .durable(true)
                .build();
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder.directExchange(MessageTopics.EXCHANGE_DLX)
                .durable(true)
                .build();
    }

    // =========================================================================
    // DEAD LETTER QUEUE (DLQ)
    // =========================================================================

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(MessageTopics.QUEUE_DLQ).build();
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(MessageTopics.QUEUE_DLQ);
    }

    // Helper arguments for Dead Letter Routing
    private Map<String, Object> queueArgumentsWithDlq() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", MessageTopics.EXCHANGE_DLX);
        args.put("x-dead-letter-routing-key", MessageTopics.QUEUE_DLQ);
        return args;
    }

    // =========================================================================
    // QUEUES & BINDINGS
    // =========================================================================

    // 1. Notification Queue
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(MessageTopics.QUEUE_NOTIFICATION)
                .withArguments(queueArgumentsWithDlq())
                .build();
    }

    @Bean
    public Binding notificationDispatchBinding(Queue notificationQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(notificationQueue).to(eventsExchange).with(MessageTopics.ROUTING_NOTIFICATION_ANY);
    }

    @Bean
    public Binding notificationOrderBinding(Queue notificationQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(notificationQueue).to(eventsExchange).with(MessageTopics.ROUTING_ORDER_ANY);
    }

    @Bean
    public Binding notificationPaymentBinding(Queue notificationQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(notificationQueue).to(eventsExchange).with(MessageTopics.ROUTING_PAYMENT_ANY);
    }

    @Bean
    public Binding notificationFarmBinding(Queue notificationQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(notificationQueue).to(eventsExchange).with(MessageTopics.ROUTING_FARM_ANY);
    }

    @Bean
    public Binding notificationSupportBinding(Queue notificationQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(notificationQueue).to(eventsExchange).with(MessageTopics.ROUTING_SUPPORT_ANY);
    }

    // 2. Order Queue (consumes payment events to update order status)
    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable(MessageTopics.QUEUE_ORDER)
                .withArguments(queueArgumentsWithDlq())
                .build();
    }

    @Bean
    public Binding orderPaymentCompletedBinding(Queue orderQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(orderQueue).to(eventsExchange).with(MessageTopics.KEY_PAYMENT_COMPLETED);
    }

    @Bean
    public Binding orderPaymentFailedBinding(Queue orderQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(orderQueue).to(eventsExchange).with(MessageTopics.KEY_PAYMENT_FAILED);
    }

    // 3. Payment Queue (consumes refund / order cancellation events)
    @Bean
    public Queue paymentQueue() {
        return QueueBuilder.durable(MessageTopics.QUEUE_PAYMENT)
                .withArguments(queueArgumentsWithDlq())
                .build();
    }

    @Bean
    public Binding paymentOrderCancelledBinding(Queue paymentQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(paymentQueue).to(eventsExchange).with(MessageTopics.KEY_ORDER_CANCELLED);
    }

    // 4. Inventory Queue (product service reacts to order creation / cancellation)
    @Bean
    public Queue inventoryQueue() {
        return QueueBuilder.durable(MessageTopics.QUEUE_INVENTORY)
                .withArguments(queueArgumentsWithDlq())
                .build();
    }

    @Bean
    public Binding inventoryOrderCreatedBinding(Queue inventoryQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(inventoryQueue).to(eventsExchange).with(MessageTopics.KEY_ORDER_CREATED);
    }

    @Bean
    public Binding inventoryOrderCancelledBinding(Queue inventoryQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(inventoryQueue).to(eventsExchange).with(MessageTopics.KEY_ORDER_CANCELLED);
    }
}
