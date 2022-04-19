package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.domain.exception.FoodcalcException;
import com.outdoor.foodcalc.endpoint.MealTypesApi;
import com.outdoor.foodcalc.model.ValidationException;
import com.outdoor.foodcalc.model.meal.MealTypeView;
import com.outdoor.foodcalc.service.MealTypesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST Endpoint for Meal Type related operations
 *
 * @author Anton Borovyk.
 */
@RestController
public class MealTypesEndpoint implements MealTypesApi {

    private static final Logger LOG = LoggerFactory.getLogger(MealTypesEndpoint.class);

    private final MealTypesService mealTypesService;

    @Autowired
    public MealTypesEndpoint(MealTypesService mealTypesService) {
        this.mealTypesService = mealTypesService;
    }

    public List<MealTypeView> getMealTypes() {
        LOG.debug("Getting all meal types");
        return mealTypesService.getMealTypes();
    }

    public MealTypeView getMealType(@PathVariable("id") int id) {
        LOG.debug("Getting meal type id = {}", id);
        return mealTypesService.getMealType(id);
    }

    public MealTypeView addMealType(@RequestBody @Valid MealTypeView mealTypeView) {
        LOG.debug("Adding new meal type - {}", mealTypeView);
        return mealTypesService.addMealType(mealTypeView.name);
    }

    public MealTypeView updateMealType(@PathVariable("id") int id,
                                       @RequestBody @Valid MealTypeView mealTypeView) {
        if (id != mealTypeView.id) {
            LOG.error("Path variable Id = {} doesn't match with request body Id = {}", id, mealTypeView.id);
            throw new ValidationException("Path variable Id = " + id
                    + " doesn't match with request body Id = " + mealTypeView.id);
        }
        LOG.debug("Updating meal type {}", mealTypeView);
        if (!mealTypesService.updateMealType(mealTypeView)) {
            throw new FoodcalcException("Meal type failed to update");
        }
        return mealTypeView;
    }

    public void deleteMealType(@PathVariable("id") int id) {
        LOG.debug("Deleting meal type id = {}", id);
        if (!mealTypesService.deleteMealType(id)) {
            throw new FoodcalcException("Meal type failed to delete");
        }
    }
}
