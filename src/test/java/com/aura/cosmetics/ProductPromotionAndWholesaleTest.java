package com.aura.cosmetics;

import com.aura.cosmetics.dto.ProductDTO;
import com.aura.cosmetics.entity.OrderItem;
import com.aura.cosmetics.entity.Product;
import com.aura.cosmetics.enums.PromotionType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProductPromotionAndWholesaleTest {

    @Test
    void testPromotionPercentageActive() {
        Product p = new Product();
        p.setPrice(100.0);
        p.setPromotionType(PromotionType.PERCENTAGE);
        p.setPromotionValue(20.0); // 20%
        p.setPromotionStartDate(LocalDateTime.now().minusDays(1)); // Started yesterday

        assertTrue(p.isPromotionActive(), "Promotion should be active when start date is in the past");
        assertEquals(80.0, p.getDiscountedPrice(), 0.001, "Discounted price should be 80.0 for 20% off 100.0");
    }

    @Test
    void testPromotionPercentageFutureActivation() {
        Product p = new Product();
        p.setPrice(100.0);
        p.setPromotionType(PromotionType.PERCENTAGE);
        p.setPromotionValue(20.0);
        p.setPromotionStartDate(LocalDateTime.now().plusDays(2)); // Activates in 2 days

        assertFalse(p.isPromotionActive(), "Promotion should NOT be active when start date is in the future");
        assertEquals(100.0, p.getDiscountedPrice(), 0.001, "Discounted price should remain standard price when not active");
    }

    @Test
    void testPromotionFixedAmountActive() {
        Product p = new Product();
        p.setPrice(1500.0);
        p.setPromotionType(PromotionType.FIXED_AMOUNT);
        p.setPromotionValue(300.0); // 300 DZD off
        p.setPromotionStartDate(LocalDateTime.now().minusHours(2));

        assertTrue(p.isPromotionActive());
        assertEquals(1200.0, p.getDiscountedPrice(), 0.001, "Discounted price should be 1200.0 for 300 DZD off 1500.0");
    }

    @Test
    void testPromotionFixedAmountCannotExceedPrice() {
        Product p = new Product();
        p.setPrice(200.0);
        p.setPromotionType(PromotionType.FIXED_AMOUNT);
        p.setPromotionValue(500.0); // discount greater than price
        p.setPromotionStartDate(LocalDateTime.now().minusHours(1));

        assertTrue(p.isPromotionActive());
        assertEquals(0.0, p.getDiscountedPrice(), 0.001, "Discounted price should never be negative");
    }

    @Test
    void testWholesaleStockLinkedWithCartons() {
        Product p = new Product();
        p.setWholesaleMinQty(12);
        p.setWholesaleStock(8); // 8 cartons

        assertEquals(8, p.getStock().getCartons(), "Wholesale stock setter should set cartons in stock");
        assertEquals(8, p.getWholesaleStock(), "Wholesale stock getter should return cartons from stock");
        assertEquals(96, p.getStock().getTotalUnits(), "8 cartons of 12 pcs should equal 96 total units");
    }

    @Test
    void testOrderItemWholesaleStock() {
        OrderItem item = new OrderItem(1L, "Élixir Céleste", 24, 2500.0, true, 5);

        assertTrue(item.getIsWholesale(), "OrderItem should preserve wholesale flag");
        assertEquals(5, item.getWholesaleStock(), "OrderItem should store wholesale stock cartons");
        assertEquals(24, item.getQuantity());
        assertEquals(2500.0, item.getUnitPrice());
    }
}