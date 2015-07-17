package com.outdoor.foodcalc.domain.model.meal;

import com.google.common.collect.ImmutableList;
import com.outdoor.foodcalc.domain.model.FoodDetails;
import com.outdoor.foodcalc.domain.model.IDomainEntity;
import com.outdoor.foodcalc.domain.model.dish.DishRef;
import com.outdoor.foodcalc.domain.model.product.ProductRef;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Meal entity. It include dishes & products.
 *
 * @author Anton Borovyk
 */
public class Meal implements IDomainEntity<Meal>, FoodDetails {

    private final long mealId;
    private MealType type;
    private List<DishRef> dishes;
    private List<ProductRef> products;

    public Meal(long mealId, MealType type, List<DishRef> dishes, List<ProductRef> products) {
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
     * Internal details summary calculation.
     * @param sp - parameter for calculations, f.e. fats, proteins, etc.
     * @return summarized parameter value
     */
    private float mealDetailsCalculation(Function<FoodDetails, Float> sp) {
        float productValue = products.stream().map(sp).reduce(FoodDetails::floatSum).get();
        float dishesValue = dishes.stream().map(sp).reduce(FoodDetails::floatSum).get();
        return productValue + dishesValue;
    }

    /**
     * @return calorific in kCal
     */
    @Override
    public float getCalorific() {
        return mealDetailsCalculation(FoodDetails::getCalorific);
    }

    /**
     * @return proteins in gram
     */
    @Override
    public float getProteins() {
        return mealDetailsCalculation(FoodDetails::getProteins);
    }

    /**
     * @return fats in gram
     */
    @Override
    public float getFats() {
        return mealDetailsCalculation(FoodDetails::getFats);
    }

    /**
     * @return carbonates in gram
     */
    @Override
    public float getCarbs() {
        return mealDetailsCalculation(FoodDetails::getCarbs);
    }

    /**
     * @return weight in gram
     */
    @Override
    public float getWeight() {
        return mealDetailsCalculation(FoodDetails::getWeight);
    }
}
