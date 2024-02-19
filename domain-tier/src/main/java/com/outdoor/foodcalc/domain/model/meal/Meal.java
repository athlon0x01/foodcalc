package com.outdoor.foodcalc.domain.model.meal;

import com.outdoor.foodcalc.domain.model.ComplexFoodEntity;
import com.outdoor.foodcalc.domain.model.DishesContainer;
import com.outdoor.foodcalc.domain.model.IDomainEntity;
import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.product.ProductRef;

import java.util.*;

/**
 * Meal entity. It includes dishes & products.
 *
 * @author Anton Borovyk
 */
public class Meal extends ComplexFoodEntity implements IDomainEntity, DishesContainer {

    private final long mealId;
    private String description;
    private MealType type;
    private List<Dish> dishes;
    private List<ProductRef> products;

    public Meal(long mealId, MealType type, Collection<Dish> dishes, Collection<ProductRef> products) {
        this.mealId = mealId;
        this.type = type;
        this.dishes = new ArrayList<>(dishes);
        this.products = new ArrayList<>(products);
    }

    public long getMealId() {
        return mealId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MealType getType() {
        return type;
    }

    public void setType(MealType type) {
        this.type = type;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = new ArrayList<>(dishes);
    }

    public List<ProductRef> getProducts() {
        return products;
    }

    public void setProducts(List<ProductRef> products) {
        this.products = new ArrayList<>(products);
    }

    @Override
    public Collection<ProductRef> getAllProducts() {
        List<ProductRef> allProducts = new ArrayList<>(products);
        dishes.forEach(dish -> allProducts.addAll(dish.getAllProducts()));
        return Collections.unmodifiableList(allProducts);
    }

    @Override
    public boolean sameValueAs(IDomainEntity other) {
        if (this.equals(other)) {
            Meal meal = (Meal) other;

            if (mealId != meal.mealId) return false;
            if (!Objects.equals(type, meal.type)) return false;
            if (!sameCollectionAs(dishes, meal.dishes)) return false;
            return sameCollectionAs(products, meal.products);
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Meal meal = (Meal) o;
        return mealId == meal.mealId;
    }

    @Override
    public int hashCode() {
        int result = (int) (mealId ^ (mealId >>> 32));
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
