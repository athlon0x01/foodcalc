package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.domain.exception.FoodcalcException;
import com.outdoor.foodcalc.model.ValidationException;
import com.outdoor.foodcalc.model.dish.SimpleDishCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST Endpoint for Dish Category related operations
 *
 * @author Anton Borovyk.
 */
@RestController
@RequestMapping("${spring.data.rest.basePath}/dish-categories")
public class DishCategoryEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(DishCategoryEndpoint.class);

    private final List<SimpleDishCategory> categories = new ArrayList<>();

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<SimpleDishCategory> getDishCategories() {
        LOG.debug("Getting all dish categories");
        return categories;
    }

    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    public SimpleDishCategory getDishCategory(@PathVariable("id") long id) {
        LOG.debug("Getting dish category id = {}", id);
        return categories.get((int) id);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public SimpleDishCategory addDishCategory(@RequestBody @Valid SimpleDishCategory category) {
        LOG.debug("Adding new dish category - {}", category);
        category.id = categories.size();
        categories.add(category);
        return category;
    }

    @PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public SimpleDishCategory updateMealType(@PathVariable("id") long id,
                                   @RequestBody @Valid SimpleDishCategory category) {
        if (id != category.id) {
            LOG.error("Path variable Id = {} doesn't match with request body Id = {}", id, category.id);
            throw new ValidationException("Path variable Id = " + id
                    + " doesn't match with request body Id = " + category.id);
        }
        LOG.debug("Updating dish category {}", category);
        if (categories.size() <= id) {
            throw new FoodcalcException("Dish category not found");
        }
        categories.set((int) id, category);
        return category;
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMealType(@PathVariable("id") long id) {
        LOG.debug("Deleting dish category id = {}", id);
        if (categories.size() <= id) {
            throw new FoodcalcException("Dish category not found");
        }
        categories.remove((int)id);
        for (int i = 0; i < categories.size(); i++) {
            categories.get(i).id = i;
        }
    }
}
