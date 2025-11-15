package com.company.model.constant;

import com.company.model.enums.OrderStatus;

import java.util.Set;

public class OrderConstant {

    public static final Set<OrderStatus> NON_CANCELLABLE_STATUSES = Set.of(
            OrderStatus.CANCELLED,
            OrderStatus.DELIVERED,
            OrderStatus.SHIPPED,
            OrderStatus.STOCK_FAILED,
            OrderStatus.PAYMENT_FAILED
    );

}
