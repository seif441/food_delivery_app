package com.system.food_delivery_app.service;


import com.system.food_delivery_app.dto.StaffRequestDTO;
import com.system.food_delivery_app.model.Staff;
import com.system.food_delivery_app.repository.OrderRepository;
import com.system.food_delivery_app.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StaffService {
    private final StaffRepository staffRepository;
    
    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }
    
    public List<Long> viewIncomingOrders() {
        return List.of(); 
        
    }

    public String startPreparation(Long orderId) {
        return ""; 
    }
    
    public String updateOrderStatus(Long orderId, String newStatus) {
        return ""; 
    }
}

