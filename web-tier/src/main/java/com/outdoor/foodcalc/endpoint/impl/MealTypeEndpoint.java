package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.domain.exception.FoodcalcException;
import com.outdoor.foodcalc.model.ValidationException;
import com.outdoor.foodcalc.model.meal.MealType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST Endpoint for Meal Type related operations
 *
 * @author Anton Borovyk.
 */
@RestController
@RequestMapping("${spring.data.rest.basePath}/meal-types")
public class MealTypeEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(MealTypeEndpoint.class);

    private final List<MealType> meals = new ArrayList<>();

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<MealType> getMealTypes() {
        LOG.debug("Getting all meal types");
        return meals;
    }

    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    public MealType getMealType(@PathVariable("id") long id) {
        LOG.debug("Getting meal type id = {}", id);
        return meals.get((int) id);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public MealType addMealType(@RequestBody @Valid MealType mealType) {
        LOG.debug("Adding new meal type - {}", mealType);
        mealType.id = meals.size();
        meals.add(mealType);
        return mealType;
    }

    @PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public MealType updateMealType(@PathVariable("id") long id,
                                   @RequestBody @Valid MealType mealType) {
        if (id != mealType.id) {
            LOG.error("Path variable Id = {} doesn't match with request body Id = {}", id, mealType.id);
            throw new ValidationException("Path variable Id = " + id
                    + " doesn't match with request body Id = " + mealType.id);
        }
        LOG.debug("Updating meal type {}", mealType);
        if (meals.size() <= id) {
            throw new FoodcalcException("Meal type not found");
        }
        meals.set((int) id, mealType);
        return mealType;
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMealType(@PathVariable("id") long id) {
        LOG.debug("Deleting meal type id = {}", id);
        if (meals.size() <= id) {
            throw new FoodcalcException("Meal type not found");
        }
        meals.remove((int)id);
        for (int i = 0; i < meals.size(); i++) {
            meals.get(i).id = i;
        }
    }
}
