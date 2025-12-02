package io.dinesync.orderstream.utility.mapper;

import io.dinesync.orderstream.data.entity.Order;
import io.dinesync.orderstream.data.entity.OrderItem;
import io.dinesync.orderstream.rest.model.request.OrderRequest;
import io.dinesync.orderstream.rest.model.response.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {

    List<OrderResponse> toResponse(List<Order> allOrders);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    Order toEntity(OrderRequest request);

    @Mapping(target = "orderItems", source = "orderItems")
    @Mapping(target = "totalAmount", expression = "java(calculateTotalAmount(order.getOrderItems()))")
    OrderResponse toResponse(Order order);

    @Named("calculateTotalAmount")
    default BigDecimal calculateTotalAmount(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}