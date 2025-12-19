package io.dinesync.orderstream.utility.mapper;

import io.dinesync.orderstream.data.entity.Order;
import io.dinesync.orderstream.enums.OrderStatus;
import io.dinesync.orderstream.rest.model.dto.OrderUpdateMessageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {OrderMapper.class})
public interface OrderWebSocketMapper {

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "tableNumber", source = "order.tableNumber")
    @Mapping(target = "status", source = "order.status")
    @Mapping(target = "previousStatus", ignore = true)
    @Mapping(target = "action", constant = "CREATED")
    @Mapping(target = "timeStamp", source = "order.createdAt")
    @Mapping(target = "orderDetails", source = "order")
    @Mapping(target = "message", source = "order", qualifiedByName = "createMessage")
    OrderUpdateMessageDto toCreatedMessage(Order order);


    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "tableNumber", source = "order.tableNumber")
    @Mapping(target = "status", source = "order.status")
    @Mapping(target = "previousStatus", source = "previousStatus")
    @Mapping(target = "action", constant = "STATUS_CHANGED")
    @Mapping(target = "timeStamp", source = "order.updatedAt")
    @Mapping(target = "orderDetails", source = "order")
    @Mapping(target = "message", expression = "java(createStatusChangeMessage(order, previousStatus))")
    OrderUpdateMessageDto toStatusChangedMessage(Order order, OrderStatus previousStatus);

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "tableNumber", source = "order.tableNumber")
    @Mapping(target = "status", source = "order.status")
    @Mapping(target = "previousStatus", ignore = true)
    @Mapping(target = "action", constant = "DELETED")
    @Mapping(target = "timeStamp", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "orderDetails", ignore = true)
    @Mapping(target = "message", source = "order", qualifiedByName = "deleteMessage")
    OrderUpdateMessageDto toDeletedMessage(Order order);

    @Named("createMessage")
    default String createMessage(Order order) {
        return String.format(
                "Created a new order with ID: %d for table with number: %d",
                order.getId(),
                order.getTableNumber()
        );
    }

    default String createStatusChangeMessage(Order order, OrderStatus previousStatus) {
        return String.format(
                "Order #%d for table %d status changed: %s → %s",
                order.getId(),
                order.getTableNumber(),
                previousStatus,
                order.getStatus()
        );
    }

    @Named("deleteMessage")
    default String deleteMessage(Order order) {
        return String.format(
                "Deleted order #%d for table %d",
                order.getId(),
                order.getTableNumber()
        );
    }
}
