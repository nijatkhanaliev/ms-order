package com.company.model.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockFailedEvent {
    private Long orderId;
    private String reason;
}
