package com.company.service;

import com.company.common.BaseResultEvent;
import com.company.model.dto.request.OrderRequest;
import com.company.model.dto.response.OrderResponse;

public interface OrderService {

    OrderResponse createOrder(OrderRequest orderRequest, Long userId);

    OrderResponse getOrderDetails(Long orderId, Long userId);

    void cancelOrder(Long orderId, Long userId);

    void processStockOrderCreatedResult(BaseResultEvent event);
}
