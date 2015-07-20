package com.outdoor.foodcalc.domain.model;

import com.outdoor.foodcalc.domain.model.product.ProductRef;

import java.util.Collection;

/**
 * Interface for entities that contains of products with specified weights.
 * It helps to calculate food details.
 *
 * @author Anton Borovyk
 */
public interface ProductsContainer {

    /**
     * Collect all products contained in this entity and nested entities and sums their weights
     * @return aggregated products list(product weights are summed).
     */
    Collection<ProductRef> getAllProducts();
}
