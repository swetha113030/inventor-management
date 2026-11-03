package com.example.inventory.dto;

import java.math.BigDecimal;

public record InventorySummary(
        long totalProducts,
        long totalStockUnits,
        BigDecimal totalInventoryValue,
        long lowStockProductCount
) {
}
