package com.system.food_delivery_app.model;

import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
public class Staff extends User {

    private Double salary;
    private LocalDate dateOfJoining;

    public Staff() {
        super();
    }

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