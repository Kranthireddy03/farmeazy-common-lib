package com.farmeazy.common.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long orderId;
    private String orderNumber;
    private Long userId;
    private String userEmail;
    private String userPhone;
    private String orderStatus;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private List<OrderItemDto> items;

    public OrderEvent() {
    }

    public OrderEvent(Long orderId, String orderNumber, Long userId, String userEmail,
                      String userPhone, String orderStatus, BigDecimal totalAmount,
                      String paymentMethod, String paymentStatus, List<OrderItemDto> items) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.orderStatus = orderStatus;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.items = items;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long orderId;
        private String orderNumber;
        private Long userId;
        private String userEmail;
        private String userPhone;
        private String orderStatus;
        private BigDecimal totalAmount;
        private String paymentMethod;
        private String paymentStatus;
        private List<OrderItemDto> items;

        public Builder orderId(Long orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder orderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
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

        public Builder orderStatus(String orderStatus) {
            this.orderStatus = orderStatus;
            return this;
        }

        public Builder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder paymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public Builder paymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
            return this;
        }

        public Builder items(List<OrderItemDto> items) {
            this.items = items;
            return this;
        }

        public OrderEvent build() {
            return new OrderEvent(orderId, orderNumber, userId, userEmail, userPhone,
                    orderStatus, totalAmount, paymentMethod, paymentStatus, items);
        }
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
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

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public List<OrderItemDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDto> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderEvent that = (OrderEvent) o;
        return Objects.equals(orderId, that.orderId) &&
                Objects.equals(orderNumber, that.orderNumber) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(userEmail, that.userEmail) &&
                Objects.equals(userPhone, that.userPhone) &&
                Objects.equals(orderStatus, that.orderStatus) &&
                Objects.equals(totalAmount, that.totalAmount) &&
                Objects.equals(paymentMethod, that.paymentMethod) &&
                Objects.equals(paymentStatus, that.paymentStatus) &&
                Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, orderNumber, userId, userEmail, userPhone,
                orderStatus, totalAmount, paymentMethod, paymentStatus, items);
    }

    @Override
    public String toString() {
        return "OrderEvent{" +
                "orderId=" + orderId +
                ", orderNumber='" + orderNumber + '\'' +
                ", userId=" + userId +
                ", orderStatus='" + orderStatus + '\'' +
                ", totalAmount=" + totalAmount +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrderItemDto implements Serializable {

        private static final long serialVersionUID = 1L;

        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private Long sellerId;

        public OrderItemDto() {
        }

        public OrderItemDto(Long productId, String productName, Integer quantity,
                            BigDecimal unitPrice, Long sellerId) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.sellerId = sellerId;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Long productId;
            private String productName;
            private Integer quantity;
            private BigDecimal unitPrice;
            private Long sellerId;

            public Builder productId(Long productId) {
                this.productId = productId;
                return this;
            }

            public Builder productName(String productName) {
                this.productName = productName;
                return this;
            }

            public Builder quantity(Integer quantity) {
                this.quantity = quantity;
                return this;
            }

            public Builder unitPrice(BigDecimal unitPrice) {
                this.unitPrice = unitPrice;
                return this;
            }

            public Builder sellerId(Long sellerId) {
                this.sellerId = sellerId;
                return this;
            }

            public OrderItemDto build() {
                return new OrderItemDto(productId, productName, quantity, unitPrice, sellerId);
            }
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        public Long getSellerId() {
            return sellerId;
        }

        public void setSellerId(Long sellerId) {
            this.sellerId = sellerId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OrderItemDto that = (OrderItemDto) o;
            return Objects.equals(productId, that.productId) &&
                    Objects.equals(productName, that.productName) &&
                    Objects.equals(quantity, that.quantity) &&
                    Objects.equals(unitPrice, that.unitPrice) &&
                    Objects.equals(sellerId, that.sellerId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(productId, productName, quantity, unitPrice, sellerId);
        }

        @Override
        public String toString() {
            return "OrderItemDto{" +
                    "productId=" + productId +
                    ", productName='" + productName + '\'' +
                    ", quantity=" + quantity +
                    ", unitPrice=" + unitPrice +
                    ", sellerId=" + sellerId +
                    '}';
        }
    }
}
