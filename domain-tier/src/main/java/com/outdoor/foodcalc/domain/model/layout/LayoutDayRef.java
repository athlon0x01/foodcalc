package com.outdoor.foodcalc.domain.model.layout;

import com.google.common.collect.ImmutableList;
import com.outdoor.foodcalc.domain.model.IValueObject;
import com.outdoor.foodcalc.domain.model.meal.MealRef;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import org.joda.time.LocalDate;

/**
 * <description>
 *
 * @author Anton Borovyk
 */
//TODO implement FoodDetails
public class LayoutDayRef implements IValueObject<LayoutDay> {

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
}
