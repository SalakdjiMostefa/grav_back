package com.aura.cosmetics.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double unitPrice;

    @Column(name = "is_wholesale")
    private Boolean isWholesale = false;

    @Column(name = "wholesale_stock")
    private Integer wholesaleStock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    public OrderItem() {}

    public OrderItem(Long productId, String productName, Integer quantity, Double unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.isWholesale = false;
    }

    public OrderItem(Long productId, String productName, Integer quantity, Double unitPrice, Boolean isWholesale, Integer wholesaleStock) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.isWholesale = isWholesale != null ? isWholesale : false;
        this.wholesaleStock = wholesaleStock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Boolean getIsWholesale() {
        return isWholesale != null ? isWholesale : false;
    }

    public void setIsWholesale(Boolean isWholesale) {
        this.isWholesale = isWholesale;
    }

    public Integer getWholesaleStock() {
        return wholesaleStock;
    }

    public void setWholesaleStock(Integer wholesaleStock) {
        this.wholesaleStock = wholesaleStock;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
