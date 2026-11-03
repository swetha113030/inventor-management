
package com.example.inventory.repository;

import com.example.inventory.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find products where quantity is below the given threshold
    List<Product> findByQuantityLessThan(Integer quantity);

    // Find products by category, ignoring uppercase/lowercase differences
    List<Product> findByCategoryIgnoreCase(String category);
}
