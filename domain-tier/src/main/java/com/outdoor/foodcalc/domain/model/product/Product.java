package com.outdoor.foodcalc.domain.model.product;

import com.outdoor.foodcalc.domain.model.FoodDetails;
import com.outdoor.foodcalc.domain.model.IDomainEntity;

/**
 * Product entity (bread, butter, milk, etc.)
 *
 * @author Anton Borovyk
 */
public class Product implements IDomainEntity<Product>, FoodDetails {

    private final long productId;
    private String name;
    private ProductCategory category;
    //calorific in kCal per 100 gram
    private float calorific;
    //proteins in gram per 100 gram
    private float proteins;
    //fats in gram per 100 gram
    private float fats;
    //carbonates in gram per 100 gram
    private float carbs;
    //default product item weight in gram
    private float defaultWeight;

    public Product(String name, ProductCategory category) {
        this.productId = -1;
        this.name = name;
        this.category = category;
    }

    public Product(long productId, String name, ProductCategory category) {
        this.productId = productId;
        this.name = name;
        this.category = category;
    }

    public Product(long productId, String name, ProductCategory category, float calorific, float proteins, float fats, float carbs, float defaultWeight) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.calorific = calorific;
        this.proteins = proteins;
        this.fats = fats;
        this.carbs = carbs;
        this.defaultWeight = defaultWeight;
    }

    public long getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    /**
     * @return calorific in kCal per 100 gram
     */
    @Override
    public float getCalorific() {
        return calorific;
    }

    public void setCalorific(float calorific) {
        this.calorific = calorific;
    }

    /**
     * @return proteins in gram per 100 gram
     */
    @Override
    public float getProteins() {
        return proteins;
    }

    public void setProteins(float proteins) {
        this.proteins = proteins;
    }

    /**
     * @return fats in gram per 100 gram
     */
    @Override
    public float getFats() {
        return fats;
    }

    public void setFats(float fats) {
        this.fats = fats;
    }

    /**
     * @return carbonates in gram per 100 gram
     */
    @Override
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

    /**
     * @return weight in gram, Product entity doesn't nave real weight.
     */
    @Override
    public float getWeight() {
        return 0;
    }

    @Override
    public boolean sameIdentityAs(Product other) {
        return productId == other.productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;

        Product product = (Product) o;

        if (productId != product.productId) return false;
        if (name != null ? !name.equals(product.name) : product.name != null) return false;
        return !(category != null ? !category.equals(product.category) : product.category != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (productId ^ (productId >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        return result;
    }
}
