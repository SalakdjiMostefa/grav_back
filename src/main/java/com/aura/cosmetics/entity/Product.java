package com.aura.cosmetics.entity;

import com.aura.cosmetics.enums.ProductStatus;
import jakarta.persistence.*;
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

    @Column(nullable = false)
    private Double price;

    private Double wholesalePrice;

    private Integer wholesaleMinQty = 12;

    @Column(nullable = false)
    private Integer stock;

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
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
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
