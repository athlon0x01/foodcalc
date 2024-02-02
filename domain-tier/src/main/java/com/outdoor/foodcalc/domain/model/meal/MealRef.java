package com.outdoor.foodcalc.domain.model.meal;

import com.outdoor.foodcalc.domain.model.FoodDetails;
import com.outdoor.foodcalc.domain.model.IValueObject;
import com.outdoor.foodcalc.domain.model.ProductsContainer;
import com.outdoor.foodcalc.domain.model.dish.DishRef;
import com.outdoor.foodcalc.domain.model.product.ProductRef;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Meal Value Object, provides readonly access to {@link com.outdoor.foodcalc.domain.model.meal.Meal} entity.
 *
 * @author Anton Borovyk
 */
public class MealRef implements IValueObject<MealRef>, FoodDetails, ProductsContainer {

    private final Meal meal;

    public MealRef(Meal meal) {
        if (meal == null)
            throw new IllegalArgumentException("Constructor doesn't allow null parameters!");
        this.meal = meal;
    }

    public long getMealId() {
        return meal.getMealId();
    }

    public int getTypeId() {
        return meal.getType().getTypeId();
    }

    public String getTypeName() {
        return meal.getType().getName();
    }

    public String getDescription() {
        return meal.getDescription();
    }

    public List<DishRef> getDishes() {
        return Collections.unmodifiableList(meal.getDishes());
    }

    public List<ProductRef> getProducts() {
        return Collections.unmodifiableList(meal.getProducts());
    }

    @Override
    public boolean sameValueAs(MealRef other) {
        return meal.equals(other.meal);
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

    /**
     * Collect all products contained in this entity and nested entities and sums their weights
     *
     * @return aggregated products list(product weights are summed).
     */
    @Override
    public Collection<ProductRef> getAllProducts() {
        return meal.getAllProducts();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MealRef mealRef = (MealRef) o;

        return meal.equals(mealRef.meal);

    }

    @Override
    public int hashCode() {
        return meal.hashCode();
    }
}
