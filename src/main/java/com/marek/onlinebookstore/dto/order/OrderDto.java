package com.marek.onlinebookstore.dto.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public class OrderDto {

    private Long id;
    private Set<OrderItemDto> orderItems;
    private LocalDateTime orderDate;

    @PositiveOrZero
        private BigDecimal total;

    @NotNull
        private String status;

    public OrderDto(Long id, Set<OrderItemDto> orderItems, LocalDateTime orderDate,
                        BigDecimal total, String status) {
        this.id = id;
        this.orderItems = orderItems;
        this.orderDate = orderDate;
        this.total = total;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<OrderItemDto> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(Set<OrderItemDto> orderItems) {
        this.orderItems = orderItems;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
