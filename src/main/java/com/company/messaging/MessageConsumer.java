package com.company.messaging;

import com.company.model.events.PaymentFailedEvent;
import com.company.model.events.PaymentSuccessEvent;
import com.company.model.events.StockFailedEvent;
import com.company.service.impl.OrderEventConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.company.config.RabbitMQConfig.DELIVERY_COMPLETED_QUEUE;
import static com.company.config.RabbitMQConfig.ORDER_PAYMENT_FAILED_QUEUE;
import static com.company.config.RabbitMQConfig.PAYMENT_SUCCESS_QUEUE;
import static com.company.config.RabbitMQConfig.STOCK_FAILED_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageConsumer {

    private final OrderEventConsumer orderEventConsumer;

    @RabbitListener(queues = STOCK_FAILED_QUEUE)
    private void consumeStockUpdated(StockFailedEvent event) {
        orderEventConsumer.consumeStockUpdated(event);
    }

    @RabbitListener(queues = PAYMENT_SUCCESS_QUEUE)
    private void consumePaymentSuccess(PaymentSuccessEvent event) {
        orderEventConsumer.consumePaymentSuccess(event);
    }


    @RabbitListener(queues = ORDER_PAYMENT_FAILED_QUEUE)
    private void consumePaymentFailed(PaymentFailedEvent event) {
        orderEventConsumer.consumerPaymentFailed(event);
    }

    @RabbitListener(queues = DELIVERY_COMPLETED_QUEUE)
    private void consumeDeliverySuccess(PaymentSuccessEvent event) {
        orderEventConsumer.consumerDeliverySuccess(event);
    }
}
