package com.aura.cosmetics.entity;

import com.aura.cosmetics.enums.PriceType;
import jakarta.persistence.*;

@Entity
@Table(name = "product_prices")
public class ProductPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PriceType type;

    @Column(nullable = false)
    private Double price;

    /** Nombre de pieces par unite de vente (1 pour detail, N pour carton gros) */
    @Column(nullable = false)
    private Integer minQty = 1;

    /** Libelle affiche dans admin et sur les factures */
    @Column(length = 120)
    private String label;

    public ProductPrice() {}

    public ProductPrice(PriceType type, Double price, Integer minQty, String label) {
        this.type = type;
        this.price = price;
        this.minQty = minQty;
        this.label = label;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public PriceType getType() { return type; }
    public void setType(PriceType type) { this.type = type; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getMinQty() { return minQty; }
    public void setMinQty(Integer minQty) { this.minQty = minQty; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}
