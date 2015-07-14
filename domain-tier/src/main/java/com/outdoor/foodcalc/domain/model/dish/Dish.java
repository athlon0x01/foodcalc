package com.outdoor.foodcalc.domain.model.dish;

import com.google.common.collect.ImmutableList;
import com.outdoor.foodcalc.domain.model.IDomainEntity;
import com.outdoor.foodcalc.domain.model.product.ProductRef;

import java.util.ArrayList;
import java.util.List;

/**
 * Dish entity, like cream soup, lemon tea, etc.
 *
 * @author Anton Borovyk
 */
//TODO implement FoodDetails
public class Dish implements IDomainEntity<Dish> {

    private final int dishId;
    private String name;
    private String description;
    private DishCategory category;
    private List<ProductRef> products;

    public Dish(String name, DishCategory category) {
        this.dishId = -1;
        this.name = name;
        this.category = category;
        this.products = new ArrayList<>();
    }

    public Dish(int dishId, String name, DishCategory category) {
        this.dishId = dishId;
        this.name = name;
        this.category = category;
        this.products = new ArrayList<>();
    }

    public Dish(int dishId, String name, String description, DishCategory category, List<ProductRef> products) {
        this.dishId = dishId;
        this.name = name;
        this.description = description;
        this.category = category;
        this.products = new ArrayList<>(products);
    }

    public int getDishId() {
        return dishId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DishCategory getCategory() {
        return category;
    }

    public void setCategory(DishCategory category) {
        this.category = category;
    }

    public ImmutableList<ProductRef> getProducts() {
        return ImmutableList.copyOf(products);
    }

    public void setProducts(List<ProductRef> products) {
        this.products = new ArrayList<>(products);
    }

    @Override
    public boolean sameIdentityAs(Dish other) {
        return dishId == other.dishId;
    }
}
