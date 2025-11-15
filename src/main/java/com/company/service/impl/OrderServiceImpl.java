package com.company.service.impl;

import com.company.client.InventoryClient;
import com.company.client.UserClient;
import com.company.common.BaseEvent;
import com.company.common.BaseResultEvent;
import com.company.dao.entity.Order;
import com.company.dao.entity.OrderItem;
import com.company.dao.repository.OrderRepository;
import com.company.exception.EmptyOrderItemsException;
import com.company.exception.NotFoundException;
import com.company.exception.OrderCancellationNotAllowedException;
import com.company.messaging.MessageProducer;
import com.company.model.dto.request.OrderItemRequest;
import com.company.model.dto.request.OrderRequest;
import com.company.model.dto.response.OrderResponse;
import com.company.model.enums.OrderStatus;
import com.company.model.mapper.OrderItemMapper;
import com.company.model.mapper.OrderMapper;
import com.company.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.company.exception.constant.ErrorCode.DATA_NOT_FOUND;
import static com.company.exception.constant.ErrorCode.EMPTY_ORDER_ITEMS;
import static com.company.exception.constant.ErrorCode.ORDER_CANCELLATION_NOT_ALLOWED;
import static com.company.exception.constant.ErrorMessage.DATA_NOT_FOUND_MESSAGE;
import static com.company.exception.constant.ErrorMessage.EMPTY_ORDER_ITEMS_MESSAGE;
import static com.company.exception.constant.ErrorMessage.ORDER_CANCELLATION_NOT_ALLOWED_MESSAGE;
import static com.company.model.constant.OrderConstant.NON_CANCELLABLE_STATUSES;
import static com.company.model.constant.RabbitConstant.ORDER_EXCHANGE;
import static com.company.model.constant.RabbitConstant.RQI_ORDER_CREATED_RESULT;
import static com.company.model.constant.RabbitConstant.STOCK_EXCHANGE;
import static com.company.model.constant.RabbitConstant.STOCK_ROUTING_KEY;
import static com.company.model.enums.OrderStatus.CANCELLED;
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
    private final MessageProducer messageProducer;

    @Override
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
        Order order = createOrderEntity(orderItems, userId, totalOrderAmount);
        order = orderRepository.save(order);
        createOrderEvent(order);
        return orderMapper.toOrderResponse(order);
    }

    @Override
    public OrderResponse getOrderDetails(Long orderId, Long userId) {
        log.info("Getting order details, userId {}, orderId {}", userId, orderId);
        Order order = findOrderByIdAndUserId(orderId, userId);
        return orderMapper.toOrderResponse(order);
    }

    @Override
    public void cancelOrder(Long orderId, Long userId) {
        log.info("Cancelling order, userId {}, orderId {}", userId, orderId);
        Order order = findOrderByIdAndUserId(orderId, userId);

        if (NON_CANCELLABLE_STATUSES.contains(order.getStatus())) {
            throw new OrderCancellationNotAllowedException(
                    String.format(ORDER_CANCELLATION_NOT_ALLOWED_MESSAGE, order.getStatus()),
                    ORDER_CANCELLATION_NOT_ALLOWED
            );
        }

        order.setStatus(CANCELLED);
        orderRepository.save(order);
    }

    @Override
    public void processStockOrderCreatedResult(BaseResultEvent event) {
        String status = event.getStatus();
        Order order = findOrderById(event.getOrderId());

        if ("SUCCESS".equalsIgnoreCase(status)) {
            //TODO SEND EVENT TO PAYMENT MS
        } else if ("FAILED".equalsIgnoreCase(status)) {
            log.info("Creating Order failed. orderId {}, reason {}",
                    event.getOrderId(), event.getReason());
            order.setStatus(STOCK_FAILED);
            orderRepository.save(order);
        }

    }

    private Order createOrderEntity(List<OrderItem> orderItems, Long userId, BigDecimal totalOrderAmount) {
        Order order = new Order();
        order.setItems(orderItems);
        order.setStatus(OrderStatus.CREATED);
        order.setUserId(userId);
        order.setTotalAmount(totalOrderAmount);

        orderItems.forEach(item -> item.setOrder(order));
        order.setItems(orderItems);

        return order;
    }

    private void createOrderEvent(Order order) {
        BaseEvent<Order> orderCreatedEvent = BaseEvent.<Order>builder()
                .eventId(UUID.randomUUID().toString())
                .payload(order)
                .responseQueueInfo(RQI_ORDER_CREATED_RESULT)
                .build();

        messageProducer.sendOrderCreatedEvent(STOCK_EXCHANGE, STOCK_ROUTING_KEY,
                orderCreatedEvent);
    }

    private Order findOrderByIdAndUserId(Long id, Long userId) {
        return orderRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException(DATA_NOT_FOUND_MESSAGE, DATA_NOT_FOUND));
    }

    private Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(DATA_NOT_FOUND_MESSAGE, DATA_NOT_FOUND));
    }


}
