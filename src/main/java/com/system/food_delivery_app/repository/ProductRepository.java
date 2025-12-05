package com.system.food_delivery_app.repository;

import com.system.food_delivery_app.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Helper method to find all products inside a specific category
    List<Product> findByCategoryId(Long categoryId);
}