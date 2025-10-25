package com.company.service.impl;

import com.company.messaging.OrderConfirmedProducer;
import com.company.model.events.OrderCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.company.config.RabbitMQConfig.ORDER_CONFIRMED_ROUTING_KEY;
import static com.company.config.RabbitMQConfig.ORDER_EXCHANGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final OrderConfirmedProducer orderConfirmedProducer;

    public void publishOrderConfirmedEvent(Long orderId, Long userId) {
        log.info("Publishing order confirmed event. orderId {}", orderId);
        OrderCompletedEvent orderCompletedEvent = new OrderCompletedEvent();
        orderCompletedEvent.setOrderId(orderId);
        orderCompletedEvent.setUserId(userId);

        orderConfirmedProducer.send(ORDER_EXCHANGE, ORDER_CONFIRMED_ROUTING_KEY, orderCompletedEvent);
    }

}
