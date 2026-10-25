package com.aura.cosmetics.dto;

public class ProductStockDTO {
    private Integer cartons = 0;
    private Integer unitsPerCarton = 12;
    private Integer looseUnits = 0;
    private Integer totalUnits = 0;
    private Integer alertThresholdCartons = 2;

    public ProductStockDTO() {}

    public ProductStockDTO(Integer cartons, Integer unitsPerCarton, Integer looseUnits, Integer totalUnits, Integer alertThresholdCartons) {
        this.cartons = cartons != null ? cartons : 0;
        this.unitsPerCarton = (unitsPerCarton != null && unitsPerCarton > 0) ? unitsPerCarton : 12;
        this.looseUnits = looseUnits != null ? looseUnits : 0;
        this.totalUnits = totalUnits != null ? totalUnits : (this.cartons * this.unitsPerCarton + this.looseUnits);
        this.alertThresholdCartons = alertThresholdCartons != null ? alertThresholdCartons : 2;
    }

    public Integer getCartons() { return cartons; }
    public void setCartons(Integer cartons) { this.cartons = cartons; }

    public Integer getUnitsPerCarton() { return unitsPerCarton; }
    public void setUnitsPerCarton(Integer unitsPerCarton) { this.unitsPerCarton = unitsPerCarton; }

    public Integer getLooseUnits() { return looseUnits; }
    public void setLooseUnits(Integer looseUnits) { this.looseUnits = looseUnits; }

    public Integer getTotalUnits() { return totalUnits; }
    public void setTotalUnits(Integer totalUnits) { this.totalUnits = totalUnits; }

    public Integer getAlertThresholdCartons() { return alertThresholdCartons; }
    public void setAlertThresholdCartons(Integer alertThresholdCartons) { this.alertThresholdCartons = alertThresholdCartons; }
}
