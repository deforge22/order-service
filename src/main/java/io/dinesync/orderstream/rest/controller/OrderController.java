package io.dinesync.orderstream.rest.controller;

import io.dinesync.orderstream.rest.model.request.OrderRequest;
import io.dinesync.orderstream.rest.model.response.ApiResponse;
import io.dinesync.orderstream.rest.model.response.OrderResponse;
import io.dinesync.orderstream.service.OrderService;
import io.dinesync.orderstream.utility.generator.ApiResponseGenerator;
import jakarta.validation.Valid;
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
}
