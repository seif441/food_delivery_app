package com.system.food_delivery_app.model;

import jakarta.persistence.*;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.system.food_delivery_app.repository.OrderRepository;

@Entity
@DiscriminatorValue("STAFF")
public class Staff extends User {

@Autowired
private OrderRepository orderRepository;
// SEND THE MODEL AND THE SERVICE AND ASK THE AI WHICH OF THEM SHOULD BE KEPT AND EDIT ME WHAT IT NEEDS TO BE EDIT

public List<Order> viewOrders() {
    return orderRepository.findAll();
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
            newStatus == OrderStatus.CANCELLED) {
            order.setStatus(newStatus);
        }
    }
}


}