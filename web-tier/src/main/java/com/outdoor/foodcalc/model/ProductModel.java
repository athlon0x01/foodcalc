package com.outdoor.foodcalc.model;

/**
 * <description>
 *
 * @author Anton Borovyk
 */
public class ProductModel {
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
