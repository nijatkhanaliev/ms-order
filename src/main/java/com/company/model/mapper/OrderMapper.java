package com.company.model.mapper;

import com.company.dao.entity.Order;
import com.company.model.dto.response.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {

    @Mapping(target = "orderItemResponses", source = "items")
    OrderResponse toOrderResponse(Order order);

}
