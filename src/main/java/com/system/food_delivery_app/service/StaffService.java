package com.system.food_delivery_app.service;


import com.system.food_delivery_app.dto.StaffRequestDTO;
import com.system.food_delivery_app.model.Staff;
import com.system.food_delivery_app.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StaffService {

    private final StaffRepository staffRepository;

    
    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    public Staff saveStaff(StaffRequestDTO staffDTO) {
        Staff staff = new Staff(
            staffDTO.getName(),
            staffDTO.getSalary(),
            staffDTO.getDateOfJoining()
        );
        return staffRepository.save(staff);
    }

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    public Optional<Staff> getStaffById(Long id) {
        return staffRepository.findById(id);
    }

    public void deleteStaff(Long id) {
        staffRepository.deleteById(id);
    }

    public Staff updateStaff(Long id, StaffRequestDTO staffDTO) {
        Optional<Staff> staffOptional = staffRepository.findById(id);

        if (staffOptional.isPresent()) {
            Staff existingStaff = staffOptional.get();

            if (staffDTO.getName() != null) {
                existingStaff.setName(staffDTO.getName());
            }

            if (staffDTO.getSalary() != null) {
                existingStaff.setSalary(staffDTO.getSalary());
            }
            if (staffDTO.getDateOfJoining() != null) {
                existingStaff.setDateOfJoining(staffDTO.getDateOfJoining());
            }

            return staffRepository.save(existingStaff);
        } else {
            throw new RuntimeException("Staff not found with id " + id);
        }
    }
}