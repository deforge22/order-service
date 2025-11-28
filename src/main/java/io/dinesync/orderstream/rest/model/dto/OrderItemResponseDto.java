package io.dinesync.orderstream.rest.model.dto;

import java.math.BigDecimal;

public record OrderItemResponseDto(
        Long id,
        String itemName,
        Integer quantity,
        BigDecimal unitPrice,
        String notes,
        BigDecimal subtotal
) {
}
