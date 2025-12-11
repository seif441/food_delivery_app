package com.system.food_delivery_app.model;

import java.sql.Date;
import java.util.HashSet;
import jakarta.persistence.*;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "Users")
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    private String phoneNumber;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = true)
    private Role role;
    @Column(name = "role_name")
    private String roleName;
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date(System.currentTimeMillis());
    }

    public User(Long id, String name, String email, String password, String phoneNumber, Role role, String roleName, Date createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.roleName = roleName;
        this.createdAt = createdAt;
    }

    public User() {
    }


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public Date getCreatedAt() {
        return this.createdAt;
    }
    public void setRole(Role role) {
        this.role = role;
        if (role != null) {
            this.roleName = role.getRoleName();
        }
    }
    public String getRoleName() {
        return roleName;
    }
    @JsonIgnore
    public Role getRole() {
        return this.role;
    }
}

    
