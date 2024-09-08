package com.marek.onlinebookstore.mapper;

import com.marek.onlinebookstore.config.MapperConfig;
import com.marek.onlinebookstore.dto.order.OrderItemDto;
import com.marek.onlinebookstore.model.CartItem;
import com.marek.onlinebookstore.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = {CartItemMapper.class, BookMapper.class})
public interface OrderItemMapper {
    OrderItem fromCarttoOrderItem(CartItem cartItem);

    @Mapping(target = "bookId", source = "book.id")
    OrderItemDto toDto(OrderItem orderItem);
}

