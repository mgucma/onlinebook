package com.marek.onlinebookstore.mapper;

import com.marek.onlinebookstore.config.MapperConfig;
import com.marek.onlinebookstore.dto.order.OrderDto;
import com.marek.onlinebookstore.model.Order;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class, uses = {OrderItemMapper.class, BookMapper.class})
public interface OrderMapper {

    OrderDto toDto(Order order);
}
