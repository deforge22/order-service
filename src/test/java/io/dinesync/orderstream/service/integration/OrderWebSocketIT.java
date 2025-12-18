package io.dinesync.orderstream.service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.dinesync.orderstream.rest.model.dto.OrderUpdateMessageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;


import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderWebSocketIT {

    @LocalServerPort
    private int port;

    private WebSocketStompClient webSocketStompClient;
    private BlockingQueue<OrderUpdateMessageDto> receivedMessages;

    @BeforeEach
    void setUp() {

        SockJsClient sockJsClient = new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))
        );

        webSocketStompClient = new WebSocketStompClient(sockJsClient);
        webSocketStompClient.setMessageConverter(new JacksonJsonMessageConverter());
        receivedMessages = new LinkedBlockingQueue<>();
    }

    @Test
    void shouldConnectToWebSocket() throws Exception {
        String wsUrl = "ws://localhost:" + port + "/ws";
        StompSession session = webSocketStompClient.connectAsync(wsUrl, new StompSessionHandlerAdapter() {})
                .get(5, TimeUnit.SECONDS);
        assertThat(session.isConnected()).isTrue();
        session.disconnect();
    }

}
