package com.outdoor.foodcalc.domain.model.product;

import com.outdoor.foodcalc.domain.model.IDomainEntity;
import lombok.EqualsAndHashCode;

/**
 * Product items - components of the dish (building bricks for dish).
 *
 * @author Anton Borovyk
 */
@EqualsAndHashCode
public class ProductRef implements IDomainEntity {
    private final Product product;
    //product item weight in 0.1 grams
    private final int weight;
    private final Long packageId;

    /**
     * Product item constructor
     * @param product product entity
     * @param weight item weight in 0.1 grams
     */
    public ProductRef(Product product, int weight) {
        this(product, weight, null);
    }

    /**
     * Product item constructor
     * @param product product entity
     * @param weight item weight in 0.1 grams
     * @param packageId food package linked to product or null
     */
    public ProductRef(Product product, int weight, Long packageId) {
        if (product == null)
            throw new IllegalArgumentException("Null Product is not allowed!");
        this.product = product;
        this.weight = weight;
        if (packageId == null || packageId == 0) {
            this.packageId = null;
        }
        else {
            this.packageId = packageId;
        }
    }

    public ProductRef(Product product, float weight) {
        this(product, Math.round(weight * 10));
    }

    public ProductRef(Product product, float weight, Long packageId) {
        this(product, Math.round(weight * 10), packageId);
    }

    public ProductRef buildNewRef(int newWeight) {
        return new ProductRef(product, newWeight, packageId);
    }

    /**
     * @return weight in gram
     */
    public float getWeight() {
        return weight / 10.f;
    }

    public int getInternalWeight() {
        return weight;
    }

    public Long getPackageId() {
        return packageId;
    }

    public long getProductId() {
        return product.getProductId();
    }

    public String getName() {
        return product.getName();
    }

    public Long getProductCategoryId() {
        return product.getCategory().getCategoryId();
    }

    public String getProductCategoryName() {
        return product.getCategory().getName();
    }

    /**
     * @return calorific in kCal per 100 gram
     */
    public float getCalorific() {
        return product.getCalorific() * weight / 1000.f;
    }

    /**
     * @return proteins in gram per 100 gram
     */
    public float getProteins() {
        return product.getProteins() * weight / 1000.f;
    }

    /**
     * @return fats in gram per 100 gram
     */
    public float getFats() {
        return product.getFats() * weight / 1000.f;
    }

    /**
     * @return carbonates in gram per 100 gram
     */
    public float getCarbs() {
        return product.getCarbs() * weight / 1000.f;
    }

    @Override
    public boolean sameValueAs(IDomainEntity other) {
        return this.equals(other);
    }

    @Override
    public String toString() {
        return "[id=" + getProductId() + ", Name='" + getName() + "', weight=" + getInternalWeight() + ", packageId=" + getPackageId() + "]";
    }
}
