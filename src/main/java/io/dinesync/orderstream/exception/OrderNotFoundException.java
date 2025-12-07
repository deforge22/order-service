package io.dinesync.orderstream.exception;

import lombok.Getter;

@Getter
public class OrderNotFoundException extends RuntimeException {

    private final Long orderId;

    public OrderNotFoundException(Long orderId) {
        super("Order with id " + orderId + " not found");
        this.orderId = orderId;
    }

}
