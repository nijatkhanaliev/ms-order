package com.company.messaging;

import com.company.dao.entity.Order;
import com.company.dao.repository.OrderRepository;
import com.company.exception.NotFoundException;
import com.company.model.events.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.company.config.RabbitMQConfig.DELIVERY_COMPLETED_QUEUE;
import static com.company.exception.constant.ErrorCode.DATA_NOT_FOUND;
import static com.company.exception.constant.ErrorMessage.DATA_NOT_FOUND_MESSAGE;
import static com.company.model.enums.OrderStatus.DELIVERED;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryCompletedConsumer {

    private final OrderRepository orderRepository;

    @RabbitListener(queues = DELIVERY_COMPLETED_QUEUE)
    private void consumePaymentSuccess(PaymentSuccessEvent event) {
        log.info("ORDER.DELIVERY.COMPLETED, orderId {}", event.getOrderId());
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new NotFoundException(DATA_NOT_FOUND_MESSAGE, DATA_NOT_FOUND));

        order.setStatus(DELIVERED);
        orderRepository.save(order);
    }
}
