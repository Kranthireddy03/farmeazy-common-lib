package com.farmeazy.common.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationDispatchEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String channel; // EMAIL, SMS, PUSH, IN_APP
    private Long recipientUserId;
    private String recipientAddress; // Email address, phone number, or push token
    private String subject;
    private String messageContent;
    private String purpose;
    private String referenceType;
    private String referenceId;
    private String idempotencyKey;
    private String priority; // LOW, NORMAL, HIGH, URGENT
    private Map<String, Object> templateData;
}
