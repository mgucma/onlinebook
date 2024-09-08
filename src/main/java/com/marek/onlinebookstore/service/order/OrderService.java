package com.marek.onlinebookstore.service.order;

import com.marek.onlinebookstore.dto.order.OrderDto;
import com.marek.onlinebookstore.dto.order.OrderItemDto;
import com.marek.onlinebookstore.dto.order.PlacingOrderRequestDto;
import com.marek.onlinebookstore.model.User;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto makeOrder(PlacingOrderRequestDto requestDto);

    List<OrderDto> getHistory(User user, Pageable pageable);

    List<OrderItemDto> getFromOrder(Long orderId);

    OrderItemDto getFromOrder(Long orderId, Long id);

    OrderDto updateStatus(Long id);
}
