package com.system.food_delivery_app.service;

import com.example.model.Staff;
import com.example.repository.StaffRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StaffService {
    private final StaffRepository staffRepository;

    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }
    
    public Staff createStaff(Staff newStaff) {
        return staffRepository.save(newStaff);
    }
    
    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
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

