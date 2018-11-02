package com.outdoor.foodcalc.domain.repository.product;

import com.outdoor.foodcalc.domain.model.product.Product;

import java.util.List;

/**
 * Product repository responsible for {@link Product} persistence.
 *
 * @author Anton Borovyk
 */
public interface IProductRepo {

    /**
     * Loads all {@link Product}.
     *
     * @return list of products
     */
    List<Product> getAllProducts();

    /**
     * Count products number in the product category
     * @param categoryId product category Id
     * @return products number
     */
    long countProductsInCategory(long categoryId);
}
