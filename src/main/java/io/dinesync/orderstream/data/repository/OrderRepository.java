package io.dinesync.orderstream.data.repository;

import io.dinesync.orderstream.data.entity.Order;
import io.dinesync.orderstream.enums.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>{

    List<Order> findByStatus(OrderStatus orderStatus);

    List<Order> findByTableNumber(Integer tableNumber);

    List<Order> findByTableNumberAndStatus(Integer tableNumber, OrderStatus orderStatus);

    List<Order> findByTableNumberOrderByCreatedAtDesc(Integer tableNumber);

    @EntityGraph(attributePaths = {"orderItems"})
    List<Order> findAllByOrderByCreatedAtDesc();


}
