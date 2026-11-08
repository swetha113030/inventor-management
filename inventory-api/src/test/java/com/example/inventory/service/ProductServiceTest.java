
package com.example.inventory.service;

import com.example.inventory.dto.InventorySummary;
import com.example.inventory.entity.Product;
import com.example.inventory.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Wireless Mouse");
        product.setCategory("Accessories");
        product.setPrice(new BigDecimal("20.00"));
        product.setQuantity(10);
    }

    @Test
    void getsAllProducts() {
        when(repository.findAll()).thenReturn(List.of(product));

        List<Product> result = service.getAllProducts();

        assertEquals(1, result.size());
        assertEquals("Wireless Mouse", result.get(0).getName());

        verify(repository).findAll();
    }

    @Test
    void getsProductById() {
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        Product result = service.getProductById(1L);

        assertEquals("Wireless Mouse", result.getName());
        assertEquals(1L, result.getId());
    }

    @Test
    void throwsExceptionWhenProductDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.getProductById(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void addsProduct() {
        when(repository.save(product)).thenReturn(product);

        Product result = service.addProduct(product);

        assertEquals("Wireless Mouse", result.getName());
        verify(repository).save(product);
    }

    @Test
    void updatesProduct() {
        Product updatedProduct = new Product();
        updatedProduct.setName("Gaming Mouse");
        updatedProduct.setCategory("Gaming");
        updatedProduct.setPrice(new BigDecimal("45.00"));
        updatedProduct.setQuantity(20);

        when(repository.findById(1L)).thenReturn(Optional.of(product));
        when(repository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Product result = service.updateProduct(1L, updatedProduct);

        assertEquals("Gaming Mouse", result.getName());
        assertEquals("Gaming", result.getCategory());
        assertEquals(new BigDecimal("45.00"), result.getPrice());
        assertEquals(20, result.getQuantity());

        verify(repository).save(product);
    }

    @Test
    void deletesProduct() {
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        service.deleteProduct(1L);

        verify(repository).delete(product);
    }

    @Test
    void getsTotalInventoryValue() {
        when(repository.calculateTotalInventoryValue())
                .thenReturn(new BigDecimal("200.00"));

        BigDecimal result = service.getTotalInventoryValue();

        assertEquals(new BigDecimal("200.00"), result);
    }

    @Test
    void getsLowStockProducts() {
        product.setQuantity(2);

        when(repository.findByQuantityLessThan(5))
                .thenReturn(List.of(product));

        List<Product> result = service.getLowStockProducts(5);

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getQuantity());
    }

    @Test
    void getsTotalProductCount() {
        when(repository.count()).thenReturn(5L);

        long result = service.getTotalProductCount();

        assertEquals(5L, result);
    }

    @Test
    void getsTotalStockUnits() {
        when(repository.calculateTotalStockUnits()).thenReturn(50L);

        long result = service.getTotalStockUnits();

        assertEquals(50L, result);
    }

    @Test
    void getsCategoryProductCount() {
        when(repository.countByCategory("Accessories"))
                .thenReturn(3L);

        long result = service.getCategoryProductCount("Accessories");

        assertEquals(3L, result);
    }

    @Test
    void getsProductsByCategory() {
        when(repository.findByCategoryIgnoreCase("Accessories"))
                .thenReturn(List.of(product));

        List<Product> result =
                service.getProductsByCategory("Accessories");

        assertEquals(1, result.size());
    }

    @Test
    void calculatesInventorySummary() {
        Product secondProduct = new Product();
        secondProduct.setId(2L);
        secondProduct.setName("Keyboard");
        secondProduct.setCategory("Accessories");
        secondProduct.setPrice(new BigDecimal("30.00"));
        secondProduct.setQuantity(2);

        when(repository.findAll())
                .thenReturn(List.of(product, secondProduct));

        InventorySummary result = service.getInventorySummary();

        assertEquals(2, result.totalProducts());
        assertEquals(12, result.totalStockUnits());
        assertEquals(
                new BigDecimal("260.00"),
                result.totalInventoryValue()
        );
        assertEquals(1, result.lowStockProductCount());
    }

    @Test
    void getsPaginatedProducts() {
        Pageable pageable = PageRequest.of(0, 5);

        Page<Product> expectedPage =
                new PageImpl<>(List.of(product));

        when(repository.findAll(pageable)).thenReturn(expectedPage);

        Page<Product> result = service.getProductsPaginated(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Wireless Mouse",
                result.getContent().get(0).getName());

        verify(repository).findAll(pageable);
    }
}
