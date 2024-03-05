package com.outdoor.foodcalc.domain.model.meal;

import com.outdoor.foodcalc.domain.model.ComplexFoodEntity;
import com.outdoor.foodcalc.domain.model.IDomainEntity;
import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;

import java.util.*;

/**
 * Meal entity. It includes dishes & products.
 *
 * @author Anton Borovyk
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Jacksonized
@Builder(toBuilder = true)
public class Meal extends ComplexFoodEntity implements IDomainEntity {

    @EqualsAndHashCode.Include
    private final long mealId;
    private String description;
    private MealType type;
    @Builder.Default
    private List<Dish> dishes = new ArrayList<>();
    @Builder.Default
    private List<ProductRef> products = new ArrayList<>();

    @Override
    public Collection<ProductRef> getAllProducts() {
        List<ProductRef> allProducts = new ArrayList<>(products);
        dishes.forEach(dish -> allProducts.addAll(dish.getAllProducts()));
        return Collections.unmodifiableList(allProducts);
    }

    @Override
    public boolean sameValueAs(IDomainEntity other) {
        if (this.equals(other)) {
            Meal meal = (Meal) other;

            if (!Objects.equals(description, meal.description)) return false;
            if (!Objects.equals(type, meal.type)) return false;
            if (!sameCollectionAs(dishes, meal.dishes)) return false;
            return sameCollectionAs(products, meal.products);
        }
        return false;
    }
}
