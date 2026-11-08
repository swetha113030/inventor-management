
package com.example.inventory.repository;

import com.example.inventory.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository repository;

    private Product createProduct(
            String name,
            String category,
            String price,
            int quantity) {

        Product product = new Product();
        product.setName(name);
        product.setCategory(category);
        product.setPrice(new BigDecimal(price));
        product.setQuantity(quantity);

        return repository.save(product);
    }

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        createProduct("Mouse", "Accessories", "20.00", 2);
        createProduct("Keyboard", "Accessories", "30.00", 10);
        createProduct("Monitor", "Electronics", "200.00", 5);

        repository.flush();
    }

    @Test
    void findsProductsBelowQuantityThreshold() {
        List<Product> result =
                repository.findByQuantityLessThan(5);

        assertEquals(1, result.size());
        assertEquals("Mouse", result.get(0).getName());
    }

    @Test
    void findsProductsByCategoryIgnoringCase() {
        List<Product> result =
                repository.findByCategoryIgnoreCase("accessories");

        assertEquals(2, result.size());
    }

    @Test
    void calculatesTotalInventoryValue() {
        BigDecimal result =
                repository.calculateTotalInventoryValue();

        assertEquals(new BigDecimal("1340.00"), result);
    }

    @Test
    void calculatesTotalStockUnits() {
        Long result = repository.calculateTotalStockUnits();

        assertEquals(17L, result);
    }

    @Test
    void countsProductsByCategoryIgnoringCase() {
        Long result = repository.countByCategory("ACCESSORIES");

        assertEquals(2L, result);
    }

    @Test
    void returnsZeroInventoryValueWhenNoProductsExist() {
        repository.deleteAll();
        repository.flush();

        BigDecimal result =
                repository.calculateTotalInventoryValue();

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void returnsZeroStockWhenNoProductsExist() {
        repository.deleteAll();
        repository.flush();

        Long result = repository.calculateTotalStockUnits();

        assertEquals(0L, result);
    }
}
