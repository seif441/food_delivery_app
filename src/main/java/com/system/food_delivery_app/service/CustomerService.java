package com.system.food_delivery_app.service;

import com.system.food_delivery_app.model.Customer;
import com.system.food_delivery_app.model.Order;
import com.system.food_delivery_app.model.Product;
import com.system.food_delivery_app.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepo;
    private final ProductService productService; // Changed from MenuService to ProductService for clarity
    private final OrderService orderService;

    public CustomerService(CustomerRepository customerRepo,
                           ProductService productService,
                           OrderService orderService) {
        this.customerRepo = customerRepo;
        this.productService = productService;
        this.orderService = orderService;
    }

    // 1. View menu (delegates to ProductService)
    public List<Product> viewMenu() {
        return productService.getAllProducts();
    }

    // 2. Create order
    // NOTE: In a real app, you usually pass a Cart object, not just a list of IDs.
    // This is a simplified example.
    public Order createOrder(Long customerId, List<Long> productIds) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Logic to create order object would go here
        // For now, we assume OrderService handles the heavy lifting
        // return orderService.placeOrder(customer, productIds); 
        return null; // Placeholder until OrderService.placeOrder is implemented
    }

    // 3. View order status
    public Order viewOrderStatus(Long orderId) {
        return orderService.getOrderById(orderId);
    }
}