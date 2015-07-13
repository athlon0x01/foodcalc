package com.outdoor.foodcalc.domain.model.dish;

import com.outdoor.foodcalc.domain.model.product.Product;

/**
 * Product items - components of the dish (building bricks for dish).
 *
 * @author Anton Borovyk
 */
public class DishProduct {
    private final Product product;
    //product item weight in gram
    private final float weight;

    public DishProduct(Product product, float weight) {
        this.product = product;
        this.weight = weight;
    }

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

    public float getCalorific() {
        return product.getCalorific();
    }

    public float getProteins() {
        return product.getProteins();
    }

    public float getFats() {
        return product.getFats();
    }

    public float getCarbs() {
        return product.getCarbs();
    }
}
