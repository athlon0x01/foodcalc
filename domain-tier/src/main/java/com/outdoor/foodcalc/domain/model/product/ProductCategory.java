package com.outdoor.foodcalc.domain.model.product;

/**
 * Product categories, like bakery, meat, etc.
 *
 * @author Anton Borovyk
 */
public class ProductCategory {

    private final int categoryId;
    private String name;

    public ProductCategory(int categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
