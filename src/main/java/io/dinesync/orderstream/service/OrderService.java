package io.dinesync.orderstream.service;

import io.dinesync.orderstream.data.entity.Order;
import io.dinesync.orderstream.data.repository.OrderRepository;
import io.dinesync.orderstream.enums.OrderStatus;
import io.dinesync.orderstream.exception.OrderNotFoundException;
import io.dinesync.orderstream.redis.CachePrefix;
import io.dinesync.orderstream.redis.RedisService;
import io.dinesync.orderstream.rest.model.dto.OrderUpdateMessageDto;
import io.dinesync.orderstream.rest.model.request.OrderRequest;
import io.dinesync.orderstream.rest.model.response.OrderResponse;
import io.dinesync.orderstream.utility.mapper.OrderItemMapper;
import io.dinesync.orderstream.utility.mapper.OrderMapper;
import io.dinesync.orderstream.utility.mapper.OrderWebSocketMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static io.dinesync.orderstream.redis.CachePrefix.ORDER;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final OrderWebSocketMapper orderWebSocketMapper;
    private final RedisService redisService;
    private static final String ORDER_RESPONSE_LIST_KEY = "all";
    private static final ParameterizedTypeReference<List<OrderResponse>> ORDER_RESPONSE_LIST_TYPE
            = new ParameterizedTypeReference<>() {};


    public OrderResponse createOrder(OrderRequest request) {
        var order = orderMapper.toEntity(request);
        var orderItems = orderItemMapper.toEntityList(request.orderItems());
        orderItems.forEach(order::addOrderItem);
        var savedOrder = orderRepository.save(order);
        broadcastOrderCreated(savedOrder);
        redisService.delete(ORDER, ORDER_RESPONSE_LIST_KEY);
        return orderMapper.toResponse(savedOrder);
    }

    public List<OrderResponse> getAllOrders() {
        var data = redisService.get(ORDER, ORDER_RESPONSE_LIST_KEY, ORDER_RESPONSE_LIST_TYPE);
        if (CollectionUtils.isEmpty(data)) {
            var dbData = orderRepository.findAllByOrderByCreatedAtDesc();
            if (CollectionUtils.isEmpty(dbData)) {
                log.info("no info found in cache and db...");
            }else {
                var response = orderMapper.toResponse(dbData);
                redisService.set(ORDER, ORDER_RESPONSE_LIST_KEY, response);
                log.info("data came from db and loaded into cache");
                return response;
            }
        }
        log.info("data came from cache and loaded into cache");
        return data;
    }



    public OrderResponse getOrderById(Long id) {
        var orderById = orderRepository.findById(id).orElseThrow(
                () -> new OrderNotFoundException(id)
        );
        return orderMapper.toResponse(orderById);
    }

    public OrderResponse updateOrderStatus(Long id, String status) {
        redisService.delete(ORDER, ORDER_RESPONSE_LIST_KEY);
        var orderById = orderRepository.findById(id).orElseThrow(
                () -> new OrderNotFoundException(id)
        );
        var newOrderStatus = OrderStatus.valueOf(status.toUpperCase());
        var previousStatus = orderById.getStatus();
        orderById.setStatus(newOrderStatus);
        var updatedOrder = orderRepository.save(orderById);
        broadcastOrderStatusUpdated(updatedOrder, previousStatus);
        return orderMapper.toResponse(updatedOrder);
    }

    private void broadcastOrderCreated(Order orderEntity) {
        try {
            var message = orderWebSocketMapper.toCreatedMessage(orderEntity);
            messagingTemplate.convertAndSend("/topic/orders", message);
            log.info("Broadcasting order created: {}", orderEntity.getId());
        } catch (Exception e) {
            log.error("Failed to broadcast order creation for order {}", orderEntity.getId(), e);
        }
    }

    private void broadcastOrderStatusUpdated(Order orderEntity, OrderStatus previousStatus) {
        try {
            var message = orderWebSocketMapper.toStatusChangedMessage(orderEntity, previousStatus);
            messagingTemplate.convertAndSend("/topic/orders", message);
            log.info(
                    "Broadcasting status update for order {}: {} -> {}",
                    orderEntity.getId(), previousStatus, orderEntity.getStatus()
            );
        } catch (Exception e) {
            log.error("Failed to broadcast order status update for order {}", orderEntity.getId(), e);
        }

    }
}
