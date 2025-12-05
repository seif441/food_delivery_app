package com.system.food_delivery_app.service;

<<<<<<< HEAD

import com.system.food_delivery_app.dto.StaffRequestDTO;
import com.system.food_delivery_app.model.Staff;
import com.system.food_delivery_app.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
=======
import com.example.model.Staff;
import com.example.repository.StaffRepository;
>>>>>>> c29c617705a343e594dd219d69c302c9ad04ea88
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StaffService {
    private final StaffRepository staffRepository;

<<<<<<< HEAD
    
=======
>>>>>>> c29c617705a343e594dd219d69c302c9ad04ea88
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

