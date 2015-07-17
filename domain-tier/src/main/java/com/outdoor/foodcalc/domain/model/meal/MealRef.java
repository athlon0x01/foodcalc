package com.outdoor.foodcalc.domain.model.meal;

import com.google.common.collect.ImmutableList;
import com.outdoor.foodcalc.domain.model.FoodDetails;
import com.outdoor.foodcalc.domain.model.IValueObject;
import com.outdoor.foodcalc.domain.model.dish.DishRef;
import com.outdoor.foodcalc.domain.model.product.ProductRef;

/**
 * Meal Value Object, provides readonly access to {@link Meal} entity.
 *
 * @author Anton Borovyk
 */
public class MealRef implements IValueObject<MealRef>, FoodDetails {

    private final Meal meal;

    public MealRef(Meal meal) {
        this.meal = meal;
    }

    public long getMealId() {
        return meal.getMealId();
    }

    public String getTypeName() {
        return meal.getType().getName();
    }

    public ImmutableList<DishRef> getDishes() {
        return ImmutableList.copyOf(meal.getDishes());
    }

    public ImmutableList<ProductRef> getProducts() {
        return ImmutableList.copyOf(meal.getProducts());
    }

    @Override
    public boolean sameValueAs(MealRef other) {
        return meal.getMealId() == other.getMealId();
    }

    /**
     * @return calorific in kCal
     */
    @Override
    public float getCalorific() {
        return meal.getCalorific();
    }

    /**
     * @return proteins in gram
     */
    @Override
    public float getProteins() {
        return meal.getProteins();
    }

    /**
     * @return fats in gram
     */
    @Override
    public float getFats() {
        return meal.getFats();
    }

    /**
     * @return carbonates in gram
     */
    @Override
    public float getCarbs() {
        return meal.getCarbs();
    }

    /**
     * @return weight in gram
     */
    @Override
    public float getWeight() {
        return meal.getWeight();
    }
}
