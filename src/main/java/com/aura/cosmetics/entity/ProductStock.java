package com.aura.cosmetics.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ProductStock {

    @Column(name = "stock_cartons")
    private Integer cartons = 0;

    @Column(name = "units_per_carton")
    private Integer unitsPerCarton = 12;

    @Column(name = "loose_units")
    private Integer looseUnits = 0;

    /** Mappe directement sur la colonne 'stock' existante dans la table products */
    @Column(name = "stock")
    private Integer totalUnits = 0;

    @Column(name = "alert_threshold_cartons")
    private Integer alertThresholdCartons = 2;

    public ProductStock() {}

    public ProductStock(Integer cartons, Integer unitsPerCarton, Integer looseUnits) {
        this.cartons = cartons != null ? Math.max(0, cartons) : 0;
        this.unitsPerCarton = (unitsPerCarton != null && unitsPerCarton > 0) ? unitsPerCarton : 12;
        this.looseUnits = looseUnits != null ? Math.max(0, looseUnits) : 0;
        recalculateTotal();
    }

    public void recalculateTotal() {
        int c = this.cartons != null ? Math.max(0, this.cartons) : 0;
        int u = (this.unitsPerCarton != null && this.unitsPerCarton > 0) ? this.unitsPerCarton : 12;
        int l = this.looseUnits != null ? Math.max(0, this.looseUnits) : 0;
        this.totalUnits = (c * u) + l;
    }

    public Integer getCartons() {
        if (cartons == null && totalUnits != null) {
            int u = (unitsPerCarton != null && unitsPerCarton > 0) ? unitsPerCarton : 12;
            return totalUnits / u;
        }
        return cartons != null ? cartons : 0;
    }

    public void setCartons(Integer cartons) {
        this.cartons = cartons != null ? Math.max(0, cartons) : 0;
        recalculateTotal();
    }

    public Integer getUnitsPerCarton() {
        return (unitsPerCarton != null && unitsPerCarton > 0) ? unitsPerCarton : 12;
    }

    public void setUnitsPerCarton(Integer unitsPerCarton) {
        this.unitsPerCarton = (unitsPerCarton != null && unitsPerCarton > 0) ? unitsPerCarton : 12;
        recalculateTotal();
    }

    public Integer getLooseUnits() {
        if (looseUnits == null && totalUnits != null) {
            int u = (unitsPerCarton != null && unitsPerCarton > 0) ? unitsPerCarton : 12;
            return totalUnits % u;
        }
        return looseUnits != null ? looseUnits : 0;
    }

    public void setLooseUnits(Integer looseUnits) {
        this.looseUnits = looseUnits != null ? Math.max(0, looseUnits) : 0;
        recalculateTotal();
    }

    public Integer getTotalUnits() {
        if (totalUnits == null) {
            recalculateTotal();
        }
        return totalUnits;
    }

    public void setTotalUnits(Integer totalUnits) {
        this.totalUnits = totalUnits != null ? Math.max(0, totalUnits) : 0;
        // Si cartons est nul, on ajuste
        int u = getUnitsPerCarton();
        this.cartons = this.totalUnits / u;
        this.looseUnits = this.totalUnits % u;
    }

    public Integer getAlertThresholdCartons() {
        return alertThresholdCartons != null ? alertThresholdCartons : 2;
    }

    public void setAlertThresholdCartons(Integer alertThresholdCartons) {
        this.alertThresholdCartons = alertThresholdCartons;
    }
}
