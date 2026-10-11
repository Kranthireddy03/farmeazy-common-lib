package com.farmeazy.common.messaging;

public final class MessageTopics {

    private MessageTopics() {}

    // Exchange
    public static final String EXCHANGE_EVENTS = "farmeazy.events.exchange";
    public static final String EXCHANGE_DLX = "farmeazy.dlx";

    // Queues
    public static final String QUEUE_NOTIFICATION = "farmeazy.notification.queue";
    public static final String QUEUE_ORDER = "farmeazy.order.queue";
    public static final String QUEUE_PAYMENT = "farmeazy.payment.queue";
    public static final String QUEUE_INVENTORY = "farmeazy.inventory.queue";
    public static final String QUEUE_DLQ = "farmeazy.dlq";

    // Routing Key Patterns / Keys
    public static final String ROUTING_NOTIFICATION_ANY = "notification.#";
    public static final String ROUTING_ORDER_ANY = "order.#";
    public static final String ROUTING_PAYMENT_ANY = "payment.#";
    public static final String ROUTING_FARM_ANY = "farm.#";
    public static final String ROUTING_SUPPORT_ANY = "support.#";

    // Specific Routing Keys
    public static final String KEY_ORDER_CREATED = "order.created";
    public static final String KEY_ORDER_STATUS_UPDATED = "order.status.updated";
    public static final String KEY_ORDER_CANCELLED = "order.cancelled";

    public static final String KEY_PAYMENT_COMPLETED = "payment.completed";
    public static final String KEY_PAYMENT_FAILED = "payment.failed";
    public static final String KEY_PAYMENT_REFUNDED = "payment.refunded";

    public static final String KEY_NOTIFICATION_DISPATCH = "notification.dispatch";
    public static final String KEY_FARM_ALERT = "farm.alert";
    public static final String KEY_SUPPORT_CHAT = "support.chat";
    public static final String KEY_USER_REGISTERED = "user.registered";
}
