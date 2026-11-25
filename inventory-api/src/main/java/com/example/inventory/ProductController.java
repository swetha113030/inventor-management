
package com.example.inventory;

import com.example.inventory.dto.InventorySummary;
import com.example.inventory.entity.Product;
import com.example.inventory.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Management", description = "APIs for managing products and inventory")
public class ProductController {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "name",
            "category",
            "price",
            "quantity"
    );

    private static final int MAX_PAGE_SIZE = 100;

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @Operation(
            summary = "Get all products",
            description = "Retrieves a list of all products available in the inventory."
    )
    @GetMapping
    public List<Product> getAllProducts() {
        return service.getAllProducts();
    }

    @Operation(
            summary = "Get product by ID",
            description = "Retrieves a specific product using its unique ID."
    )
    @GetMapping("/{id}")
    public Product getProduct(
            @Parameter(description = "Unique ID of the product")
            @PathVariable Long id) {
        return service.getProductById(id);
    }

    @Operation(
            summary = "Create a new product",
            description = "Adds a new product to the inventory. Product details are validated."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product addProduct(@Valid @RequestBody Product product) {
        return service.addProduct(product);
    }

    @Operation(
            summary = "Update a product",
            description = "Updates the details of an existing product using its ID."
    )
    @PutMapping("/{id}")
    public Product updateProduct(
            @Parameter(description = "ID of the product to update")
            @PathVariable Long id,
            @Valid @RequestBody Product updatedProduct) {

        return service.updateProduct(id, updatedProduct);
    }

    @Operation(
            summary = "Delete a product",
            description = "Deletes a product from the inventory using its ID."
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(
            @Parameter(description = "ID of the product to delete")
            @PathVariable Long id) {
        service.deleteProduct(id);
    }

    @Operation(
            summary = "Get total inventory value",
            description = "Calculates the total monetary value of all products in stock."
    )
    @GetMapping("/inventory-value")
    public BigDecimal getTotalInventoryValue() {
        return service.getTotalInventoryValue();
    }

    @GetMapping("/low-stock")
    @Operation(
            summary = "Get low-stock products",
            description = "Retrieves products whose stock quantity is below the specified threshold."
    )
    public List<Product> getLowStockProducts(
            @Parameter(description = "Stock threshold. Defaults to 5.", example = "5")
            @RequestParam(defaultValue = "5") Integer threshold) {

        if (threshold < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Stock threshold cannot be negative"
            );
        }

        return service.getLowStockProducts(threshold);
    }

    @Operation(
            summary = "Get total product count",
            description = "Returns the total number of products in the inventory."
    )
    @GetMapping("/count")
    public long getTotalProductCount() {
        return service.getTotalProductCount();
    }

    @Operation(
            summary = "Get total stock units",
            description = "Calculates the total quantity of all stock units across products."
    )
    @GetMapping("/total-stock")
    public long getTotalStockUnits() {
        return service.getTotalStockUnits();
    }

    @Operation(
            summary = "Get products by category",
            description = "Retrieves all products belonging to the specified category."
    )
    @GetMapping("/category/{category}")
    public List<Product> getProductsByCategory(
            @Parameter(description = "Product category, for example Electronics")
            @PathVariable String category) {

        return service.getProductsByCategory(category);
    }

    @Operation(
            summary = "Get inventory summary",
            description = "Returns an overview containing product count, stock units, inventory value and low-stock count."
    )
    @GetMapping("/inventory-summary")
    public InventorySummary getInventorySummary() {
        return service.getInventorySummary();
    }

    @Operation(
            summary = "Get product count by category",
            description = "Returns the number of products belonging to a specific category."
    )
    @GetMapping("/category/{category}/count")
    public long getCategoryProductCount(
            @Parameter(description = "Category to count")
            @PathVariable String category) {

        return service.getCategoryProductCount(category);
    }

    @GetMapping("/page")
    @Operation(
            summary = "Get paginated products",
            description = "Retrieves products page by page with configurable sorting."
    )
    public Page<Product> getProductsPaginated(

            @Parameter(description = "Page number (starts from 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of products per page (maximum 100)", example = "5")
            @RequestParam(defaultValue = "5") int size,

            @Parameter(description = "Field used for sorting: id, name, category, price or quantity")
            @RequestParam(defaultValue = "id") String sortBy,

            @Parameter(description = "Sort direction: asc or desc")
            @RequestParam(defaultValue = "asc") String direction) {

        if (page < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page number cannot be negative"
            );
        }

        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page size must be between 1 and " + MAX_PAGE_SIZE
            );
        }

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid sort field. Allowed fields: " + ALLOWED_SORT_FIELDS
            );
        }

        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Sort direction must be either asc or desc"
            );
        }

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return service.getProductsPaginated(pageable);
    }


    @Operation(
            summary = "Search and filter products",
            description = """
                    Searches products using optional name, category, price range,
                    and stock availability filters. Results are paginated and sortable.
                    """
    )
    @GetMapping("/search")
    public Page<Product> searchProducts(

            @Parameter(description = "Product name to search for", example = "mouse")
            @RequestParam(required = false) String name,

            @Parameter(description = "Product category", example = "Accessories")
            @RequestParam(required = false) String category,

            @Parameter(description = "Minimum product price", example = "10.00")
            @RequestParam(required = false) BigDecimal minPrice,

            @Parameter(description = "Maximum product price", example = "100.00")
            @RequestParam(required = false) BigDecimal maxPrice,

            @Parameter(description = "Filter by stock availability", example = "true")
            @RequestParam(required = false) Boolean inStock,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size,

            @RequestParam(defaultValue = "id") String sortBy,

            @RequestParam(defaultValue = "asc") String direction) {

        if (page < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page number cannot be negative"
            );
        }

        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page size must be between 1 and " + MAX_PAGE_SIZE
            );
        }

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid sort field"
            );
        }

        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Sort direction must be asc or desc"
            );
        }

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return service.searchProducts(
                name,
                category,
                minPrice,
                maxPrice,
                inStock,
                pageable
        );
    }

}
