
package com.example.inventory.service;

import com.example.inventory.dto.InventorySummary;
import com.example.inventory.entity.Product;
import com.example.inventory.repository.ProductRepository;
import com.example.inventory.repository.ProductSpecifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private static final Logger logger =
            LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    // Get all products
    public List<Product> getAllProducts() {
        logger.info("Fetching all products");

        List<Product> products = repository.findAll();

        logger.info("Successfully retrieved {} products", products.size());

        return products;
    }

    // Get product by ID
    public Product getProductById(Long id) {
        logger.info("Fetching product with ID: {}", id);

        return repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Product not found with ID: {}", id);

                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Product not found");
                });
    }

    // Add a new product
    public Product addProduct(Product product) {
        logger.info("Adding new product: {}", product.getName());

        Product savedProduct = repository.save(product);

        logger.info("Product created successfully with ID: {}",
                savedProduct.getId());

        return savedProduct;
    }

    // Update an existing product
    public Product updateProduct(Long id, Product updatedProduct) {
        logger.info("Updating product with ID: {}", id);

        Product existing = getProductById(id);

        existing.setName(updatedProduct.getName());
        existing.setCategory(updatedProduct.getCategory());
        existing.setPrice(updatedProduct.getPrice());
        existing.setQuantity(updatedProduct.getQuantity());

        Product savedProduct = repository.save(existing);

        logger.info("Product updated successfully with ID: {}", id);

        return savedProduct;
    }

    // Delete a product
    public void deleteProduct(Long id) {
        logger.info("Deleting product with ID: {}", id);

        Product existing = getProductById(id);

        repository.delete(existing);

        logger.info("Product deleted successfully with ID: {}", id);
    }

    // Calculate total inventory value
    public BigDecimal getTotalInventoryValue() {
        logger.info("Calculating total inventory value");

        BigDecimal totalValue =
                repository.calculateTotalInventoryValue();

        logger.info("Total inventory value calculated: {}", totalValue);

        return totalValue;
    }

    // Find low-stock products
    public List<Product> getLowStockProducts(Integer threshold) {
        logger.info("Fetching products with stock below threshold: {}",
                threshold);

        List<Product> products =
                repository.findByQuantityLessThan(threshold);

        logger.warn("Found {} products below stock threshold",
                products.size());

        return products;
    }

    // Get total product count
    public long getTotalProductCount() {
        logger.info("Counting total products");

        long count = repository.count();

        logger.info("Total product count: {}", count);

        return count;
    }

    // Calculate total stock units
    public long getTotalStockUnits() {
        logger.info("Calculating total stock units");

        long totalStock = repository.calculateTotalStockUnits();

        logger.info("Total stock units: {}", totalStock);

        return totalStock;
    }

    // Get category product count
    public long getCategoryProductCount(String category) {
        logger.info("Counting products in category: {}", category);

        long count = repository.countByCategory(category);

        logger.info("Category {} contains {} products", category, count);

        return count;
    }

    // Search products by category
    public List<Product> getProductsByCategory(String category) {
        logger.info("Fetching products in category: {}", category);

        List<Product> products =
                repository.findByCategoryIgnoreCase(category);

        logger.info("Found {} products in category: {}",
                products.size(), category);

        return products;
    }


    // Get complete inventory summary using database aggregation
    public InventorySummary getInventorySummary() {

        logger.info("Generating inventory summary using database aggregation");

        long totalProducts = repository.count();

        long totalStockUnits = repository.calculateTotalStockUnits();

        BigDecimal totalInventoryValue =
                repository.calculateTotalInventoryValue();

        long lowStockProductCount =
                repository.countByQuantityLessThan(5);

        InventorySummary summary = new InventorySummary(
                totalProducts,
                totalStockUnits,
                totalInventoryValue,
                lowStockProductCount
        );

        logger.info(
                "Inventory summary generated. Products: {}, Stock units: {}, Value: {}, Low stock: {}",
                totalProducts,
                totalStockUnits,
                totalInventoryValue,
                lowStockProductCount
        );

        return summary;
    }


    // Retrieve products with pagination and sorting
    public Page<Product> getProductsPaginated(Pageable pageable) {
        logger.info("Fetching products with pagination. Page: {}, Size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Product> result = repository.findAll(pageable);

        logger.info("Retrieved {} products on page {}",
                result.getNumberOfElements(), result.getNumber());

        return result;
    }

    // Search products using multiple optional filters
    public Page<Product> searchProducts(
            String name,
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock,
            Pageable pageable) {

        logger.info(
                "Searching products. Name: {}, Category: {}, Min price: {}, Max price: {}, In stock: {}",
                name, category, minPrice, maxPrice, inStock
        );

        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Minimum price cannot be negative"
            );
        }

        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Maximum price cannot be negative"
            );
        }

        if (minPrice != null && maxPrice != null
                && minPrice.compareTo(maxPrice) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Minimum price cannot be greater than maximum price"
            );
        }

        Page<Product> result = repository.findAll(
                ProductSpecifications.filterProducts(
                        name, category, minPrice, maxPrice, inStock
                ),
                pageable
        );

        logger.info("Found {} matching products", result.getTotalElements());

        return result;
    }

}
