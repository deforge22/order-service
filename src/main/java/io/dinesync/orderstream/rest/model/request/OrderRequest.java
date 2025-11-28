package io.dinesync.orderstream.rest.model.request;

import io.dinesync.orderstream.rest.model.dto.OrderItemDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record OrderRequest(
        @NotNull @Positive Integer tableNumber,
        @NotEmpty @Valid List<OrderItemDto> orderItems
) {
}
