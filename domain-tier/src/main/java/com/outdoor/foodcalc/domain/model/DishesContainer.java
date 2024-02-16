package com.outdoor.foodcalc.domain.model;

import com.outdoor.foodcalc.domain.model.dish.Dish;

import java.util.List;

/**
 * Interface for entities that contains dishes inside.
 * It helps to calculate food details.
 *
 * @author Anton Borovyk
 */
//TODO review if this interface is needed
public interface DishesContainer {

    /**
     * Collect all dishes contained in this entity, no nested \ recursive calculations
     * @return entity's dishes
     */
    List<Dish> getDishes();
}
