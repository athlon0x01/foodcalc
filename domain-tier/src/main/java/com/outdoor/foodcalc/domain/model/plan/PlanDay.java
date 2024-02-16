package com.outdoor.foodcalc.domain.model.plan;

import com.outdoor.foodcalc.domain.model.ComplexFoodEntity;
import com.outdoor.foodcalc.domain.model.DishesContainer;
import com.outdoor.foodcalc.domain.model.IDomainEntity;
import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.meal.Meal;
import com.outdoor.foodcalc.domain.model.product.ProductRef;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

/**
 * Plan Day entity, that contains some meals and may be some additional dishes and products.
 * In general day doesn't include dishes. Dishes should be included into meals, but some exceptions allowed.
 * F.e. Breakfast & Lunch (meals) & some nuts & sweets (products).
 *
 * @author Anton Borovyk
 */
public class PlanDay extends ComplexFoodEntity implements IDomainEntity, DishesContainer {

    private final long dayId;
    private LocalDate date;
    private String description;
    private List<Meal> meals;
    private List<Dish> dishes;
    private List<ProductRef> products;

    public PlanDay(long dayId, LocalDate date, List<Meal> meals, List<Dish> dishes, List<ProductRef> products) {
        this.dayId = dayId;
        this.date = date;
        this.meals = new ArrayList<>(meals);
        this.dishes = new ArrayList<>(dishes);
        this.products = new ArrayList<>(products);
    }

    public long getDayId() {
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

    public List<Meal> getMeals() {
        return meals;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = new ArrayList<>(meals);
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = new ArrayList<>(dishes);
    }

    public List<ProductRef> getProducts() {
        return products;
    }

    public void setProducts(List<ProductRef> products) {
        this.products = new ArrayList<>(products);
    }

    @Override
    public boolean sameValueAs(IDomainEntity other) {
        if (this.equals(other)) {
            PlanDay planDay = (PlanDay) other;
            if (dayId != planDay.dayId) return false;
            if (!Objects.equals(date, planDay.date)) return false;
            if (!sameCollectionAs(meals, planDay.meals)) return false;
            if (!sameCollectionAs(dishes, planDay.dishes)) return false;
            return sameCollectionAs(products, planDay.products);
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlanDay planDay = (PlanDay) o;
        return dayId == planDay.dayId;
    }

    @Override
    public int hashCode() {
        int result = (int) (dayId ^ (dayId >>> 32));
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    /**
     * Combine all collection of different food entities to complex products collection.
     *
     * @return collection of fields products collection
     */
    @Override
    protected Collection<Collection<ProductRef>> getProductsCollections() {
        //collect all meals products & products to one list
        final List<Collection<ProductRef>> allProductsList = meals.stream()
                .map(Meal::getAllProducts)
                .collect(toList());
        allProductsList.addAll(dishes.stream()
                .map(Dish::getAllProducts)
                .collect(toList()));
        allProductsList.add(products);
        return allProductsList;
    }
}
