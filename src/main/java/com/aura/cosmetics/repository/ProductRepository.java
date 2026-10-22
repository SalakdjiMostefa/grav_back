package com.aura.cosmetics.repository;

import com.aura.cosmetics.entity.Product;
import com.aura.cosmetics.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStatus(ProductStatus status);

    List<Product> findByCategoryId(Long categoryId);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.stock <= :threshold")
    long countLowStockProducts(int threshold);
}
