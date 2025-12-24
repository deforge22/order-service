package io.dinesync.orderstream.config;

import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@NullMarked
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${websocket.allowed-origins}")
    private String allowedOrigins;

    @Value("${websocket.broker-destination-prefix}")
    private String brokerDestinationPrefix;

    @Value("${websocket.application-destination-prefix}")
    private String applicationDestinationPrefix;

    @Value("${websocket.user-destination-prefix}")
    private String userDestinationPrefix;

    @Value("${websocket.endpoint}")
    private String wsEndpoint;

    @Value("${websocket.raw-endpoint}")
    private String wsRawEndpoint;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(brokerDestinationPrefix);
        registry.setApplicationDestinationPrefixes(applicationDestinationPrefix);
        registry.setUserDestinationPrefix(userDestinationPrefix);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        String[] origins = allowedOrigins.split(",");

        registry
                .addEndpoint(wsEndpoint)
                .setAllowedOrigins(origins)
                .withSockJS();

        registry
                .addEndpoint(wsRawEndpoint)
                .setAllowedOriginPatterns("*");
    }
}