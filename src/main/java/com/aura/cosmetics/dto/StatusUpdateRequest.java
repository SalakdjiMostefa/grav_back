package com.aura.cosmetics.dto;

import com.aura.cosmetics.enums.OrderStatus;

public class StatusUpdateRequest {
    private OrderStatus status;

    public StatusUpdateRequest() {}

    public StatusUpdateRequest(OrderStatus status) {
        this.status = status;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
