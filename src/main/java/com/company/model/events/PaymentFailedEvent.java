package com.company.model.events;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaymentFailedEvent {
    private Long orderId;
    private String reason;
}