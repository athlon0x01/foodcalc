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
    //product item weight in gram
    private final float weight;

    public ProductRef(Product product, float weight) {
        this.product = product;
        this.weight = weight;
    }

    /**
     * @return weight in gram
     */
    @Override
    public float getWeight() {
        return weight;
    }

    public int getProductId() {
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
        return product.getCalorific();
    }

    /**
     * @return proteins in gram
     */
    @Override
    public float getProteins() {
        return product.getProteins();
    }

    /**
     * @return fats in gram
     */
    @Override
    public float getFats() {
        return product.getFats();
    }

    /**
     * @return carbonates in gram
     */
    @Override
    public float getCarbs() {
        return product.getCarbs();
    }

    @Override
    public boolean sameValueAs(ProductRef other) {
        return product.getProductId() == other.getProductId() && weight == other.getWeight();
    }
}
