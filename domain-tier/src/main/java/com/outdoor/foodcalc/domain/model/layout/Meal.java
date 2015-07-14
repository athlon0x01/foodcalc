package com.outdoor.foodcalc.domain.model.layout;

import com.google.common.collect.ImmutableList;
import com.outdoor.foodcalc.domain.model.IDomainEntity;
import com.outdoor.foodcalc.domain.model.dish.DishRef;
import com.outdoor.foodcalc.domain.model.product.ProductRef;

import java.util.ArrayList;
import java.util.List;

/**
 * <description>
 *
 * @author Anton Borovyk
 */
public class Meal implements IDomainEntity<Meal> {

    private final int mealId;
    private MealType type;
    private List<DishRef> dishes;
    private List<ProductRef> products;

    public Meal(int mealId, MealType type, List<DishRef> dishes, List<ProductRef> products) {
        this.mealId = mealId;
        this.type = type;
        this.dishes = new ArrayList<>(dishes);
        this.products = new ArrayList<>(products);
    }

    public int getMealId() {
        return mealId;
    }

    public MealType getType() {
        return type;
    }

    public void setType(MealType type) {
        this.type = type;
    }

    public ImmutableList<DishRef> getDishes() {
        return ImmutableList.copyOf(dishes);
    }

    public void setDishes(List<DishRef> dishes) {
        this.dishes = new ArrayList<>(dishes);
    }

    public ImmutableList<ProductRef> getProducts() {
        return ImmutableList.copyOf(products);
    }

    public void setProducts(List<ProductRef> products) {
        this.products = new ArrayList<>(products);
    }

    @Override
    public boolean sameIdentityAs(Meal other) {
        return mealId == other.mealId;
    }
}
