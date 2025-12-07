package io.dinesync.orderstream.rest.model.dto;

import io.dinesync.orderstream.utility.annotations.ValidQuantity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemDto(
        @NotBlank
        String itemName,
        @NotNull(message = "quantity cannot be null")
        @Positive(message = "quantity should be positive :)")
        @ValidQuantity(min = "0", max = "1000", allowFractional = false)
        BigDecimal quantity,
        @NotNull
        @DecimalMin("0.01")
        BigDecimal unitPrice,
        String notes
) {
}
