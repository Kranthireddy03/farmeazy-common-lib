package com.farmeazy.common.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long paymentId;
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String paymentMethod;
    private String razorpayPaymentId;
    private String razorpayOrderId;
    private String failureReason;
    private String refundId;

    public PaymentEvent() {
    }

    public PaymentEvent(Long paymentId, Long orderId, Long userId, BigDecimal amount,
                        String currency, String status, String paymentMethod,
                        String razorpayPaymentId, String razorpayOrderId,
                        String failureReason, String refundId) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.razorpayPaymentId = razorpayPaymentId;
        this.razorpayOrderId = razorpayOrderId;
        this.failureReason = failureReason;
        this.refundId = refundId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long paymentId;
        private Long orderId;
        private Long userId;
        private BigDecimal amount;
        private String currency;
        private String status;
        private String paymentMethod;
        private String razorpayPaymentId;
        private String razorpayOrderId;
        private String failureReason;
        private String refundId;

        public Builder paymentId(Long paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public Builder orderId(Long orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder paymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public Builder razorpayPaymentId(String razorpayPaymentId) {
            this.razorpayPaymentId = razorpayPaymentId;
            return this;
        }

        public Builder razorpayOrderId(String razorpayOrderId) {
            this.razorpayOrderId = razorpayOrderId;
            return this;
        }

        public Builder failureReason(String failureReason) {
            this.failureReason = failureReason;
            return this;
        }

        public Builder refundId(String refundId) {
            this.refundId = refundId;
            return this;
        }

        public PaymentEvent build() {
            return new PaymentEvent(paymentId, orderId, userId, amount, currency, status,
                    paymentMethod, razorpayPaymentId, razorpayOrderId, failureReason, refundId);
        }
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentEvent that = (PaymentEvent) o;
        return Objects.equals(paymentId, that.paymentId) &&
                Objects.equals(orderId, that.orderId) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(currency, that.currency) &&
                Objects.equals(status, that.status) &&
                Objects.equals(paymentMethod, that.paymentMethod) &&
                Objects.equals(razorpayPaymentId, that.razorpayPaymentId) &&
                Objects.equals(razorpayOrderId, that.razorpayOrderId) &&
                Objects.equals(failureReason, that.failureReason) &&
                Objects.equals(refundId, that.refundId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentId, orderId, userId, amount, currency, status,
                paymentMethod, razorpayPaymentId, razorpayOrderId, failureReason, refundId);
    }

    @Override
    public String toString() {
        return "PaymentEvent{" +
                "paymentId=" + paymentId +
                ", orderId=" + orderId +
                ", userId=" + userId +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", status='" + status + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}
