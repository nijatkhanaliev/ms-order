package com.company.service.impl;

import com.company.dao.entity.Order;
import com.company.dao.repository.OrderRepository;
import com.company.exception.NotFoundException;
import com.company.exception.OrderAlreadyCancelledException;
import com.company.model.events.PaymentFailedEvent;
import com.company.model.events.PaymentSuccessEvent;
import com.company.model.events.StockFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.company.exception.constant.ErrorCode.DATA_NOT_FOUND;
import static com.company.exception.constant.ErrorCode.ORDER_ALREADY_CANCELLED;
import static com.company.exception.constant.ErrorMessage.DATA_NOT_FOUND_MESSAGE;
import static com.company.exception.constant.ErrorMessage.ORDER_ALREADY_CANCELLED_MESSAGE;
import static com.company.model.enums.OrderStatus.CANCELLED;
import static com.company.model.enums.OrderStatus.CONFIRMED;
import static com.company.model.enums.OrderStatus.DELIVERED;
import static com.company.model.enums.OrderStatus.PAYMENT_FAILED;
import static com.company.model.enums.OrderStatus.STOCK_FAILED;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final OrderEventPublisher orderEventPublisher;
    private final OrderRepository orderRepository;

    public void consumeStockUpdated(StockFailedEvent event) {
        log.info("Stock updated failed, cancelling order. orderId {}. Message '{}'",
                event.getOrderId(), event.getReason());

        Long orderId = event.getOrderId();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(DATA_NOT_FOUND_MESSAGE, DATA_NOT_FOUND));
        order.setStatus(STOCK_FAILED);
        orderRepository.save(order);
    }

    public void consumePaymentSuccess(PaymentSuccessEvent event) {
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

    public void consumerPaymentFailed(PaymentFailedEvent event) {
        log.info("ORDER.PAYMENT.FAILED orderId {}, reason '{}'", event.getOrderId(), event.getReason());
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new NotFoundException(DATA_NOT_FOUND_MESSAGE, DATA_NOT_FOUND));

        order.setStatus(PAYMENT_FAILED);
        orderRepository.save(order);
    }


    public void consumerDeliverySuccess(PaymentSuccessEvent event) {
        log.info("ORDER.DELIVERY.COMPLETED, orderId {}", event.getOrderId());
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new NotFoundException(DATA_NOT_FOUND_MESSAGE, DATA_NOT_FOUND));

        order.setStatus(DELIVERED);
        orderRepository.save(order);
    }

}
