package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.endpoint.DishCategoriesApi;
import com.outdoor.foodcalc.model.dish.DishCategoryView;
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
public class DishCategoryEndpoint extends AbstractEndpoint implements DishCategoriesApi {

    private static final Logger LOG = LoggerFactory.getLogger(DishCategoryEndpoint.class);

    private final DishCategoryService categoryService;

    @Autowired
    public DishCategoryEndpoint(DishCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public List<DishCategoryView> getDishCategories() {
        LOG.debug("Getting all dish categories");
        return categoryService.getDishCategories();
    }

    public DishCategoryView getDishCategory(@PathVariable("id") long id) {
        LOG.debug("Getting dish category id = {}", id);
        return categoryService.getDishCategory(id);
    }

    public DishCategoryView addDishCategory(@RequestBody @Valid DishCategoryView category) {
        LOG.debug("Adding new dish category - {}", category);
        return categoryService.addDishCategory(category.getName());
    }

    public void updateDishCategory(@PathVariable("id") long id,
                                   @RequestBody @Valid DishCategoryView category) {
        verifyEntityId(id, category);
        LOG.debug("Updating dish category {}", category);
        categoryService.updateDishCategory(category);
    }

    public void deleteDishCategory(@PathVariable("id") long id) {
        LOG.debug("Deleting dish category id = {}", id);
        categoryService.deleteDishCategory(id);
    }
}
