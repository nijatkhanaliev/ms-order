package com.company.model.mapper;

import com.company.client.InventoryClient;
import com.company.dao.entity.OrderItem;
import com.company.model.dto.request.OrderItemRequest;
import com.company.model.dto.response.OrderItemResponse;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface OrderItemMapper {

    @Mapping(target = "totalPrice", source = "request", qualifiedByName = "getTotalPrice")
    OrderItem toOrderItem(OrderItemRequest request, @Context InventoryClient inventoryClient);

    List<OrderItem> toOrderItems(List<OrderItemRequest> requests, @Context InventoryClient inventoryClient);

    OrderItemResponse toOrderItemResponse(OrderItem orderItem);

    @Named(value = "getTotalPrice")
    default BigDecimal getTotalPrice(OrderItemRequest request, @Context InventoryClient inventoryClient) {
        BigDecimal productPrice = inventoryClient.getProductPriceById(request.getProductId());

        return productPrice
                .multiply(BigDecimal.valueOf(request.getQuantity()));
    }


}
