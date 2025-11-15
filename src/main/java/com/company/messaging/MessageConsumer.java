package com.company.messaging;

import com.company.common.BaseResultEvent;
import com.company.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.company.model.constant.RabbitConstant.STOCK_RESPONSE_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageConsumer {

    private final OrderService orderService;

    @RabbitListener(queues = STOCK_RESPONSE_QUEUE)
    private void consumeStockOrderCreatedResult(BaseResultEvent event) {
        log.info("Consuming stock.order.created.result, eventId {}", event.getEventId());
        orderService.processStockOrderCreatedResult(event);
    }



}
