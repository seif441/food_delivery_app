package com.system.food_delivery_app.service;

import com.system.food_delivery_app.dto.ProductDTO;
import com.system.food_delivery_app.model.Category;
import com.system.food_delivery_app.model.Product;
import com.system.food_delivery_app.repository.CategoryRepository;
import com.system.food_delivery_app.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // --- 1. CREATE ---
    public Product addProduct(ProductDTO productDTO) {
        // 1. Find the Category from the ID provided in the DTO
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + productDTO.getCategoryId()));

        // 2. Map DTO to Entity manually
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setImageUrl(productDTO.getImageUrl());
        
        // Default availability to true if null, otherwise use the DTO value
        if (productDTO.getAvailable() != null) {
            product.setAvailable(productDTO.getAvailable());
        } else {
            product.setAvailable(true);
        }

        // 3. Set the relationship
        product.setCategory(category);

        // 4. Save to Database
        return productRepository.save(product);
    }

    // --- 2. READ (Get All) ---
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // --- 3. READ (Get One by ID) ---
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // --- 4. READ (Get by Category) ---
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    // --- 5. UPDATE ---
    public Product updateProduct(Long id, ProductDTO productDTO) {
        return productRepository.findById(id).map(product -> {
            // Update basic fields
            product.setName(productDTO.getName());
            product.setDescription(productDTO.getDescription());
            product.setPrice(productDTO.getPrice());
            product.setImageUrl(productDTO.getImageUrl());
            
            if (productDTO.getAvailable() != null) {
                product.setAvailable(productDTO.getAvailable());
            }

            // Check if the Category has changed
            if (product.getCategory() == null || !product.getCategory().getId().equals(productDTO.getCategoryId())) {
                Category newCategory = categoryRepository.findById(productDTO.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Category not found with ID: " + productDTO.getCategoryId()));
                product.setCategory(newCategory);
            }

            return productRepository.save(product);
        }).orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
    }

    // --- 6. DELETE ---
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with ID: " + id);
        }
        productRepository.deleteById(id);
    }
}