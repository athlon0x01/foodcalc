package com.outdoor.foodcalc.domain.model.meal;

import com.google.common.collect.ImmutableList;
import com.outdoor.foodcalc.domain.model.IValueObject;
import com.outdoor.foodcalc.domain.model.dish.DishRef;
import com.outdoor.foodcalc.domain.model.product.ProductRef;

/**
 * <description>
 *
 * @author Anton Borovyk
 */
public class MealRef implements IValueObject<MealRef> {

    private final Meal meal;

    public MealRef(Meal meal) {
        this.meal = meal;
    }

    public int getMealId() {
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
}
