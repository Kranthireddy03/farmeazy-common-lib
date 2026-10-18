package com.farmeazy.common.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

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

    public NotificationDispatchEvent() {
    }

    public NotificationDispatchEvent(String channel, Long recipientUserId, String recipientAddress,
                                     String subject, String messageContent, String purpose,
                                     String referenceType, String referenceId, String idempotencyKey,
                                     String priority, Map<String, Object> templateData) {
        this.channel = channel;
        this.recipientUserId = recipientUserId;
        this.recipientAddress = recipientAddress;
        this.subject = subject;
        this.messageContent = messageContent;
        this.purpose = purpose;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.idempotencyKey = idempotencyKey;
        this.priority = priority;
        this.templateData = templateData;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String channel;
        private Long recipientUserId;
        private String recipientAddress;
        private String subject;
        private String messageContent;
        private String purpose;
        private String referenceType;
        private String referenceId;
        private String idempotencyKey;
        private String priority;
        private Map<String, Object> templateData;

        public Builder channel(String channel) {
            this.channel = channel;
            return this;
        }

        public Builder recipientUserId(Long recipientUserId) {
            this.recipientUserId = recipientUserId;
            return this;
        }

        public Builder recipientAddress(String recipientAddress) {
            this.recipientAddress = recipientAddress;
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder messageContent(String messageContent) {
            this.messageContent = messageContent;
            return this;
        }

        public Builder purpose(String purpose) {
            this.purpose = purpose;
            return this;
        }

        public Builder referenceType(String referenceType) {
            this.referenceType = referenceType;
            return this;
        }

        public Builder referenceId(String referenceId) {
            this.referenceId = referenceId;
            return this;
        }

        public Builder idempotencyKey(String idempotencyKey) {
            this.idempotencyKey = idempotencyKey;
            return this;
        }

        public Builder priority(String priority) {
            this.priority = priority;
            return this;
        }

        public Builder templateData(Map<String, Object> templateData) {
            this.templateData = templateData;
            return this;
        }

        public NotificationDispatchEvent build() {
            return new NotificationDispatchEvent(channel, recipientUserId, recipientAddress,
                    subject, messageContent, purpose, referenceType, referenceId,
                    idempotencyKey, priority, templateData);
        }
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Long getRecipientUserId() {
        return recipientUserId;
    }

    public void setRecipientUserId(Long recipientUserId) {
        this.recipientUserId = recipientUserId;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Map<String, Object> getTemplateData() {
        return templateData;
    }

    public void setTemplateData(Map<String, Object> templateData) {
        this.templateData = templateData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationDispatchEvent that = (NotificationDispatchEvent) o;
        return Objects.equals(channel, that.channel) &&
                Objects.equals(recipientUserId, that.recipientUserId) &&
                Objects.equals(recipientAddress, that.recipientAddress) &&
                Objects.equals(subject, that.subject) &&
                Objects.equals(messageContent, that.messageContent) &&
                Objects.equals(purpose, that.purpose) &&
                Objects.equals(referenceType, that.referenceType) &&
                Objects.equals(referenceId, that.referenceId) &&
                Objects.equals(idempotencyKey, that.idempotencyKey) &&
                Objects.equals(priority, that.priority) &&
                Objects.equals(templateData, that.templateData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel, recipientUserId, recipientAddress, subject, messageContent,
                purpose, referenceType, referenceId, idempotencyKey, priority, templateData);
    }

    @Override
    public String toString() {
        return "NotificationDispatchEvent{" +
                "channel='" + channel + '\'' +
                ", recipientUserId=" + recipientUserId +
                ", recipientAddress='" + recipientAddress + '\'' +
                ", subject='" + subject + '\'' +
                ", purpose='" + purpose + '\'' +
                ", priority='" + priority + '\'' +
                '}';
    }
}
