package com.aura.cosmetics.dto;

public class OrderItemDTO {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double unitPrice;
    private Boolean isWholesale;
    private Integer wholesaleStock;

    public OrderItemDTO() {}

    public OrderItemDTO(Long productId, String productName, Integer quantity, Double unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.isWholesale = false;
    }

    public OrderItemDTO(Long productId, String productName, Integer quantity, Double unitPrice, Boolean isWholesale, Integer wholesaleStock) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.isWholesale = isWholesale;
        this.wholesaleStock = wholesaleStock;
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
        return isWholesale;
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
}
