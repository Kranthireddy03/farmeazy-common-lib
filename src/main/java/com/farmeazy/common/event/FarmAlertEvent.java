package com.farmeazy.common.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FarmAlertEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long farmId;
    private String farmName;
    private Long userId;
    private String userEmail;
    private String userPhone;
    private String ruleName;
    private String sensorType; // SOIL_MOISTURE, TEMPERATURE, HUMIDITY, etc.
    private Object readingValue;
    private String unit;
    private String severity; // INFO, WARNING, CRITICAL
    private String alertMessage;
    private String idempotencyKey;

    public FarmAlertEvent() {
    }

    public FarmAlertEvent(Long farmId, String farmName, Long userId, String userEmail,
                          String userPhone, String ruleName, String sensorType,
                          Object readingValue, String unit, String severity,
                          String alertMessage, String idempotencyKey) {
        this.farmId = farmId;
        this.farmName = farmName;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.ruleName = ruleName;
        this.sensorType = sensorType;
        this.readingValue = readingValue;
        this.unit = unit;
        this.severity = severity;
        this.alertMessage = alertMessage;
        this.idempotencyKey = idempotencyKey;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long farmId;
        private String farmName;
        private Long userId;
        private String userEmail;
        private String userPhone;
        private String ruleName;
        private String sensorType;
        private Object readingValue;
        private String unit;
        private String severity;
        private String alertMessage;
        private String idempotencyKey;

        public Builder farmId(Long farmId) {
            this.farmId = farmId;
            return this;
        }

        public Builder farmName(String farmName) {
            this.farmName = farmName;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder userEmail(String userEmail) {
            this.userEmail = userEmail;
            return this;
        }

        public Builder userPhone(String userPhone) {
            this.userPhone = userPhone;
            return this;
        }

        public Builder ruleName(String ruleName) {
            this.ruleName = ruleName;
            return this;
        }

        public Builder sensorType(String sensorType) {
            this.sensorType = sensorType;
            return this;
        }

        public Builder readingValue(Object readingValue) {
            this.readingValue = readingValue;
            return this;
        }

        public Builder unit(String unit) {
            this.unit = unit;
            return this;
        }

        public Builder severity(String severity) {
            this.severity = severity;
            return this;
        }

        public Builder alertMessage(String alertMessage) {
            this.alertMessage = alertMessage;
            return this;
        }

        public Builder idempotencyKey(String idempotencyKey) {
            this.idempotencyKey = idempotencyKey;
            return this;
        }

        public FarmAlertEvent build() {
            return new FarmAlertEvent(farmId, farmName, userId, userEmail, userPhone,
                    ruleName, sensorType, readingValue, unit, severity, alertMessage, idempotencyKey);
        }
    }

    public Long getFarmId() {
        return farmId;
    }

    public void setFarmId(Long farmId) {
        this.farmId = farmId;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public Object getReadingValue() {
        return readingValue;
    }

    public void setReadingValue(Object readingValue) {
        this.readingValue = readingValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getAlertMessage() {
        return alertMessage;
    }

    public void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
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
        FarmAlertEvent that = (FarmAlertEvent) o;
        return Objects.equals(farmId, that.farmId) &&
                Objects.equals(farmName, that.farmName) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(userEmail, that.userEmail) &&
                Objects.equals(userPhone, that.userPhone) &&
                Objects.equals(ruleName, that.ruleName) &&
                Objects.equals(sensorType, that.sensorType) &&
                Objects.equals(readingValue, that.readingValue) &&
                Objects.equals(unit, that.unit) &&
                Objects.equals(severity, that.severity) &&
                Objects.equals(alertMessage, that.alertMessage) &&
                Objects.equals(idempotencyKey, that.idempotencyKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(farmId, farmName, userId, userEmail, userPhone, ruleName,
                sensorType, readingValue, unit, severity, alertMessage, idempotencyKey);
    }

    @Override
    public String toString() {
        return "FarmAlertEvent{" +
                "farmId=" + farmId +
                ", farmName='" + farmName + '\'' +
                ", userId=" + userId +
                ", ruleName='" + ruleName + '\'' +
                ", sensorType='" + sensorType + '\'' +
                ", severity='" + severity + '\'' +
                '}';
    }
}
