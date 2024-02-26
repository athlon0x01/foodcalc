package com.outdoor.foodcalc.domain.model.dish;

import com.outdoor.foodcalc.domain.model.ComplexFoodEntity;
import com.outdoor.foodcalc.domain.model.FoodDetailsInstance;
import com.outdoor.foodcalc.domain.model.IDomainEntity;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;

import java.util.*;

/**
 * Dish entity, like cream soup, lemon tea, etc.
 *
 * @author Anton Borovyk
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Jacksonized
@Builder(toBuilder = true)
public class Dish extends ComplexFoodEntity implements IDomainEntity {

    @EqualsAndHashCode.Include
    private final long dishId;
    private String name;
    private String description;
    private DishCategory category;

    private final Boolean template;
    //dish components
    @Builder.Default
    private List<ProductRef> products = new ArrayList<>();

    /**
     * Collect all products contained in this entity and nested entities and sums their weights
     *
     * @return aggregated products list(product weights are summed).
     */
    @Override
    public Collection<ProductRef> getAllProducts() {
        return Collections.unmodifiableList(products);
    }

    @Override
    public FoodDetailsInstance getFoodDetails() {
        return new FoodDetailsInstance(products);
    }

    @Override
    public boolean sameValueAs(IDomainEntity other) {
        if (this.equals(other)) {
            Dish dish = (Dish) other;
            if (!Objects.equals(name, dish.name)) return false;
            if (!Objects.equals(description, dish.description)) return false;
            if (!Objects.equals(category, dish.category)) return false;
            if (!Objects.equals(template, dish.template)) return false;
            return sameCollectionAs(products, dish.products);
        }
        return false;
    }
}
