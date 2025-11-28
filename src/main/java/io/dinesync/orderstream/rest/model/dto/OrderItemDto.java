package io.dinesync.orderstream.rest.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrderItemDto(
        @NotBlank String itemName,
        @NotNull @Positive Integer quantity,
        @NotNull @DecimalMin("0.01") BigDecimal unitPrice,
        String notes
) {
}
