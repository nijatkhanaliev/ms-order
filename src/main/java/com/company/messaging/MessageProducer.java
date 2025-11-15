package com.company.messaging;

import com.company.common.BaseEvent;
import com.company.dao.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendOrderCreatedEvent(String exchange, String routingKey, BaseEvent<Order> event) {
        log.info("Sending order created event, eventId {}", event.getEventId());
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }

}
