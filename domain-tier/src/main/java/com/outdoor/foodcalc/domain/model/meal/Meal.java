package com.outdoor.foodcalc.domain.model.meal;

import com.google.common.collect.ImmutableList;
import com.outdoor.foodcalc.domain.model.ComplexFoodEntity;
import com.outdoor.foodcalc.domain.model.IDomainEntity;
import com.outdoor.foodcalc.domain.model.dish.DishRef;
import com.outdoor.foodcalc.domain.model.product.ProductRef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Meal entity. It include dishes & products.
 *
 * @author Anton Borovyk
 */
public class Meal extends ComplexFoodEntity implements IDomainEntity<Meal> {

    private final long mealId;
    private MealType type;
    private List<DishRef> dishes;
    private List<ProductRef> products;

    public Meal(long mealId, MealType type, Collection<DishRef> dishes, Collection<ProductRef> products) {
        this.mealId = mealId;
        this.type = type;
        this.dishes = new ArrayList<>(dishes);
        this.products = new ArrayList<>(products);
    }

    public long getMealId() {
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

    /**
     * Combine all collection of different food entities to complex products collection.
     *
     * @return collection of fields products collection
     */
    @Override
    protected Collection<Collection<ProductRef>> getProductsCollections() {
        //collect all dish products & products to one list
        final List<Collection<ProductRef>> allProductsList = dishes.stream().map(DishRef::getAllProducts).collect(toList());
        allProductsList.add(products);
        return allProductsList;
    }
}
