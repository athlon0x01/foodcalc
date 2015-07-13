package com.outdoor.foodcalc.domain.model.dish;

/**
 * Dish categories, like soups, snacks, hot drinks, etc.
 *
 * @author Anton Borovyk
 */
public class DishCategory {

    private final int categoryId;
    private String name;

    public DishCategory(int categoryId, String name) {
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
