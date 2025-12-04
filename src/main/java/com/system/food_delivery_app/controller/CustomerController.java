package com.system.food_delivery_app.controller;

import com.system.food_delivery_app.model.Customer;
import com.system.food_delivery_app.model.Order;
import com.system.food_delivery_app.model.Product;
import com.system.food_delivery_app.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    // View menu
    // @GetMapping("/menu")
    // public List<Product> viewMenu() {
    //     return service.viewMenu();
    // }

    // Create order
    // @PostMapping("/{id}/orders")
    // public Order createOrder(@PathVariable Long id, @RequestBody List<Long> productIds) {
    //     return service.createOrder(id, productIds);
    // }

    // View order status
    @GetMapping("/orders/{orderId}")
    public Order viewOrderStatus(@PathVariable Long orderId) {
        return service.viewOrderStatus(orderId);
    }


}


