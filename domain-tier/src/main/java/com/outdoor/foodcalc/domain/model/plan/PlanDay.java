package com.outdoor.foodcalc.domain.model.plan;

import com.outdoor.foodcalc.domain.model.ComplexFoodEntity;
import com.outdoor.foodcalc.domain.model.IDomainEntity;
import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.meal.Meal;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.util.*;

/**
 * Plan Day entity, that contains some meals and may be some additional dishes and products.
 * In general day doesn't include dishes. Dishes should be included into meals, but some exceptions allowed.
 * F.e. Breakfast & Lunch (meals) & some nuts & sweets (products).
 *
 * @author Anton Borovyk
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Jacksonized
@Builder(toBuilder = true)
public class PlanDay extends ComplexFoodEntity implements IDomainEntity {

    @EqualsAndHashCode.Include
    private final long dayId;
    private LocalDate date;
    private String description;
    @Builder.Default
    private List<Meal> meals = new ArrayList<>();
    @Builder.Default
    private List<Dish> dishes = new ArrayList<>();
    @Builder.Default
    private List<ProductRef> products = new ArrayList<>();

    @Override
    public Collection<ProductRef> getAllProducts() {
        List<ProductRef> allProducts = new ArrayList<>(products);
        dishes.forEach(dish -> allProducts.addAll(dish.getAllProducts()));
        meals.forEach(meal -> allProducts.addAll(meal.getAllProducts()));
        return Collections.unmodifiableList(allProducts);
    }

    @Override
    public boolean sameValueAs(IDomainEntity other) {
        if (this.equals(other)) {
            PlanDay planDay = (PlanDay) other;
            if (!Objects.equals(description, planDay.description)) return false;
            if (!Objects.equals(date, planDay.date)) return false;
            if (!sameCollectionAs(meals, planDay.meals)) return false;
            if (!sameCollectionAs(dishes, planDay.dishes)) return false;
            return sameCollectionAs(products, planDay.products);
        }
        return false;
    }

    @Override
    public String toString() {
        //TODO remove
        return "[id=" + dayId + ", day=" + date + "]";
    }
}
