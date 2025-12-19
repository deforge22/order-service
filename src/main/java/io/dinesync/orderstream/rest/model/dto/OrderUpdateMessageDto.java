package io.dinesync.orderstream.rest.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.dinesync.orderstream.enums.OrderStatus;
import io.dinesync.orderstream.rest.model.response.OrderResponse;
import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record OrderUpdateMessageDto(
    Long orderId,
    Integer tableNumber,
    OrderStatus status,
    OrderStatus previousStatus,
    String action,
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    LocalDateTime timeStamp,
    OrderResponse orderDetails,
    String message
) {

}
