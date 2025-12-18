package io.dinesync.orderstream.rest.model.dto;

import io.dinesync.orderstream.enums.OrderStatus;
import io.dinesync.orderstream.rest.model.response.OrderResponse;
import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record OrderUpdateMessageDto(
    Long orderId,
    Integer tableNumber,
    OrderStatus status,
    String action,
    LocalDateTime timeStamp,
    OrderResponse orderDetails
) {

}
