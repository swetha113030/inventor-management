package com.example.inventory;

import com.example.inventory.entity.Product;
import com.example.inventory.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private Product createProduct(String name, String category,
                                  String price, int quantity) {
        Product product = new Product();
        product.setName(name);
        product.setCategory(category);
        product.setPrice(new BigDecimal(price));
        product.setQuantity(quantity);
        return repository.save(product);
    }

    // 1. Get all products
    @Test
    void getProductsReturnsOk() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // 2. Create a valid product
    @Test
    void createsProduct() throws Exception {
        String json = """
                {
                  "name": "Test Keyboard",
                  "category": "Accessories",
                  "price": 29.99,
                  "quantity": 8
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Keyboard"))
                .andExpect(jsonPath("$.category").value("Accessories"))
                .andExpect(jsonPath("$.quantity").value(8));
    }

    // 3. Reject negative quantity
    @Test
    void rejectsNegativeQuantity() throws Exception {
        String json = """
                {
                  "name": "Mouse",
                  "category": "Accessories",
                  "price": 19.99,
                  "quantity": -5
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.quantity")
                        .value("Quantity cannot be negative"));
    }

    // 4. Reject blank product name
    @Test
    void rejectsBlankProductName() throws Exception {
        String json = """
                {
                  "name": "",
                  "category": "Accessories",
                  "price": 19.99,
                  "quantity": 5
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name")
                        .value("Product name is required"));
    }

    // 5. Reject zero price
    @Test
    void rejectsZeroPrice() throws Exception {
        String json = """
                {
                  "name": "Mouse",
                  "category": "Accessories",
                  "price": 0,
                  "quantity": 5
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    // 6. Get product by ID
    @Test
    void getsProductById() throws Exception {
        Product product = createProduct(
                "Wireless Mouse", "Accessories", "19.99", 10);

        mockMvc.perform(get("/api/products/{id}", product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Wireless Mouse"))
                .andExpect(jsonPath("$.quantity").value(10));
    }

    // 7. Return 404 when product does not exist
    @Test
    void returnsNotFoundForMissingProduct() throws Exception {
        mockMvc.perform(get("/api/products/999999"))
                .andExpect(status().isNotFound());
    }

    // 8. Update product
    @Test
    void updatesProduct() throws Exception {
        Product product = createProduct(
                "Old Mouse", "Accessories", "15.00", 5);

        String json = """
                {
                  "name": "Updated Mouse",
                  "category": "Electronics",
                  "price": 25.99,
                  "quantity": 15
                }
                """;

        mockMvc.perform(put("/api/products/{id}", product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Mouse"))
                .andExpect(jsonPath("$.category").value("Electronics"))
                .andExpect(jsonPath("$.quantity").value(15));
    }

    // 9. Delete product
    @Test
    void deletesProduct() throws Exception {
        Product product = createProduct(
                "Temporary Product", "Accessories", "10.00", 3);

        mockMvc.perform(delete("/api/products/{id}", product.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/products/{id}", product.getId()))
                .andExpect(status().isNotFound());
    }

    // 10. Get low-stock products
    @Test
    void returnsLowStockProducts() throws Exception {
        createProduct("Low Stock Mouse", "Accessories", "20.00", 2);
        createProduct("Available Keyboard", "Accessories", "30.00", 10);

        mockMvc.perform(get("/api/products/low-stock")
                        .param("threshold", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name")
                        .value("Low Stock Mouse"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    // 11. Calculate inventory value
    @Test
    void calculatesInventoryValue() throws Exception {
        createProduct("Product A", "Electronics", "10.00", 2);
        createProduct("Product B", "Accessories", "5.00", 3);

        mockMvc.perform(get("/api/products/inventory-value"))
                .andExpect(status().isOk())
                .andExpect(content().string("35.00"));
    }

    // 12. Get inventory summary
    @Test
    void returnsInventorySummary() throws Exception {
        createProduct("Product A", "Electronics", "10.00", 2);
        createProduct("Product B", "Accessories", "5.00", 10);

        mockMvc.perform(get("/api/products/inventory-summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProducts").value(2))
                .andExpect(jsonPath("$.totalStockUnits").value(12))
                .andExpect(jsonPath("$.totalInventoryValue").value(70.00))
                .andExpect(jsonPath("$.lowStockProductCount").value(1));
    }

    // 13. Get total product count
    @Test
    void returnsTotalProductCount() throws Exception {
        createProduct("Product A", "Electronics", "10.00", 2);
        createProduct("Product B", "Accessories", "5.00", 10);

        mockMvc.perform(get("/api/products/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    // 14. Get total stock units
    @Test
    void returnsTotalStockUnits() throws Exception {
        createProduct("Product A", "Electronics", "10.00", 2);
        createProduct("Product B", "Accessories", "5.00", 10);

        mockMvc.perform(get("/api/products/total-stock"))
                .andExpect(status().isOk())
                .andExpect(content().string("12"));
    }

    // 15. Search products by category
    @Test
    void returnsProductsByCategory() throws Exception {
        createProduct("Mouse", "Accessories", "20.00", 5);
        createProduct("Keyboard", "Accessories", "30.00", 8);
        createProduct("Monitor", "Electronics", "200.00", 3);

        mockMvc.perform(get("/api/products/category/Accessories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // 16. Count products by category
    @Test
    void returnsCategoryProductCount() throws Exception {
        createProduct("Mouse", "Accessories", "20.00", 5);
        createProduct("Keyboard", "Accessories", "30.00", 8);

        mockMvc.perform(
                        get("/api/products/category/Accessories/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    // 17. Test pagination and sorting
    @Test
    void returnsPaginatedProducts() throws Exception {
        createProduct("Mouse", "Accessories", "20.00", 5);
        createProduct("Keyboard", "Accessories", "30.00", 8);
        createProduct("Monitor", "Electronics", "200.00", 3);

        mockMvc.perform(get("/api/products/page")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sortBy", "id")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(3));
    }
}
