package com.outdoor.foodcalc.domain.model.product;

import com.outdoor.foodcalc.domain.model.IDomainEntity;

/**
 * Product items - components of the dish (building bricks for dish).
 *
 * @author Anton Borovyk
 */
public class ProductRef implements IDomainEntity {
    private final Product product;
    //product item weight in 0.1 grams
    private int weight;

    /**
     * Product item constructor
     * @param product product entity
     * @param weight item weight in 0.1 grams
     */
    public ProductRef(Product product, int weight) {
        if (product == null)
            throw new IllegalArgumentException("Constructor doesn't allow null parameters!");
        this.product = product;
        this.weight = weight;
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

    public void setWeight(float weight) {
        this.weight = Math.round(weight * 10);
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
     * @return calorific in kCal
     */
    public float getCalorific() {
        return product.getCalorific() * weight / 1000.f;
    }

    /**
     * @return proteins in gram
     */
    public float getProteins() {
        return product.getProteins() * weight / 1000.f;
    }

    /**
     * @return fats in gram
     */
    public float getFats() {
        return product.getFats() * weight / 1000.f;
    }

    /**
     * @return carbonates in gram
     */
    public float getCarbs() {
        return product.getCarbs() * weight / 1000.f;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductRef that = (ProductRef) o;

        if (weight != that.weight) return false;
        if (getProductId() != that.getProductId()) return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        return !(getProductCategoryName() != null ? !getProductCategoryName().equals(that.getProductCategoryName())
                : that.getProductCategoryName() != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (getProductId() ^ (getProductId() >>> 32));
        result = 31 * result + weight;
        return result;
    }

    @Override
    public boolean sameValueAs(IDomainEntity other) {
        if (other instanceof ProductRef) {
            ProductRef that = (ProductRef) other;
            return getProductId() == that.getProductId() && weight == that.weight;
        }
        return false;
    }

    @Override
    public String toString() {
        return "[id=" + getProductId() + ", Name='" + getName() + "', weight=" + getInternalWeight() + "]";
    }
}
