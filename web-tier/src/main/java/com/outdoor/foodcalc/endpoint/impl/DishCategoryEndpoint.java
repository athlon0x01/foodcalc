package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.endpoint.DishCategoriesApi;
import com.outdoor.foodcalc.model.ValidationException;
import com.outdoor.foodcalc.model.dish.SimpleDishCategory;
import com.outdoor.foodcalc.service.DishCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * REST Endpoint for Dish Category related operations
 *
 * @author Anton Borovyk.
 */
@RestController
public class DishCategoryEndpoint implements DishCategoriesApi {

    private static final Logger LOG = LoggerFactory.getLogger(DishCategoryEndpoint.class);

    private final DishCategoryService categoryService;

    @Autowired
    public DishCategoryEndpoint(DishCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public List<SimpleDishCategory> getDishCategories() {
        LOG.debug("Getting all dish categories");
        return categoryService.getDishCategories();
    }

    public SimpleDishCategory getDishCategory(@PathVariable("id") long id) {
        LOG.debug("Getting dish category id = {}", id);
        return categoryService.getDishCategory(id);
    }

    public SimpleDishCategory addDishCategory(@RequestBody @Valid SimpleDishCategory category) {
        LOG.debug("Adding new dish category - {}", category);
        return categoryService.addDishCategory(category);
    }

    public SimpleDishCategory updateDishCategory(@PathVariable("id") long id,
                                   @RequestBody @Valid SimpleDishCategory category) {
        if (id != category.id) {
            LOG.error("Path variable Id = {} doesn't match with request body Id = {}", id, category.id);
            throw new ValidationException("Path variable Id = " + id
                    + " doesn't match with request body Id = " + category.id);
        }
        LOG.debug("Updating dish category {}", category);
        return categoryService.updateDishCategory(id, category);
    }

    public void deleteMealType(@PathVariable("id") long id) {
        LOG.debug("Deleting dish category id = {}", id);
        categoryService.deleteMealType(id);
    }
}
