package com.marek.onlinebookstore.mapper;

import com.marek.onlinebookstore.config.MapperConfig;
import com.marek.onlinebookstore.dto.order.OrderDto;
import com.marek.onlinebookstore.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = {OrderItemMapper.class, BookMapper.class})
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    OrderDto toDto(Order order);
}
