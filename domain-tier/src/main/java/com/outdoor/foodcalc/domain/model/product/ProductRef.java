package com.outdoor.foodcalc.domain.model.product;

import com.outdoor.foodcalc.domain.model.FoodDetails;
import com.outdoor.foodcalc.domain.model.IValueObject;

/**
 * Product items - components of the dish (building bricks for dish).
 *
 * @author Anton Borovyk
 */
public class ProductRef implements IValueObject<ProductRef>, FoodDetails {
    private final Product product;
    //product item weight in 0.1 grams
    private final int weight;

    public ProductRef(Product product, int weight) {
        this.product = product;
        this.weight = weight;
    }

    /**
     * @return weight in gram
     */
    @Override
    public float getWeight() {
        return weight / 10;
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

    public String getProductCategoryName() {
        return product.getCategory().getName();
    }

    /**
     * @return calorific in kCal
     */
    @Override
    public float getCalorific() {
        return product.getCalorific() * weight / 1000;
    }

    /**
     * @return proteins in gram
     */
    @Override
    public float getProteins() {
        return product.getProteins() * weight / 1000;
    }

    /**
     * @return fats in gram
     */
    @Override
    public float getFats() {
        return product.getFats() * weight / 1000;
    }

    /**
     * @return carbonates in gram
     */
    @Override
    public float getCarbs() {
        return product.getCarbs() * weight / 1000;
    }

    @Override
    public boolean sameValueAs(ProductRef other) {
        return product.getProductId() == other.getProductId() && weight == other.getWeight();
    }
}
