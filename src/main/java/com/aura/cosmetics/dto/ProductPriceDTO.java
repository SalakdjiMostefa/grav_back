package com.aura.cosmetics.dto;

import com.aura.cosmetics.enums.PriceType;

public class ProductPriceDTO {
    private Long id;
    private PriceType type;
    private Double price;
    private Integer minQty;
    private String label;

    public ProductPriceDTO() {}

    public ProductPriceDTO(Long id, PriceType type, Double price, Integer minQty, String label) {
        this.id = id;
        this.type = type;
        this.price = price;
        this.minQty = minQty;
        this.label = label;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PriceType getType() { return type; }
    public void setType(PriceType type) { this.type = type; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getMinQty() { return minQty; }
    public void setMinQty(Integer minQty) { this.minQty = minQty; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}
