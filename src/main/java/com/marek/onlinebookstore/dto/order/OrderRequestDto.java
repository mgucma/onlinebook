package com.marek.onlinebookstore.dto.order;

import jakarta.validation.constraints.NotNull;
import java.util.Set;

public class OrderRequestDto {

    private Set<OrderItemDto> orderItems;

    @NotNull
    private String status;

    public OrderRequestDto(Set<OrderItemDto> orderItems, String status) {
        this.orderItems = orderItems;
        this.status = status;
    }

    public Set<OrderItemDto> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(Set<OrderItemDto> orderItems) {
        this.orderItems = orderItems;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
