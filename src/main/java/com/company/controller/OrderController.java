package com.company.controller;

import com.company.model.dto.request.OrderRequest;
import com.company.model.dto.response.OrderResponse;
import com.company.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader("current-user-id") Long userId,
            @Valid @RequestBody OrderRequest request) {

        return ResponseEntity.status(CREATED)
                .body(orderService.createOrder(request, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderDetails(
            @RequestHeader("current-user-id") Long userId,
            @PathVariable Long id) {

        return ResponseEntity.ok(orderService.getOrderDetails(id, userId));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@RequestHeader("current-user-id") Long userId,
                                            @PathVariable Long id) {

        orderService.cancelOrder(id, userId);
        return ResponseEntity.noContent().build();
    }

}
