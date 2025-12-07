package io.dinesync.orderstream.rest.model.request;

import io.dinesync.orderstream.rest.model.dto.OrderItemDto;
import io.dinesync.orderstream.utility.annotations.ValidTableNumber;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record OrderRequest(
        @NotNull @Positive @ValidTableNumber(min = 5, max = 100) Integer tableNumber,
        @NotEmpty @Valid List<OrderItemDto> orderItems
) {
}
