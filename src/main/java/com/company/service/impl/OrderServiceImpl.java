package com.company.service.impl;

import com.company.client.InventoryClient;
import com.company.client.UserClient;
import com.company.dao.entity.Order;
import com.company.dao.entity.OrderItem;
import com.company.dao.repository.OrderRepository;
import com.company.exception.EmptyOrderItemsException;
import com.company.exception.NotFoundException;
import com.company.exception.OrderAlreadyCancelledException;
import com.company.messaging.OrderCreatedProducer;
import com.company.model.dto.OrderItemDto;
import com.company.model.dto.request.OrderItemRequest;
import com.company.model.dto.request.OrderRequest;
import com.company.model.dto.response.OrderResponse;
import com.company.model.enums.OrderStatus;
import com.company.model.events.OrderCreatedEvent;
import com.company.model.mapper.OrderItemMapper;
import com.company.model.mapper.OrderMapper;
import com.company.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static com.company.config.RabbitMQConfig.ORDER_EXCHANGE;
import static com.company.config.RabbitMQConfig.ORDER_ROUTING_KEY;
import static com.company.exception.constant.ErrorCode.DATA_NOT_FOUND;
import static com.company.exception.constant.ErrorCode.EMPTY_ORDER_ITEMS;
import static com.company.exception.constant.ErrorCode.ORDER_CANCELLATION_NOT_ALLOWED;
import static com.company.exception.constant.ErrorMessage.DATA_NOT_FOUND_MESSAGE;
import static com.company.exception.constant.ErrorMessage.EMPTY_ORDER_ITEMS_MESSAGE;
import static com.company.exception.constant.ErrorMessage.ORDER_CANCELLATION_NOT_ALLOWED_MESSAGE;
import static com.company.model.enums.OrderStatus.CANCELLED;
import static com.company.model.enums.OrderStatus.DELIVERED;
import static com.company.model.enums.OrderStatus.PAYMENT_FAILED;
import static com.company.model.enums.OrderStatus.SHIPPED;
import static com.company.model.enums.OrderStatus.STOCK_FAILED;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserClient userClient;
    private final InventoryClient inventoryClient;
    private final OrderItemMapper orderItemMapper;
    private final OrderCreatedProducer orderCreatedProducer;


    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest, Long userId) {
        log.info("Creating order, userId {}", userId);
        boolean userExists = userClient.userExists(userId);
        if (!userExists) {
            throw new NotFoundException(DATA_NOT_FOUND_MESSAGE, DATA_NOT_FOUND);
        }

        List<OrderItemRequest> orderItemRequests = orderRequest.getOrderItemRequests();
        if (orderItemRequests.isEmpty()) {
            throw new EmptyOrderItemsException(EMPTY_ORDER_ITEMS_MESSAGE, EMPTY_ORDER_ITEMS);
        }
        List<OrderItem> orderItems = orderItemMapper.toOrderItems(orderItemRequests, inventoryClient);

        BigDecimal totalOrderAmount = orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setItems(orderItems);
        order.setStatus(OrderStatus.CREATED);
        order.setUserId(userId);
        order.setTotalAmount(totalOrderAmount);

        orderItems.forEach(item -> item.setOrder(order));
        order.setItems(orderItems);
        Order orderEntity = orderRepository.save(order);

        log.info("OrderCreatedEvent created, orderId {}", orderEntity.getId());
        List<OrderItemDto> orderItemDtos = orderItems.stream()
                .map(item -> new OrderItemDto(item.getProductId(), item.getQuantity()))
                .toList();

        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
        orderCreatedEvent.setOrderItemDtos(orderItemDtos);
        orderCreatedEvent.setUserId(userId);
        orderCreatedEvent.setTotalPrice(order.getTotalAmount());
        orderCreatedEvent.setOrderId(orderEntity.getId());

        orderCreatedProducer.send(ORDER_EXCHANGE, ORDER_ROUTING_KEY, orderCreatedEvent);

        return orderMapper.toOrderResponse(orderEntity);
    }

    @Override
    public OrderResponse getOrderDetails(Long orderId, Long userId) {
        log.info("Getting order details, userId {}, orderId {}", userId, orderId);
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new NotFoundException(DATA_NOT_FOUND_MESSAGE, DATA_NOT_FOUND));

        return orderMapper.toOrderResponse(order);
    }

    @Override
    public void cancelOrder(Long orderId, Long userId) {
        log.info("Cancelling order, userId {}, orderId {}", userId, orderId);
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new NotFoundException(DATA_NOT_FOUND_MESSAGE, DATA_NOT_FOUND));

        if (order.getStatus() == CANCELLED || order.getStatus() == DELIVERED ||
                order.getStatus() == SHIPPED || order.getStatus() == STOCK_FAILED
                || order.getStatus() == PAYMENT_FAILED
        ) {
            throw new OrderAlreadyCancelledException(
                    String.format(ORDER_CANCELLATION_NOT_ALLOWED_MESSAGE, order.getStatus()),
                    ORDER_CANCELLATION_NOT_ALLOWED
            );
        }

        order.setStatus(CANCELLED);
        orderRepository.save(order);
    }

}
