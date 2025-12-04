package com.system.food_delivery_app.model;

import jakarta.persistence.*;

@Entity

public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String imageURL;
    private boolean isActive = true;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}
