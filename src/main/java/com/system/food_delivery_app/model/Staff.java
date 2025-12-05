package com.system.food_delivery_app.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.*;
import java.util.*;
@Data
@EqualsAndHashCode(callSuper = true)
public class Staff extends User {
    private String staffIdNumber;

    public Staff(String name, String email, String staffIdNumber) {
        super(name, email, "STAFF");
        this.staffIdNumber = staffIdNumber;
    }
<<<<<<< HEAD

    // public Staff(String name, Double salary, LocalDate dateOfJoining) {
    //     super(name);
    //     this.salary = salary;
    //     this.dateOfJoining = dateOfJoining;
    // }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public LocalDate getDateOfJoining() {
        return dateOfJoining;
    }

    public void setDateOfJoining(LocalDate dateOfJoining) {
        this.dateOfJoining = dateOfJoining;
    }
}
=======
}
>>>>>>> c29c617705a343e594dd219d69c302c9ad04ea88
