package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.domain.exception.FoodcalcException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.endpoint.MealTypesApi;
import com.outdoor.foodcalc.model.ValidationException;
import com.outdoor.foodcalc.model.meal.MealType;
import com.outdoor.foodcalc.service.MealTypesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST Endpoint for Meal Type related operations
 *
 * @author Anton Borovyk.
 */
@RestController
public class MealTypesEndpoint implements MealTypesApi {

    private static final Logger LOG = LoggerFactory.getLogger(MealTypesEndpoint.class);

    private final List<MealType> meals = new ArrayList<>();

    private final MealTypesService mealTypesService;

    @Autowired
    public MealTypesEndpoint(MealTypesService mealTypesService) {
        this.mealTypesService = mealTypesService;
    }

    public List<MealType> getMealTypes() {
        LOG.debug("Getting all meal types");
        return mealTypesService.getMealTypes();
    }

    public MealType getMealType(@PathVariable("id") long id) {
        LOG.debug("Getting meal type id = {}", id);
        return mealTypesService.getMealType(id);
    }

    public MealType addMealType(@RequestBody @Valid MealType mealType) {
        LOG.debug("Adding new meal type - {}", mealType);
        return mealTypesService.addMealType(mealType);
    }

    public MealType updateMealType(@PathVariable("id") long id,
                                   @RequestBody @Valid MealType mealType) {
        if (id != mealType.id) {
            LOG.error("Path variable Id = {} doesn't match with request body Id = {}", id, mealType.id);
            throw new ValidationException("Path variable Id = " + id
                    + " doesn't match with request body Id = " + mealType.id);
        }
        LOG.debug("Updating meal type {}", mealType);
        if (!mealTypesService.updateMealType(mealType)) {
            throw new FoodcalcException("Meal type failed to update");
        }
        return mealType;
    }

    public void deleteMealType(@PathVariable("id") long id) {
        LOG.debug("Deleting meal type id = {}", id);
        if (!mealTypesService.deleteMealType(id)) {
            throw new FoodcalcException("Meal type failed to delete");
        }
    }
}
