package com.aura.cosmetics.service;

import com.aura.cosmetics.dto.DashboardStatsDTO;
import com.aura.cosmetics.dto.OrderDTO;
import com.aura.cosmetics.repository.OrderRepository;
import com.aura.cosmetics.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderService orderService;

    public DashboardService(OrderRepository orderRepository, ProductRepository productRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderService = orderService;
    }

    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        Double totalRevenue = orderRepository.calculateTotalRevenue();
        stats.setTotalRevenue(totalRevenue != null ? totalRevenue : 0.0);

        long totalOrders = orderRepository.count();
        stats.setTotalOrders(totalOrders);

        long totalProducts = productRepository.count();
        stats.setTotalProducts(totalProducts);

        long lowStock = productRepository.countLowStockProducts(5);
        stats.setLowStockProducts(lowStock);

        List<OrderDTO> recent = orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .limit(5)
                .map(orderService::toDTO)
                .collect(Collectors.toList());
        stats.setRecentOrders(recent);

        return stats;
    }
}
