package io.dinesync.orderstream.rest.controller;

import io.dinesync.orderstream.rest.model.dto.OrderUpdateMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderWebSocketController {

    private final SimpMessagingTemplate simpMessagingTemplate;


    @MessageMapping("/order-update")
    @SendTo("/topic/orders")
    public OrderUpdateMessageDto updateOrder(OrderUpdateMessageDto message) {
        log.info("Received WebSocket message: action={}, orderId={}, tableNumber={}",
                message.action(),
                message.orderId(),
                message.tableNumber());
        return message;
    }

    public void broadcastOrderUpdate(OrderUpdateMessageDto message) {
        log.info("Broadcasting order update: action={}, orderId={}, tableNumber={}",
                message.action(),
                message.orderId(),
                message.tableNumber());
        simpMessagingTemplate.convertAndSend("/topic/orders", message);
    }
}
