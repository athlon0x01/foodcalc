package com.outdoor.foodcalc.domain.model.dish;

import com.google.common.collect.ImmutableList;
import com.outdoor.foodcalc.domain.model.FoodDetails;
import com.outdoor.foodcalc.domain.model.IValueObject;
import com.outdoor.foodcalc.domain.model.product.ProductRef;

/**
 * Dish Value Object, provides readonly access to {@Link Dish} Entity.
 *
 * @author Anton Borovyk
 */
public class DishRef implements IValueObject<DishRef>, FoodDetails {

    //internal Dish entity
    private final Dish dish;

    public DishRef(Dish dish) {
        this.dish = dish;
    }

    public int getDishId() {
        return dish.getDishId();
    }

    public String getName() {
        return dish.getName();
    }

    public String getDescription() {
        return dish.getDescription();
    }

    public String getCategoryName() {
        return dish.getCategory().getName();
    }

    public ImmutableList<ProductRef> getProducts() {
        return ImmutableList.copyOf(dish.getProducts());
    }

    @Override
    public boolean sameValueAs(DishRef other) {
        return dish.getDishId() == other.getDishId() && dish.getProducts().equals(other.getProducts());
    }

    /**
     * @return calorific in kCal
     */
    @Override
    public float getCalorific() {
        return dish.getCalorific();
    }

    /**
     * @return proteins in gram
     */
    @Override
    public float getProteins() {
        return dish.getProteins();
    }

    /**
     * @return fats in gram
     */
    @Override
    public float getFats() {
        return dish.getFats();
    }

    /**
     * @return carbonates in gram
     */
    @Override
    public float getCarbs() {
        return dish.getCarbs();
    }

    /**
     * @return weight in gram
     */
    @Override
    public float getWeight() {
        return dish.getWeight();
    }
}
