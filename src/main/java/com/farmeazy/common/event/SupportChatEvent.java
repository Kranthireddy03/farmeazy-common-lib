package com.farmeazy.common.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupportChatEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long conversationId;
    private Long messageId;
    private Long recipientUserId;
    private String recipientEmail;
    private String recipientPhone;
    private String senderName;
    private String messageSnippet;
    private String channel; // EMAIL or SMS
    private String idempotencyKey;
}
