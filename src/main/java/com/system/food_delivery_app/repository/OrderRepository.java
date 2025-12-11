package com.system.food_delivery_app.repository;

import com.system.food_delivery_app.model.Order;
import com.system.food_delivery_app.model.OrderStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Find orders belonging to a specific customer
    List<Order> findByCustomerId(Long customerId);

    // Find orders belonging to a specific delivery staff with a specific status
    // SQL: SELECT * FROM orders WHERE delivery_staff_id = ? AND status = ?
    List<Order> findByDeliveryStaffIdAndStatus(Long deliveryStaffId, OrderStatus status);

    // Find all orders by status (useful for Admins or finding unassigned orders)
    List<Order> findByStatus(OrderStatus status);
}