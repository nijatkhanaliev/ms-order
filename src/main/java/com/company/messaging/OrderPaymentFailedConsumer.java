package com.company.messaging;

import com.company.dao.entity.Order;
import com.company.dao.repository.OrderRepository;
import com.company.exception.NotFoundException;
import com.company.model.events.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.company.config.RabbitMQConfig.ORDER_PAYMENT_FAILED_QUEUE;
import static com.company.exception.constant.ErrorCode.DATA_NOT_FOUND;
import static com.company.exception.constant.ErrorMessage.DATA_NOT_FOUND_MESSAGE;
import static com.company.model.enums.OrderStatus.PAYMENT_FAILED;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaymentFailedConsumer {

    private final OrderRepository orderRepository;

    @RabbitListener(queues = ORDER_PAYMENT_FAILED_QUEUE)
    private void consume(PaymentFailedEvent event) {
        log.info("ORDER.PAYMENT.FAILED orderId {}, reason '{}'", event.getOrderId(), event.getReason());
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new NotFoundException(DATA_NOT_FOUND_MESSAGE, DATA_NOT_FOUND));

        order.setStatus(PAYMENT_FAILED);
        orderRepository.save(order);
    }

}
