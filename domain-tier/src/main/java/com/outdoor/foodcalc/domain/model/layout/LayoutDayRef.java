package com.outdoor.foodcalc.domain.model.layout;

import com.google.common.collect.ImmutableList;
import com.outdoor.foodcalc.domain.model.FoodDetails;
import com.outdoor.foodcalc.domain.model.IValueObject;
import com.outdoor.foodcalc.domain.model.meal.MealRef;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import org.joda.time.LocalDate;

/**
 * Day Value Object, provides readonly access to {@Link Day} Entity.
 *
 * @author Anton Borovyk
 */
public class LayoutDayRef implements IValueObject<LayoutDay>, FoodDetails {

    private final LayoutDay day;

    public LayoutDayRef(LayoutDay day) {
        this.day = day;
    }

    public int getDayId() {
        return day.getDayId();
    }

    public LocalDate getDate() {
        return day.getDate();
    }

    public String getDescription() {
        return day.getDescription();
    }

    public ImmutableList<MealRef> getMeals() {
        return ImmutableList.copyOf(day.getMeals());
    }

    public ImmutableList<ProductRef> getProducts() {
        return ImmutableList.copyOf(day.getProducts());
    }

    @Override
    public boolean sameValueAs(LayoutDay other) {
        return day.getDayId() == other.getDayId();
    }

    /**
     * @return calorific in kCal
     */
    @Override
    public float getCalorific() {
        return day.getCalorific();
    }

    /**
     * @return proteins in gram
     */
    @Override
    public float getProteins() {
        return day.getProteins();
    }

    /**
     * @return fats in gram
     */
    @Override
    public float getFats() {
        return day.getFats();
    }

    /**
     * @return carbonates in gram
     */
    @Override
    public float getCarbs() {
        return day.getCarbs();
    }

    /**
     * @return weight in gram
     */
    @Override
    public float getWeight() {
        return day.getWeight();
    }
}
