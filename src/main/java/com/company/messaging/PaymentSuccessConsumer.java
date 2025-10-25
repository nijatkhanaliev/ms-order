package com.company.messaging;

import com.company.dao.entity.Order;
import com.company.dao.repository.OrderRepository;
import com.company.exception.NotFoundException;
import com.company.exception.OrderAlreadyCancelledException;
import com.company.model.events.PaymentSuccessEvent;
import com.company.service.impl.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.company.config.RabbitMQConfig.PAYMENT_SUCCESS_QUEUE;
import static com.company.exception.constant.ErrorCode.DATA_NOT_FOUND;
import static com.company.exception.constant.ErrorCode.ORDER_ALREADY_CANCELLED;
import static com.company.exception.constant.ErrorMessage.DATA_NOT_FOUND_MESSAGE;
import static com.company.exception.constant.ErrorMessage.ORDER_ALREADY_CANCELLED_MESSAGE;
import static com.company.model.enums.OrderStatus.CANCELLED;
import static com.company.model.enums.OrderStatus.CONFIRMED;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentSuccessConsumer {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;

    @RabbitListener(queues = PAYMENT_SUCCESS_QUEUE)
    private void consumePaymentSuccess(PaymentSuccessEvent event) {
        log.info("ORDER.CREATED.PAYMENT.SUCCESSFUL, orderId {}", event.getOrderId());
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new NotFoundException(DATA_NOT_FOUND_MESSAGE, DATA_NOT_FOUND));

        if (order.getStatus() == CANCELLED) {
            log.error("Order already cancelled, orderId {}", event.getOrderId());
            throw new OrderAlreadyCancelledException(
                    String.format(ORDER_ALREADY_CANCELLED_MESSAGE, event.getOrderId()),
                    ORDER_ALREADY_CANCELLED
            );
        }

        order.setStatus(CONFIRMED);
        orderRepository.save(order);

        orderEventPublisher.publishOrderConfirmedEvent(order.getId(), order.getUserId());
    }
}
