package com.company.service.impl;

import com.company.messaging.MessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final MessageProducer messageProducer;

    public void publishOrderConfirmedEvent(Long orderId, Long userId) {
        log.info("Publishing order confirmed event. orderId {}", orderId);

    }

}
