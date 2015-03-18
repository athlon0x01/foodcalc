package com.outdoor.foodcalc.domain.model.product;

/**
 * <description>
 *
 * @author Anton Borovyk
 */
public class Category {

    private final int categoryId;
    private String name;

    public Category(int categoryId, String name) {
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
