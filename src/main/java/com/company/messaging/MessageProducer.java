package com.company.messaging;

import com.company.model.events.OrderCompletedEvent;
import com.company.model.events.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendOrderConfirmedEvent(String exchange, String routingKey, OrderCompletedEvent event) {
        log.info("Sending order confirmed event, eventId {}", event.getEventId());
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }

    public void sendOrderCreatedEvent(String exchange, String routingKey, OrderCreatedEvent event) {
        log.info("Sending order created event, eventId {}", event.getEventId());
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }

}
