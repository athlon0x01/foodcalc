package com.outdoor.foodcalc.domain.model;

import com.outdoor.foodcalc.domain.model.product.ProductRef;

import java.util.Collection;

/**
 * Unified interface for getting food details from any grocery layout object.
 *
 * @author Anton Borovyk
 */
public interface FoodDetails {

    /**
     * Collect all products contained in this entity and nested entities and sums their weights
     * @return aggregated products list(product weights are summed).
     */
    Collection<ProductRef> getAllProducts();

    /**
     * Method for calculation and getting aggregated food details instance.
     * @return aggregated food details instance
     */
    FoodDetailsInstance getFoodDetails();
}
