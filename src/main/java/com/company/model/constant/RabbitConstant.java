package com.company.model.constant;

public final class RabbitConstant {
    // QUEUE
    public static final String STOCK_FAILED_QUEUE = "stock-failed-queue";
    public static final String ORDER_PAYMENT_FAILED_QUEUE = "order-payment-failed-queue";
    public static final String PAYMENT_SUCCESS_QUEUE = "payment-success-queue";
    public static final String DELIVERY_COMPLETED_QUEUE = "delivery-completed-queue";

    // EXCHANGE
    public static final String ORDER_EXCHANGE = "order-exchange";

    // ROUTING KEY
    public static final String ORDER_ROUTING_KEY = "order.created";
    public static final String STOCK_FAILED_ROUTING_KEY = "stock.failed";
    public static final String PAYMENT_SUCCESS_ROUTING_KEY = "payment.success";
    public static final String ORDER_PAYMENT_FAILED_ROUTING_KEY = "order.payment.failed";
    public static final String ORDER_CONFIRMED_ROUTING_KEY = "order.confirmed";
    public static final String DELIVERY_COMPLETED_ROUTING_KEY = "order.delivered";

}
