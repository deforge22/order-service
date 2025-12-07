package io.dinesync.orderstream.service.unit;

import io.dinesync.orderstream.data.entity.Order;
import io.dinesync.orderstream.data.entity.OrderItem;
import io.dinesync.orderstream.data.repository.OrderRepository;
import io.dinesync.orderstream.rest.model.dto.OrderItemDto;
import io.dinesync.orderstream.rest.model.dto.OrderItemResponseDto;
import io.dinesync.orderstream.rest.model.request.OrderRequest;
import io.dinesync.orderstream.rest.model.response.OrderResponse;
import io.dinesync.orderstream.service.OrderService;
import io.dinesync.orderstream.utility.mapper.OrderItemMapper;
import io.dinesync.orderstream.utility.mapper.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static io.dinesync.orderstream.enums.OrderStatus.PENDING;
import static java.math.BigDecimal.TEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Order Service unit tests")
public class OrderServiceUT {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderItemMapper orderItemMapper;

    @InjectMocks
    private OrderService orderService;

    private OrderRequest firstOderRequest;
    private OrderItemDto firstOrderItemDto;
    private OrderResponse firstOrderResponse;
    private OrderItemResponseDto firstOrderItemResponseDto;
    private OrderItem firstOrderItem;
    private Order firstOrder;

    private OrderItemDto secondOrderItemDto;
    private OrderItem secondOrderItem;
    private Order secondOrder;
    private OrderResponse secondOrderResponse;
    private OrderItemResponseDto secondOrderItemResponseDto;






    @BeforeEach
    void setUp() {
        firstOrderItemDto =
                new OrderItemDto("Burger", BigDecimal.valueOf(2), new BigDecimal("12.99"), "No pickles");
        firstOderRequest = new OrderRequest(5, List.of(firstOrderItemDto));

        firstOrder = Order.builder()
                .id(1L)
                .tableNumber(5)
                .status(PENDING)
                .orderItems(new ArrayList<>())
                .createdAt(LocalDateTime.now().minusHours(1))
                .build();

        firstOrderItem = OrderItem.builder()
                .id(1L)
                .itemName("Burger")
                .quantity(BigDecimal.valueOf(2))
                .unitPrice(new BigDecimal("12.99"))
                .notes("No pickles")
                .build();

        firstOrderItemResponseDto = new OrderItemResponseDto(
                1L, "Burger", BigDecimal.valueOf(2), new BigDecimal("12.99"), "No pickles", BigDecimal.ZERO);

        firstOrderResponse = new OrderResponse(
                1L,
                5,
                PENDING,
                List.of(firstOrderItemResponseDto),
                null,
                null,
                new BigDecimal("25.98")
        );



        secondOrderItem = new OrderItem(
                12L,
                secondOrder,
                "Apple",
                BigDecimal.ONE,
                TEN,
                "Green Apple"
        );

        secondOrder = Order.builder()
                .id(2L)
                .tableNumber(10)
                .status(PENDING)
                .orderItems(new ArrayList<>(List.of(secondOrderItem)))
                .createdAt(LocalDateTime.now())
                .build();

        secondOrderItemDto = new  OrderItemDto("Apple", BigDecimal.valueOf(1), TEN, "Green Apple");
        secondOrderItemResponseDto = new OrderItemResponseDto(
                12L,
                "Apple",
                BigDecimal.valueOf(2),
                TEN,
                "Green Apple",
                BigDecimal.ONE
        );
        secondOrderResponse = new OrderResponse(
                2L,
                10,
                PENDING,
                List.of(secondOrderItemResponseDto),
                null,
                null,
                new BigDecimal("25.98")
        );
    }

    @Test
    @DisplayName("test should create order successfully...")
    void createOrderSuccessfully() {
        when(orderMapper.toEntity(firstOderRequest)).thenReturn(firstOrder);
        when(orderItemMapper.toEntityList(firstOderRequest.orderItems())).thenReturn(List.of(firstOrderItem));
        when(orderRepository.save(firstOrder)).thenReturn(firstOrder);
        when(orderMapper.toResponse(firstOrder)).thenReturn(firstOrderResponse);

        var serviceResponse = orderService.createOrder(firstOderRequest);

        var orderEntityCaptor = ArgumentCaptor.forClass(Order.class);

        verify(orderRepository).save(orderEntityCaptor.capture());

        var capturedSavedOrderValue = orderEntityCaptor.getValue();

        assertThat(capturedSavedOrderValue.getId()).isEqualTo(firstOrder.getId());
        assertThat(capturedSavedOrderValue.getTableNumber()).isEqualTo(firstOrder.getTableNumber());
        assertThat(capturedSavedOrderValue.getStatus()).isEqualTo(firstOrder.getStatus());
        assertThat(capturedSavedOrderValue.getCreatedAt()).isEqualTo(firstOrder.getCreatedAt());
        assertThat(capturedSavedOrderValue.getOrderItems()).hasSize(1).first()
                .satisfies(orderItem -> {
                    assertThat(orderItem.getId()).isEqualTo(firstOrderItem.getId());
                    assertThat(orderItem.getItemName()).isEqualTo(firstOrderItem.getItemName());
                    assertThat(orderItem.getQuantity()).isEqualTo(firstOrderItem.getQuantity());
                    assertThat(orderItem.getUnitPrice()).isEqualTo(firstOrderItem.getUnitPrice());
                    assertThat(orderItem.getNotes()).isEqualTo(firstOrderItem.getNotes());
                });
        assertThat(serviceResponse.tableNumber()).isEqualTo(firstOrder.getTableNumber());

        verify(orderMapper).toEntity(firstOderRequest);
        verify(orderItemMapper).toEntityList(firstOderRequest.orderItems());
        verify(orderMapper).toResponse(firstOrder);
        verify(orderRepository).save(firstOrder);

    }

    @Test
    @DisplayName("test should return all orders successfully by time order")
    void getAllOrdersSuccessfully() {
        orderRepository.saveAll(new ArrayList<>(List.of(firstOrder, secondOrder)));

        when(orderRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(firstOrder, secondOrder));
        when(orderMapper.toResponse(List.of(firstOrder, secondOrder)))
                .thenReturn(List.of(firstOrderResponse, secondOrderResponse));
        var serviceResponse = orderService.getAllOrders();

        assertThat(serviceResponse.getFirst().tableNumber()).isEqualTo(firstOrder.getTableNumber());

        verify(orderRepository).findAllByOrderByCreatedAtDesc();
        verify(orderMapper).toResponse(List.of(firstOrder, secondOrder));
    }



}
