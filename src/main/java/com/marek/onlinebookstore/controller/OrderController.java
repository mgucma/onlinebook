package com.marek.onlinebookstore.controller;

import com.marek.onlinebookstore.dto.order.OrderDto;
import com.marek.onlinebookstore.dto.order.OrderItemDto;
import com.marek.onlinebookstore.dto.order.PlacingOrderRequestDto;
import com.marek.onlinebookstore.model.User;
import com.marek.onlinebookstore.service.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "Make Order",
            description = "make order for user with your shipping address")
    public OrderDto makeOrder(@RequestBody @Valid PlacingOrderRequestDto requestDto) {
        return orderService.makeOrder(requestDto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "get order history",
            description = "get your all orders")
    public List<OrderDto> getOrderHistory(Authentication authentication, Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        return orderService.getHistory(user, pageable);
    }

    @GetMapping("/{orderId}/items")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "get all items",
            description = "get all items from your order")
    public List<OrderItemDto> getItemsFromOrder(@PathVariable Long orderId) {
        return orderService.getFromOrder(orderId);
    }

    @GetMapping("/{orderId}/items/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "get one item",
            description = "get one item from your order")
    public OrderItemDto getOneItemFromOrder(@PathVariable Long orderId, @PathVariable Long id) {
        return orderService.getFromOrder(orderId, id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @Operation(summary = "update order status",
            description = "change status for your order")
    public OrderDto updateOrderStatus(@PathVariable Long id) {
        return orderService.updateStatus(id);
    }
}

