package com.outdoor.foodcalc.domain.model.layout;

import com.google.common.collect.ImmutableList;
import com.outdoor.foodcalc.domain.model.IDomainEntity;
import com.outdoor.foodcalc.domain.model.meal.MealRef;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

/**
 * <description>
 *
 * @author Anton Borovyk
 */
//TODO implement FoodDetails
public class LayoutDay implements IDomainEntity<LayoutDay> {

    private final int dayId;
    private LocalDate date;
    private String description;
    private List<MealRef> meals;
    private List<ProductRef> products;

    public LayoutDay(int dayId, LocalDate date, List<MealRef> meals, List<ProductRef> products) {
        this.dayId = dayId;
        this.date = date;
        this.meals = new ArrayList<>(meals);
        this.products = new ArrayList<>(products);
    }

    public int getDayId() {
        return dayId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ImmutableList<MealRef> getMeals() {
        return ImmutableList.copyOf(meals);
    }

    public void setMeals(List<MealRef> meals) {
        this.meals = new ArrayList<>(meals);
    }

    public ImmutableList<ProductRef> getProducts() {
        return ImmutableList.copyOf(products);
    }

    public void setProducts(List<ProductRef> products) {
        this.products = new ArrayList<>(products);
    }

    @Override
    public boolean sameIdentityAs(LayoutDay other) {
        return dayId == other.dayId;
    }
}
