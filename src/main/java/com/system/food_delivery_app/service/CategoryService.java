package com.system.food_delivery_app.service;

import com.system.food_delivery_app.model.Category;
import com.system.food_delivery_app.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // 1. Create
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    // 2. Read All
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // 3. Read One
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    // 4. Update
    public Category updateCategory(Long id, Category categoryDetails) {
        return categoryRepository.findById(id).map(category -> {
            category.setName(categoryDetails.getName());
            category.setDescription(categoryDetails.getDescription());
            return categoryRepository.save(category);
        }).orElseThrow(() -> new RuntimeException("Category not found with id " + id));
    }

    // 5. Delete
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}