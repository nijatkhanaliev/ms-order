package com.company.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequest {
    @NotNull(message = "Order.Item.Request.product.ID cannot be null")
    private Long productId;
    @NotNull(message = "Order.Item.Request.product.QUANTITY cannot be null")
    private Integer quantity;
}
