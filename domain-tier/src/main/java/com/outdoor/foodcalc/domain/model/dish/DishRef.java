package com.outdoor.foodcalc.domain.model.dish;

import com.google.common.collect.ImmutableList;
import com.outdoor.foodcalc.domain.model.IValueObject;
import com.outdoor.foodcalc.domain.model.product.ProductRef;

/**
 * <description>
 *
 * @author Anton Borovyk
 */
public class DishRef implements IValueObject<DishRef> {

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
}
