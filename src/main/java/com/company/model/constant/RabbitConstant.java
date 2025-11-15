package com.company.model.constant;

import com.company.model.dto.ResponseQueueInfo;

public final class RabbitConstant {
    // QUEUE
    public static final String STOCK_RESPONSE_QUEUE = "stock-response-queue";


    public static final String DELIVERY_COMPLETED_QUEUE = "delivery-completed-queue";

    // EXCHANGE
    public static final String ORDER_EXCHANGE = "order-exchange";
    public static final String STOCK_EXCHANGE = "stock-exchange";

    // ROUTING KEY
    public static final String STOCK_ROUTING_KEY = "stock.order.created";
    public static final String STOCK_RESULT_ROUTING_KEY = "stock.result";

    public static final String DELIVERY_COMPLETED_ROUTING_KEY = "order.delivered";

    // RESPONSE QUEUE
    public static final ResponseQueueInfo RQI_ORDER_CREATED_RESULT =
            new ResponseQueueInfo(ORDER_EXCHANGE, STOCK_RESULT_ROUTING_KEY);

}
