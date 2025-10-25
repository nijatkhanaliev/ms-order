package com.company.messaging;

import com.company.dao.entity.Order;
import com.company.dao.repository.OrderRepository;
import com.company.exception.NotFoundException;
import com.company.model.events.StockFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.company.config.RabbitMQConfig.STOCK_FAILED_QUEUE;
import static com.company.exception.constant.ErrorCode.DATA_NOT_FOUND;
import static com.company.exception.constant.ErrorMessage.DATA_NOT_FOUND_MESSAGE;
import static com.company.model.enums.OrderStatus.STOCK_FAILED;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockFailedConsumer {

    private final OrderRepository orderRepository;

    @RabbitListener(queues = STOCK_FAILED_QUEUE)
    private void consumerStockUpdated(StockFailedEvent event) {
        log.info("Stock updated failed, cancelling order. orderId {}. Message '{}'",
                event.getOrderId(), event.getReason());

        Long orderId = event.getOrderId();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(DATA_NOT_FOUND_MESSAGE, DATA_NOT_FOUND));
        order.setStatus(STOCK_FAILED);
        orderRepository.save(order);
    }

}
