package com.aura.cosmetics.dto;

import com.aura.cosmetics.enums.ProductStatus;
import com.aura.cosmetics.enums.PromotionType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductDTO {
    private Long id;
    private String name;
    private String brand;
    private String description;
    private Double price;
    private Double wholesalePrice;
    private Integer wholesaleMinQty;
    /** Stock en gros (cartons) lie directement au stock */
    private Integer wholesaleStock;

    /** Type de promotion : PERCENTAGE ou FIXED_AMOUNT */
    private PromotionType promotionType;
    /** Valeur de promotion (ex: 20% ou 500 DZD) */
    private Double promotionValue;
    /** Date d'activation de la promotion */
    private LocalDateTime promotionStartDate;
    /** Date de fin de la promotion (optionnelle) */
    private LocalDateTime promotionEndDate;
    /** Indicateur si la promotion est active actuellement */
    private Boolean promotionActive;
    /** Prix calcule apres reduction / promotion */
    private Double discountedPrice;

    /** Liste structuree de tous les prix (DETAIL, GROS, etc.) */
    private List<ProductPriceDTO> prices = new ArrayList<>();
    private ProductStockDTO stock = new ProductStockDTO();
    private Object category;
    private List<ProductImageDTO> images = new ArrayList<>();
    private Double rating;
    private ProductStatus status;

    public ProductDTO() {}

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
    public Integer getWholesaleStock() { return wholesaleStock; }
    public void setWholesaleStock(Integer wholesaleStock) { this.wholesaleStock = wholesaleStock; }

    public PromotionType getPromotionType() { return promotionType; }
    public void setPromotionType(PromotionType promotionType) { this.promotionType = promotionType; }
    public Double getPromotionValue() { return promotionValue; }
    public void setPromotionValue(Double promotionValue) { this.promotionValue = promotionValue; }
    public LocalDateTime getPromotionStartDate() { return promotionStartDate; }
    public void setPromotionStartDate(LocalDateTime promotionStartDate) { this.promotionStartDate = promotionStartDate; }
    public LocalDateTime getPromotionEndDate() { return promotionEndDate; }
    public void setPromotionEndDate(LocalDateTime promotionEndDate) { this.promotionEndDate = promotionEndDate; }
    public Boolean getPromotionActive() { return promotionActive; }
    public void setPromotionActive(Boolean promotionActive) { this.promotionActive = promotionActive; }
    public Double getDiscountedPrice() { return discountedPrice; }
    public void setDiscountedPrice(Double discountedPrice) { this.discountedPrice = discountedPrice; }

    public List<ProductPriceDTO> getPrices() { return prices; }
    public void setPrices(List<ProductPriceDTO> prices) { this.prices = prices; }
    public ProductStockDTO getStock() {
        if (this.stock == null) {
            this.stock = new ProductStockDTO();
        }
        return this.stock;
    }
    public void setStock(ProductStockDTO stock) {
        this.stock = stock;
    }
    public void setStock(Integer stockVal) {
        if (this.stock == null) {
            this.stock = new ProductStockDTO();
        }
        int upc = (this.wholesaleMinQty != null && this.wholesaleMinQty > 0) ? this.wholesaleMinQty : 12;
        int s = stockVal != null ? stockVal : 0;
        this.stock.setUnitsPerCarton(upc);
        this.stock.setCartons(s / upc);
        this.stock.setLooseUnits(s % upc);
        this.stock.setTotalUnits(s);
    }
    public Object getCategory() { return category; }
    public void setCategory(Object category) { this.category = category; }
    public List<ProductImageDTO> getImages() { return images; }
    public void setImages(List<ProductImageDTO> images) { this.images = images; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }
}
