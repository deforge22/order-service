package io.dinesync.orderstream.rest.controller;

import io.dinesync.orderstream.enums.OrderStatus;
import io.dinesync.orderstream.rest.model.request.OrderRequest;
import io.dinesync.orderstream.rest.model.response.ApiResponse;
import io.dinesync.orderstream.rest.model.response.OrderResponse;
import io.dinesync.orderstream.service.OrderService;
import io.dinesync.orderstream.utility.annotations.ValidEnum;
import io.dinesync.orderstream.utility.generator.ApiResponseGenerator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@NullMarked
public class OrderController {

    private final OrderService service;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody OrderRequest request) {
        var order = service.createOrder(request);
        return ApiResponseGenerator.created(order, "Order created successfully");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrder() {
        var allOrders = service.getAllOrders();
        return ApiResponseGenerator.ok(allOrders, "All orders fetched successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long id) {
        var orderById = service.getOrderById(id);
        return ApiResponseGenerator.ok(orderById, String.format("Order fetched for id: %d", id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrder(
            @PathVariable Long id,
            @NotEmpty(message = "order-status param cannot be null or empty")
            @ValidEnum(enumClass = OrderStatus.class)
            @RequestParam(required = false, name = "order-status") String status) {
        var updatedOrder = service.updateOrderStatus(id, status);
        return ApiResponseGenerator.ok(
                updatedOrder,
                "Order with id: %d updated status to %s successfully".formatted(id, status)
        );
    }
}
