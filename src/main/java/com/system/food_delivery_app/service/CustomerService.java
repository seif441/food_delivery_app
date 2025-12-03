package com.system.food_delivery_app.service;

import com.system.food_delivery_app.model.Customer;
import com.system.food_delivery_app.model.Order;
// import com.system.food_delivery_app.model.Product;
import com.system.food_delivery_app.model.Role;
import com.system.food_delivery_app.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomerService {

    // private final CustomerRepository customerRepo;
    // // private final MenuService menuService;
    // private final OrderService orderService;

    // public CustomerService(CustomerRepository customerRepo,
    //                        MenuService menuService,
    //                        OrderService orderService) {
    //     this.customerRepo = customerRepo;
    //     this.menuService = menuService;
    //     this.orderService = orderService;
    // }

    // 🔹 Register new customer
    // public Customer register(Customer c) {
    //     c.setRole(Role.CUSTOMER);
    //     return customerRepo.save(c);
    // }

    // 🔹 View menu (all products)
    // public List<Product> viewMenu() {
    //     return menuService.getAllProducts();
    // }

    // 🔹 Create order
    // public Order createOrder(Long customerId, List<Long> productIds) {
    //     Customer c = customerRepo.findById(customerId).orElseThrow();
    //     return orderService.placeOrder(c, productIds);
    // }

    // 🔹 View order status
    // public Order viewOrderStatus(Long orderId) {
    //     return orderService.getOrderById(orderId);
    // }

     // 🔹 Update profile
    // public Customer updateProfile(Long id, String name, String phone) {
    //     Customer c = customerRepo.findById(id).orElseThrow();
    //     c.setName(name);
    //     c.setPhoneNumber(phone);
    //     return customerRepo.save(c);
    // }

     // 🔹 Delete account
    // public void deleteAccount(Long id) {
    //     customerRepo.deleteById(id);
    // }
}


