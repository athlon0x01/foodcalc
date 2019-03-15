package com.outdoor.foodcalc.domain.repository.product;

import com.outdoor.foodcalc.domain.model.product.ProductCategory;

import java.util.List;
import java.util.Optional;

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
     * Loads {@link ProductCategory} object by Id
     *
     * @param id category Id
     * @return loaded ProductCategory
     */
    Optional<ProductCategory> getCategory(long id);

    /**
     * Add new {@link ProductCategory}.
     *
     * @param category category to add
     * @return auto generated Id
     */
    long addCategory(ProductCategory category);


    /**
     * Updates selected {@link ProductCategory} with new value.
     *
     * @param category updated category
     * @return if category was updated
     */
    boolean updateCategory(ProductCategory category);

    /**
     * Removes selected {@link ProductCategory}.
     *
     * @param id category Id to delete
     * @return if category was deleted
     */
    boolean deleteCategory(long id);

    /**
     * Check if specified {@link ProductCategory} exists.
     *
     * @param id category Id to delete
     * @return is product category exists.
     */
    boolean exist(long id);
}
