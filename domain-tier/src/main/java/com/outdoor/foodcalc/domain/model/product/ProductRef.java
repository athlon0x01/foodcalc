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

    /**
     * Product item constructor
     * @param product product entity
     * @param weight item weight in 0.1 grams
     */
    public ProductRef(Product product, int weight) {
        if (product == null)
            throw new IllegalArgumentException("Null Product is not allowed!");
        this.product = product;
        this.weight = weight;
    }

    public ProductRef(Product product, float weight) {
        this(product, Math.round(weight * 10));
    }

    public ProductRef buildNewRef(int newWeight) {
        return new ProductRef(product, newWeight);
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
        return "[id=" + getProductId() + ", Name='" + getName() + "', weight=" + getInternalWeight() + "]";
    }
}
