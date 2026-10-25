package com.aura.cosmetics.entity;

import com.aura.cosmetics.enums.ProductStatus;
import com.aura.cosmetics.enums.PromotionType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String brand;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** Prix de vente au detail (public) - conserve pour compatibilite rapide */
    @Column(nullable = false)
    private Double price;

    /** Prix grossiste unitaire - conserve pour compatibilite rapide */
    private Double wholesalePrice;

    /** Nombre de pieces dans un carton grossiste */
    private Integer wholesaleMinQty = 12;

    /** Notion de promotion sur le produit */
    @Enumerated(EnumType.STRING)
    @Column(name = "promotion_type")
    private PromotionType promotionType;

    /** Valeur de promotion : pourcentage (ex: 20) ou valeur concrete / reduction en DZD (ex: 500) */
    @Column(name = "promotion_value")
    private Double promotionValue;

    /** Date d'activation de la promotion */
    @Column(name = "promotion_start_date")
    private LocalDateTime promotionStartDate;

    /** Date de fin de promotion (optionnelle) */
    @Column(name = "promotion_end_date")
    private LocalDateTime promotionEndDate;

    /** Liste structuree des prix (DETAIL + GROS + paliers futurs) */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ProductPrice> prices = new ArrayList<>();

    @Embedded
    private ProductStock stock = new ProductStock();

    private Double rating = 5.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.EN_STOCK;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("displayOrder ASC")
    private List<ProductImage> images = new ArrayList<>();

    public Product() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Double getWholesalePrice() { return wholesalePrice; }
    public void setWholesalePrice(Double wholesalePrice) { this.wholesalePrice = wholesalePrice; }
    public Integer getWholesaleMinQty() { return wholesaleMinQty; }
    public void setWholesaleMinQty(Integer wholesaleMinQty) { this.wholesaleMinQty = wholesaleMinQty; }

    public PromotionType getPromotionType() { return promotionType; }
    public void setPromotionType(PromotionType promotionType) { this.promotionType = promotionType; }
    public Double getPromotionValue() { return promotionValue; }
    public void setPromotionValue(Double promotionValue) { this.promotionValue = promotionValue; }
    public LocalDateTime getPromotionStartDate() { return promotionStartDate; }
    public void setPromotionStartDate(LocalDateTime promotionStartDate) { this.promotionStartDate = promotionStartDate; }
    public LocalDateTime getPromotionEndDate() { return promotionEndDate; }
    public void setPromotionEndDate(LocalDateTime promotionEndDate) { this.promotionEndDate = promotionEndDate; }

    /** Verifie si la promotion est actuellement active en fonction de sa date d'activation et eventuelle date de fin */
    public boolean isPromotionActive() {
        if (promotionType == null || promotionValue == null || promotionValue <= 0) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        if (promotionStartDate != null && now.isBefore(promotionStartDate)) {
            return false;
        }
        if (promotionEndDate != null && now.isAfter(promotionEndDate)) {
            return false;
        }
        return true;
    }

    /** Calcule le prix remisé selon le type (pourcentage ou valeur concrète) */
    public Double getDiscountedPrice() {
        if (price == null) return 0.0;
        if (!isPromotionActive()) {
            return price;
        }
        if (promotionType == PromotionType.PERCENTAGE) {
            double discounted = price * (1.0 - (promotionValue / 100.0));
            return Math.max(0.0, Math.round(discounted * 100.0) / 100.0);
        } else if (promotionType == PromotionType.FIXED_AMOUNT) {
            double discounted = price - promotionValue;
            return Math.max(0.0, Math.round(discounted * 100.0) / 100.0);
        }
        return price;
    }

    /** Stock en gros (cartons) directement lie au stock produit */
    public Integer getWholesaleStock() {
        return getStock().getCartons();
    }

    public void setWholesaleStock(Integer wholesaleStock) {
        if (this.stock == null) {
            this.stock = new ProductStock();
        }
        this.stock.setCartons(wholesaleStock != null ? Math.max(0, wholesaleStock) : 0);
    }

    public List<ProductPrice> getPrices() { return prices; }
    public void setPrices(List<ProductPrice> prices) {
        this.prices.clear();
        if (prices != null) {
            for (ProductPrice pp : prices) {
                addPrice(pp);
            }
        }
    }
    public void addPrice(ProductPrice pp) {
        this.prices.add(pp);
        pp.setProduct(this);
    }
    public void removePrice(ProductPrice pp) {
        this.prices.remove(pp);
        pp.setProduct(null);
    }
    public ProductStock getStock() {
        if (this.stock == null) {
            this.stock = new ProductStock();
        }
        return this.stock;
    }
    public void setStock(ProductStock stock) {
        this.stock = stock;
    }
    public void setStock(Integer totalUnits) {
        if (this.stock == null) {
            this.stock = new ProductStock();
        }
        this.stock.setTotalUnits(totalUnits != null ? totalUnits : 0);
    }
    public Integer getTotalStock() {
        return getStock().getTotalUnits();
    }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public List<ProductImage> getImages() { return images; }
    public void setImages(List<ProductImage> images) {
        this.images.clear();
        if (images != null) {
            for (ProductImage img : images) {
                addImage(img);
            }
        }
    }
    public void addImage(ProductImage image) {
        this.images.add(image);
        image.setProduct(this);
    }
    public void removeImage(ProductImage image) {
        this.images.remove(image);
        image.setProduct(null);
    }
}
