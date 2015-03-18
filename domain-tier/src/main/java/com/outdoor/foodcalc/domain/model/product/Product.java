package com.outdoor.foodcalc.domain.model.product;

/**
 * <description>
 *
 * @author Anton Borovyk
 */
public class Product {

    private final int productId;
    private String name;
    private Category category;
    private float calorific;
    private float proteins;
    private float fats;
    private float carbs;
    private float defaultWeight;

    public Product(int productId, String name, Category category) {
        this.productId = productId;
        this.name = name;
        this.category = category;
    }

    public Product(int productId, String name, Category category, float calorific, float proteins, float fats, float carbs, float defaultWeight) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.calorific = calorific;
        this.proteins = proteins;
        this.fats = fats;
        this.carbs = carbs;
        this.defaultWeight = defaultWeight;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public float getCalorific() {
        return calorific;
    }

    public void setCalorific(float calorific) {
        this.calorific = calorific;
    }

    public float getProteins() {
        return proteins;
    }

    public void setProteins(float proteins) {
        this.proteins = proteins;
    }

    public float getFats() {
        return fats;
    }

    public void setFats(float fats) {
        this.fats = fats;
    }

    public float getCarbs() {
        return carbs;
    }

    public void setCarbs(float carbs) {
        this.carbs = carbs;
    }

    public float getDefaultWeight() {
        return defaultWeight;
    }

    public void setDefaultWeight(float defaultWeight) {
        this.defaultWeight = defaultWeight;
    }
}
