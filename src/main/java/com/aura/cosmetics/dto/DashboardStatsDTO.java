package com.aura.cosmetics.dto;

import java.util.ArrayList;
import java.util.List;

public class DashboardStatsDTO {
    private Double totalRevenue = 0.0;
    private Long totalOrders = 0L;
    private Long totalProducts = 0L;
    private Long lowStockProducts = 0L;
    private List<OrderDTO> recentOrders = new ArrayList<>();

    public DashboardStatsDTO() {}

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(Long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public Long getLowStockProducts() {
        return lowStockProducts;
    }

    public void setLowStockProducts(Long lowStockProducts) {
        this.lowStockProducts = lowStockProducts;
    }

    public List<OrderDTO> getRecentOrders() {
        return recentOrders;
    }

    public void setRecentOrders(List<OrderDTO> recentOrders) {
        this.recentOrders = recentOrders;
    }
}
