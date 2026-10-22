package com.aura.cosmetics.dto;

public class ProductImageDTO {
    private Long id;
    private String originalName;
    private String storedFileName;
    private Integer displayOrder;

    public ProductImageDTO() {}

    public ProductImageDTO(Long id, String originalName, String storedFileName, Integer displayOrder) {
        this.id = id;
        this.originalName = originalName;
        this.storedFileName = storedFileName;
        this.displayOrder = displayOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public void setStoredFileName(String storedFileName) {
        this.storedFileName = storedFileName;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
