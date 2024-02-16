package com.outdoor.foodcalc.domain.model.meal;

import com.outdoor.foodcalc.domain.model.ComplexFoodEntity;
import com.outdoor.foodcalc.domain.model.DishesContainer;
import com.outdoor.foodcalc.domain.model.IDomainEntity;
import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.product.ProductRef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

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

    /**
     * Combine all collection of different food entities to complex products collection.
     *
     * @return collection of fields products collection
     */
    @Override
    protected Collection<Collection<ProductRef>> getProductsCollections() {
        //collect all dish products & products to one list
        final List<Collection<ProductRef>> allProductsList = dishes.stream().map(Dish::getAllProducts).collect(toList());
        allProductsList.add(products);
        return allProductsList;
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
