package com.outdoor.foodcalc.domain.model.layout;

import com.google.common.collect.ImmutableList;
import com.outdoor.foodcalc.domain.model.FoodDetails;
import com.outdoor.foodcalc.domain.model.IDomainEntity;
import com.outdoor.foodcalc.domain.model.meal.MealRef;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Day entity, that contains some meals and may be some additional products.
 * Day doesn't include dishes. Dishes should be included into meals.
 * F.e. Breakfast & Lunch (meals) & some nuts & sweets (products).
 *
 * @author Anton Borovyk
 */
public class LayoutDay implements IDomainEntity<LayoutDay>, FoodDetails {

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

    /**
     * Internal details summary calculation.
     * @param sp - parameter for calculations, f.e. fats, proteins, etc.
     * @return summarized parameter value
     */
    private float dayDetailsCalculation(Function<FoodDetails, Float> sp) {
        float mealValue = meals.stream().map(sp).reduce(FoodDetails::floatSum).get();
        float productValue = products.stream().map(sp).reduce(FoodDetails::floatSum).get();
        return mealValue + productValue;
    }

    /**
     * @return calorific in kCal
     */
    @Override
    public float getCalorific() {
        return dayDetailsCalculation(FoodDetails::getCalorific);
    }

    /**
     * @return proteins in gram
     */
    @Override
    public float getProteins() {
        return dayDetailsCalculation(FoodDetails::getProteins);
    }

    /**
     * @return fats in gram
     */
    @Override
    public float getFats() {
        return dayDetailsCalculation(FoodDetails::getFats);
    }

    /**
     * @return carbonates in gram
     */
    @Override
    public float getCarbs() {
        return dayDetailsCalculation(FoodDetails::getCarbs);
    }

    /**
     * @return weight in gram
     */
    @Override
    public float getWeight() {
        return dayDetailsCalculation(FoodDetails::getWeight);
    }
}
