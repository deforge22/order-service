package io.dinesync.orderstream.utility.mapper;

import io.dinesync.orderstream.data.entity.OrderItem;
import io.dinesync.orderstream.rest.model.dto.OrderItemDto;
import io.dinesync.orderstream.rest.model.dto.OrderItemResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderItem toEntity(OrderItemDto dto);

    List<OrderItem> toEntityList(List<OrderItemDto> dtos);

    @Mapping(target = "subtotal", expression = "java(entity.getSubtotal())")
    OrderItemResponseDto toResponseDto(OrderItem entity);

    List<OrderItemResponseDto> toResponseDtoList(List<OrderItem> entities);
}