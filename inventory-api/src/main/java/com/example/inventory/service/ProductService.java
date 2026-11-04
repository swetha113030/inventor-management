
package com.example.inventory.service;

import com.example.inventory.dto.InventorySummary;
import com.example.inventory.entity.Product;
import com.example.inventory.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    // Get all products
    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    // Get product by ID
    public Product getProductById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found"));
    }

    // Add a new product
    public Product addProduct(Product product) {
        return repository.save(product);
    }

    // Update an existing product
    public Product updateProduct(Long id, Product updatedProduct) {

        Product existing = getProductById(id);

        existing.setName(updatedProduct.getName());
        existing.setCategory(updatedProduct.getCategory());
        existing.setPrice(updatedProduct.getPrice());
        existing.setQuantity(updatedProduct.getQuantity());

        return repository.save(existing);
    }

    // Delete a product
    public void deleteProduct(Long id) {

        Product existing = getProductById(id);

        repository.delete(existing);
    }

    // Calculate total inventory value
    public BigDecimal getTotalInventoryValue() {

        return repository.calculateTotalInventoryValue();
    }

    // Find low-stock products
    public List<Product> getLowStockProducts(Integer threshold) {

        return repository.findByQuantityLessThan(threshold);
    }

    // Get total product count
    public long getTotalProductCount() {
        return repository.count();
    }

    // Calculate total stock units
    public long getTotalStockUnits() {

        return repository.calculateTotalStockUnits();
    }

    public long getCategoryProductCount(String category) {

        return repository.countByCategory(category);
    }

    // Search products by category
    public List<Product> getProductsByCategory(String category) {

        return repository.findByCategoryIgnoreCase(category);
    }

    // Get complete inventory summary
    public InventorySummary getInventorySummary() {

        List<Product> products = repository.findAll();

        long totalProducts = products.size();

        long totalStockUnits = products.stream()
                .mapToLong(product -> product.getQuantity())
                .sum();

        BigDecimal totalInventoryValue = products.stream()
                .map(product -> product.getPrice()
                        .multiply(BigDecimal.valueOf(product.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long lowStockProductCount = products.stream()
                .filter(product -> product.getQuantity() < 5)
                .count();

        return new InventorySummary(
                totalProducts,
                totalStockUnits,
                totalInventoryValue,
                lowStockProductCount
        );
    }
    // Retrieve products with pagination and sorting
    public Page<Product> getProductsPaginated(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
