package com.outdoor.foodcalc.domain.repository.dish;

import com.outdoor.foodcalc.domain.model.dish.DishCategory;

import java.util.List;
import java.util.Optional;

public interface IDishCategoryRepo {

    /**
     * Loads all {@link DishCategory} objects.
     *
     * @return list of categories
     */
    List<DishCategory> getCategories();

    /**
     * Loads {@link DishCategory} object by Id
     *
     * @param id category Id
     * @return loaded DishCategory
     */
    Optional<DishCategory> getCategory(long id);

    /**
     * Add new {@link DishCategory}.
     *
     * @param category category to add
     * @return auto generated Id
     */
    long addCategory(DishCategory category);

    /**
     * Updates selected {@link DishCategory} with new value.
     *
     * @param category updated category
     * @return if category was updated
     */
    boolean updateCategory(DishCategory category);

    /**
     * Removes selected {@link DishCategory}.
     *
     * @param id category Id to delete
     * @return if category was deleted
     */
    boolean deleteCategory(long id);

    /**
     * Check if specified {@link DishCategory} exists.
     *
     * @param id category Id to check
     * @return if category exists.
     */
    boolean exist(long id);

}
