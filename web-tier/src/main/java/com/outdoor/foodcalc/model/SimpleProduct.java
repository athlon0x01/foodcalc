package com.outdoor.foodcalc.model;

/**
 * Simplified view model for {@link com.outdoor.foodcalc.domain.model.product.Product} class.
 *
 * @author Anton Borovyk
 */
public class SimpleProduct {
    public long id;
    public String name;
    public float calorific;
    public float proteins;
    public float fats;
    public float carbs;
    public float defaultWeight;

    public String getName() {
        return name;
    }

    public float getCalorific() {
        return calorific;
    }

    public float getProteins() {
        return proteins;
    }

    public float getFats() {
        return fats;
    }

    public float getCarbs() {
        return carbs;
    }

    public float getDefaultWeight() {
        return defaultWeight;
    }
}
