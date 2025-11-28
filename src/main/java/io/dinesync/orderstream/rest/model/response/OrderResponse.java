package io.dinesync.orderstream.rest.model.response;

import io.dinesync.orderstream.enums.OrderStatus;
import io.dinesync.orderstream.rest.model.dto.OrderItemResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        Integer tableNumber,
        OrderStatus status,
        OrderItemResponseDto orderItems,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        BigDecimal totalAmount
) {
}
