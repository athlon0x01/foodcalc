package com.outdoor.foodcalc.domain.model.product;

import com.outdoor.foodcalc.domain.model.FoodDetails;
import com.outdoor.foodcalc.domain.model.IValueObject;
import lombok.*;

import java.util.Collection;

/**
 * Product items - components of the dish (building bricks for dish).
 *
 * @author Anton Borovyk
 */

@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ProductRef implements IValueObject<ProductRef>, FoodDetails, Comparable<ProductRef> {
    @NonNull
    private final Product product;
    //product item weight in 0.1 grams
    private int weight;

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

    public Long getProductCategoryId() {
        return product.getCategory().getCategoryId();
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
        return product.getProductId() == other.getProductId() && weight == other.weight;
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
        int weight = products.stream().mapToInt(ProductRef::getInternalWeight).sum();
        //return new Value Object
        return new ProductRef(product, weight);
    }

    @Override
    public int compareTo(ProductRef o) {
        //compare productId first
        if (getProductId() == o.getProductId()) {
            return getInternalWeight() - o.getInternalWeight();
        }
        else return (getProductId() < o.getProductId()) ? -1 : 1;
    }
}
