package com.system.food_delivery_app.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue(“DELIVERY_STAFF”)
public class DeliveryStaff extends User {


public List<Order> viewAssignedOrders() {
    return this.getOrders().stream()
            .filter(order -> order.getStatus() == OrderStatus.OUT_FOR_DELIVERY)
            .collect(Collectors.toList());
}

public void updateDeliveryStatus(Order order) {
    if (order != null && order.getStatus() == OrderStatus.OUT_FOR_DELIVERY) {
        order.setStatus(OrderStatus.DELIVERED);
    }
}


}