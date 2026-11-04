
package com.example.inventory.repository;

import com.example.inventory.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByQuantityLessThan(Integer quantity);

    List<Product> findByCategoryIgnoreCase(String category);

    // Calculate total inventory value directly in the database
    @Query("SELECT COALESCE(SUM(p.price * p.quantity), 0) FROM Product p")
    BigDecimal calculateTotalInventoryValue();

    // Calculate total stock units
    @Query("SELECT COALESCE(SUM(p.quantity), 0) FROM Product p")
    Long calculateTotalStockUnits();

    // Count products in a particular category
    @Query("SELECT COUNT(p) FROM Product p WHERE LOWER(p.category) = LOWER(:category)")
    Long countByCategory(@Param("category") String category);
}
