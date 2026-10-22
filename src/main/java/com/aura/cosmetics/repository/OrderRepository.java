package com.aura.cosmetics.repository;

import com.aura.cosmetics.entity.Order;
import com.aura.cosmetics.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByOrderByCreatedAtDesc();

    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.total), 0.0) FROM Order o WHERE o.status <> 'ANNULEE'")
    Double calculateTotalRevenue();
}
