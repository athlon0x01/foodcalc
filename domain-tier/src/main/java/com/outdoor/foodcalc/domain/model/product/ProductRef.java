package com.outdoor.foodcalc.domain.model.product;

import com.outdoor.foodcalc.domain.model.FoodDetails;
import com.outdoor.foodcalc.domain.model.IValueObject;

import java.util.Collection;

import static java.util.stream.Collectors.summingInt;

/**
 * Product items - components of the dish (building bricks for dish).
 *
 * @author Anton Borovyk
 */
public class ProductRef implements IValueObject<ProductRef>, FoodDetails, Comparable<ProductRef> {
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

    /**
     * @return weight in gram
     */
    @Override
    public float getWeight() {
        return weight / 10.f;
    }

    private int getInternalWeight() {
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

    public String getProductCategoryName() {
        return product.getCategory().getName();
    }

    /**
     * @return calorific in kCal
     */
    @Override
    public float getCalorific() {
        return product.getCalorific() * weight / 1000.f;
    }

    /**
     * @return proteins in gram
     */
    @Override
    public float getProteins() {
        return product.getProteins() * weight / 1000.f;
    }

    /**
     * @return fats in gram
     */
    @Override
    public float getFats() {
        return product.getFats() * weight / 1000.f;
    }

    /**
     * @return carbonates in gram
     */
    @Override
    public float getCarbs() {
        return product.getCarbs() * weight / 1000.f;
    }

    @Override
    public boolean sameValueAs(ProductRef other) {
        return product.getProductId() == other.getProductId() && weight == other.getWeight();
    }

    /**
     * Summarize weight of product list.
     * @param products not empty product list, that contains same products
     * @return product with summarized weight
     */
    public static ProductRef summarizeWeight(Collection<ProductRef> products) {
        //get Product entity
        Product product = products.iterator().next().product;
        //summarize product weight
        int weight = products.stream().collect(summingInt(ProductRef::getInternalWeight));
        //return new Value Object
        return new ProductRef(product, weight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductRef)) return false;

        ProductRef that = (ProductRef) o;

        if (weight != that.weight) return false;
        if (getProductId() != that.getProductId()) return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        return !(getProductCategoryName() != null ? !getProductCategoryName().equals(that.getProductCategoryName())
                : that.getProductCategoryName() != null);

    }

    @Override
    public int hashCode() {
        int result = product.hashCode();
        result = 31 * result + weight;
        return result;
    }

    @Override
    public int compareTo(ProductRef o) {
        //compare productId first
        if (getProductId() == o.getProductId()) {
            return getInternalWeight() - o.getInternalWeight();
        }
        else return (getProductId() < o.getProductId()) ? -1 : 1;
    }

    @Override
    public String toString() {
        return "[id=" + getProductId() + ", Name='" + getName() + "', weight=" + getInternalWeight() + "]";
    }
}
