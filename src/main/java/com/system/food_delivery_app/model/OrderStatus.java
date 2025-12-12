package com.system.food_delivery_app.model;

public enum OrderStatus {
    PENDING,            // New order, waiting for kitchen
    PREPARING,          // Kitchen is cooking
    PREPARED,           // Cooked, waiting for driver assignment
    OUT_FOR_DELIVERY,   // Driver picked it up
    DELIVERED,          // Done
    CANCELLED           // Cancelled by user or staff
}