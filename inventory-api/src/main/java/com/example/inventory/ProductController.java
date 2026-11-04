
package com.example.inventory;

import com.example.inventory.dto.InventorySummary;
import com.example.inventory.entity.Product;
import com.example.inventory.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // GET: Retrieve all products
    @GetMapping
    public List<Product> getAllProducts() {
        return service.getAllProducts();
    }

    // GET: Retrieve product by ID
    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return service.getProductById(id);
    }

    // POST: Add a new product
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product addProduct(@Valid @RequestBody Product product) {
        return service.addProduct(product);
    }

    // PUT: Update an existing product
    @PutMapping("/{id}")
    public Product updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody Product updatedProduct) {

        return service.updateProduct(id, updatedProduct);
    }

    // DELETE: Delete a product
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        service.deleteProduct(id);
    }

    // GET: Calculate total inventory value
    @GetMapping("/inventory-value")
    public BigDecimal getTotalInventoryValue() {
        return service.getTotalInventoryValue();
    }

    // GET: Retrieve low-stock products
    @GetMapping("/low-stock")
    public List<Product> getLowStockProducts(
            @RequestParam(defaultValue = "5") Integer threshold) {

        return service.getLowStockProducts(threshold);
    }

    // GET: Get total number of products
    @GetMapping("/count")
    public long getTotalProductCount() {
        return service.getTotalProductCount();
    }

    // GET: Calculate total stock units
    @GetMapping("/total-stock")
    public long getTotalStockUnits() {
        return service.getTotalStockUnits();
    }

    // GET: Search products by category
    @GetMapping("/category/{category}")
    public List<Product> getProductsByCategory(
            @PathVariable String category) {

        return service.getProductsByCategory(category);
    }

    // GET: Complete inventory summary
    @GetMapping("/inventory-summary")
    public InventorySummary getInventorySummary() {
        return service.getInventorySummary();
    }

    @GetMapping("/category/{category}/count")
    public long getCategoryProductCount(
            @PathVariable String category) {

        return service.getCategoryProductCount(category);
    }
    @GetMapping("/page")
    public Page<Product> getProductsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return service.getProductsPaginated(pageable);
    }
}
