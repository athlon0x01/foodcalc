package com.outdoor.foodcalc.domain.repository.product;

import com.outdoor.foodcalc.domain.model.product.ProductCategory;

import java.util.List;

/**
 * Product Category repository responsible for {@link ProductCategory} persistence.
 *
 * @author Anton Borovyk.
 */
public interface IProductCategoryRepo {

    /**
     * Loads all {@link ProductCategory} objects.
     *
     * @return list of categories
     */
    List<ProductCategory> getCategories();

    /**
     * Add new {@link ProductCategory}.
     *
     * @param category category to add
     */
    boolean addCategory(ProductCategory category);


    /**
     * Updates selected {@link ProductCategory} with new value.
     *
     * @param category updated category
     */
    boolean updateCategory(ProductCategory category);

    /**
     * Removes selected {@link ProductCategory}.
     *
     * @param category category to delete
     */
    boolean deleteCategory(ProductCategory category);
}
