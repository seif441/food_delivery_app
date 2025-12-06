package com.system.food_delivery_app.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue(“STAFF”)
public class Staff extends User {


public List<Order> viewOrders() {
    return this.getOrders();
}

public void prepareOrders(Order order) {
    if (order != null && order.getStatus() == OrderStatus.PENDING) {
        order.setStatus(OrderStatus.PREPARING);
    }
}

public void updateOrderStatus(Order order, OrderStatus newStatus) {
    if (order != null && newStatus != null) {
        if (newStatus == OrderStatus.PREPARING || 
            newStatus == OrderStatus.OUT_FOR_DELIVERY || 
            newStatus == OrderStatus.CANCELED) {
            order.setStatus(newStatus);
        }
    }
}


}