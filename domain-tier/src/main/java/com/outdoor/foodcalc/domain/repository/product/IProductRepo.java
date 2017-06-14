package com.outdoor.foodcalc.domain.repository.product;

import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.model.product.Product;

import java.util.List;

/**
 * Product repository responsible for {@link Product} persistence.
 *
 * @author Anton Borovyk
 */
public interface IProductRepo {

    List<ProductCategory> getCategories();

    /**
     * Loads all {@link Product}.
     *
     * @return list of products
     */
    List<Product> getAllProducts();
}
