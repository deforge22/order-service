package io.dinesync.orderstream.rest.model.dto;

import lombok.Builder;

@Builder
public record OrderNotification(
        String message,
        Long orderId,
        String type
) {
}
