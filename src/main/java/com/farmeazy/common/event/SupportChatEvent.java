package com.farmeazy.common.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Objects;

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

    public SupportChatEvent() {
    }

    public SupportChatEvent(Long conversationId, Long messageId, Long recipientUserId,
                            String recipientEmail, String recipientPhone, String senderName,
                            String messageSnippet, String channel, String idempotencyKey) {
        this.conversationId = conversationId;
        this.messageId = messageId;
        this.recipientUserId = recipientUserId;
        this.recipientEmail = recipientEmail;
        this.recipientPhone = recipientPhone;
        this.senderName = senderName;
        this.messageSnippet = messageSnippet;
        this.channel = channel;
        this.idempotencyKey = idempotencyKey;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long conversationId;
        private Long messageId;
        private Long recipientUserId;
        private String recipientEmail;
        private String recipientPhone;
        private String senderName;
        private String messageSnippet;
        private String channel;
        private String idempotencyKey;

        public Builder conversationId(Long conversationId) {
            this.conversationId = conversationId;
            return this;
        }

        public Builder messageId(Long messageId) {
            this.messageId = messageId;
            return this;
        }

        public Builder recipientUserId(Long recipientUserId) {
            this.recipientUserId = recipientUserId;
            return this;
        }

        public Builder recipientEmail(String recipientEmail) {
            this.recipientEmail = recipientEmail;
            return this;
        }

        public Builder recipientPhone(String recipientPhone) {
            this.recipientPhone = recipientPhone;
            return this;
        }

        public Builder senderName(String senderName) {
            this.senderName = senderName;
            return this;
        }

        public Builder messageSnippet(String messageSnippet) {
            this.messageSnippet = messageSnippet;
            return this;
        }

        public Builder channel(String channel) {
            this.channel = channel;
            return this;
        }

        public Builder idempotencyKey(String idempotencyKey) {
            this.idempotencyKey = idempotencyKey;
            return this;
        }

        public SupportChatEvent build() {
            return new SupportChatEvent(conversationId, messageId, recipientUserId,
                    recipientEmail, recipientPhone, senderName, messageSnippet, channel, idempotencyKey);
        }
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getRecipientUserId() {
        return recipientUserId;
    }

    public void setRecipientUserId(Long recipientUserId) {
        this.recipientUserId = recipientUserId;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessageSnippet() {
        return messageSnippet;
    }

    public void setMessageSnippet(String messageSnippet) {
        this.messageSnippet = messageSnippet;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SupportChatEvent that = (SupportChatEvent) o;
        return Objects.equals(conversationId, that.conversationId) &&
                Objects.equals(messageId, that.messageId) &&
                Objects.equals(recipientUserId, that.recipientUserId) &&
                Objects.equals(recipientEmail, that.recipientEmail) &&
                Objects.equals(recipientPhone, that.recipientPhone) &&
                Objects.equals(senderName, that.senderName) &&
                Objects.equals(messageSnippet, that.messageSnippet) &&
                Objects.equals(channel, that.channel) &&
                Objects.equals(idempotencyKey, that.idempotencyKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversationId, messageId, recipientUserId, recipientEmail,
                recipientPhone, senderName, messageSnippet, channel, idempotencyKey);
    }

    @Override
    public String toString() {
        return "SupportChatEvent{" +
                "conversationId=" + conversationId +
                ", messageId=" + messageId +
                ", recipientUserId=" + recipientUserId +
                ", senderName='" + senderName + '\'' +
                ", channel='" + channel + '\'' +
                '}';
    }
}
