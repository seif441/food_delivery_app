package com.example.fooddelivery.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "delivery_addresses")
public class DeliveryAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @NotBlank
    @Size(max = 255)
    private String streetAddress;

    @NotBlank
    @Size(max = 100)
    private String city;

    @Size(max = 20)
    private String postalCode;
    
    @Size(max = 500)
    private String additionalInfo;

    public DeliveryAddress() {
    }

    public DeliveryAddress(Long userId, String streetAddress, String city, String postalCode, String additionalInfo) {
        this.userId = userId;
        this.streetAddress = streetAddress;
        this.city = city;
        this.postalCode = postalCode;
        this.additionalInfo = additionalInfo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}