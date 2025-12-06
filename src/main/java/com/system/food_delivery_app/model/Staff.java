package com.system.food_delivery_app.model;
import jakarta.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("STAFF")
public class Staff extends User {



}