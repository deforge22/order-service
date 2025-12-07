package io.dinesync.orderstream.service;

import io.dinesync.orderstream.data.repository.OrderRepository;
import io.dinesync.orderstream.enums.OrderStatus;
import io.dinesync.orderstream.exception.OrderNotFoundException;
import io.dinesync.orderstream.rest.model.request.OrderRequest;
import io.dinesync.orderstream.rest.model.response.OrderResponse;
import io.dinesync.orderstream.utility.mapper.OrderItemMapper;
import io.dinesync.orderstream.utility.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;


    public OrderResponse createOrder(OrderRequest request) {
        var order = orderMapper.toEntity(request);
        var orderItems = orderItemMapper.toEntityList(request.orderItems());
        orderItems.forEach(order::addOrderItem);
        var savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }

    public List<OrderResponse> getAllOrders() {
        var allOrders = orderRepository.findAllByOrderByCreatedAtDesc();
        return orderMapper.toResponse(allOrders);
    }

    public OrderResponse getOrderById(Long id) {
        var orderById = orderRepository.findById(id).orElseThrow(
                () -> new OrderNotFoundException(id)
        );
        return orderMapper.toResponse(orderById);
    }

    public OrderResponse updateOrderStatus(Long id, String status) {
        var orderById = orderRepository.findById(id).orElseThrow(
                () -> new OrderNotFoundException(id)
        );
        orderById.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        var updatedOrder = orderRepository.save(orderById);
        return orderMapper.toResponse(updatedOrder);
    }
}
