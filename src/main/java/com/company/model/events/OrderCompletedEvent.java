package com.company.model.events;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class OrderCompletedEvent implements Serializable {
    private String eventId = UUID.randomUUID().toString();
    private Long orderId;
    private Long userId;
}
