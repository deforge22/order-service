package io.dinesync.orderstream.service.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dinesync.orderstream.data.entity.OrderItem;
import io.dinesync.orderstream.data.repository.OrderRepository;
import io.dinesync.orderstream.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import io.dinesync.orderstream.rest.model.dto.OrderItemDto;
import io.dinesync.orderstream.rest.model.request.OrderRequest;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("Order Api Integration Test")
public class OrderControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Integration: Create Order - Complete Flow")
    void createOrder_CompleteFlow() throws Exception {
        var request = new OrderRequest(
                12,
                List.of(
                        new OrderItemDto("Burger", 2, new BigDecimal("12.99"), "No pickles"),
                        new OrderItemDto("Fries", 1, new BigDecimal("4.50"), null)
                )
        );

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Order created successfully"))
                .andExpect(jsonPath("$.data.tableNumber").value(12))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.orderItems", hasSize(2)))
                .andExpect(jsonPath("$.data.orderItems[0].itemName").value("Burger"))
                .andExpect(jsonPath("$.data.orderItems[0].quantity").value(2))
                .andExpect(jsonPath("$.data.orderItems[0].subtotal").value(25.98))
                .andExpect(jsonPath("$.data.orderItems[1].itemName").value("Fries"))
                .andExpect(jsonPath("$.data.orderItems[1].subtotal").value(4.50))
                .andExpect(jsonPath("$.data.totalAmount").value(30.48));

        var orders = orderRepository.findAllByOrderByCreatedAtDesc();
        assertThat(orders).hasSize(1);

        var savedOrder = orders.get(0);

        assertThat(savedOrder.getTableNumber()).isEqualTo(12);
        assertThat(savedOrder.getOrderItems()).hasSize(2);
        assertThat(savedOrder.getOrderItems().get(0).getItemName()).isEqualTo("Burger");
        assertThat(savedOrder.getOrderItems().get(1).getItemName()).isEqualTo("Fries");
    }


    @Test
    @Order(2)
    @DisplayName("Integration: Should Throw Error When 'tableNumber' value is negative - Error flow")
    @SuppressWarnings({"DataFlowIssue", "ConstantConditions"})
    void createOrder_ShouldThrowError_WhenTableNumberIsNegative() throws Exception {
        var request = new OrderRequest(
                -12,
                List.of(
                        new OrderItemDto("Burger", 2, new BigDecimal("12.99"), "No pickles"),
                        new OrderItemDto("Fries", 1, new BigDecimal("4.50"), null)
                )
        );

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.path").value("/api/v1/orders"))
                .andExpect(jsonPath("$.errors.tableNumber").value("must be greater than 0"));
    }

    @Test
    @Order(3)
    @DisplayName("Integration: Get Orders By Created At Desc")
    void getOrdersByCreatedAtDesc() throws Exception {
        createOrdersForGetOrdersSuccess();
        mockMvc.perform(get("/api/v1/orders"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("All orders fetched successfully"))
                .andExpect(jsonPath("$.data[0].tableNumber").value(11));

    }

    private void createOrdersForGetOrdersSuccess() throws Exception {

        io.dinesync.orderstream.data.entity.Order order1 = io.dinesync.orderstream.data.entity.Order.builder()
                .tableNumber(11)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now().minusMinutes(10))
                .build();

        io.dinesync.orderstream.data.entity.Order order2 = io.dinesync.orderstream.data.entity.Order.builder()
                .tableNumber(12)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now().minusMinutes(20))
                .build();

        orderRepository.saveAll(new ArrayList<>(List.of(order1, order2)));
    }



}
