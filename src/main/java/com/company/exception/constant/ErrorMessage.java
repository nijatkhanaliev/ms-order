package com.company.exception.constant;

public interface ErrorMessage {
    String DATA_NOT_FOUND_MESSAGE = "Data not found";
    String EMPTY_ORDER_ITEMS_MESSAGE = "ORDER.ITEM.REQUESTS is empty";
    String ORDER_ALREADY_CANCELLED_MESSAGE = "Order already cancelled, orderId %s";
    String ORDER_CANCELLATION_NOT_ALLOWED_MESSAGE =  "Order cannot be cancelled because it is already %s";
}
