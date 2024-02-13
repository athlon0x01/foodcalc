package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.endpoint.MealTypesApi;
import com.outdoor.foodcalc.model.meal.MealTypeView;
import com.outdoor.foodcalc.service.MealTypesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * REST Endpoint for Meal Type related operations
 *
 * @author Anton Borovyk.
 */
@RestController
public class MealTypesEndpoint extends AbstractEndpoint implements MealTypesApi {

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

    public MealTypeView getMealType(@PathVariable("id") long id) {
        LOG.debug("Getting meal type id = {}", id);
        return mealTypesService.getMealType(id);
    }

    public MealTypeView addMealType(@RequestBody @Valid MealTypeView mealTypeView) {
        LOG.debug("Adding new meal type - {}", mealTypeView);
        return mealTypesService.addMealType(mealTypeView.getName());
    }

    public void updateMealType(@PathVariable("id") long id,
                               @RequestBody @Valid MealTypeView mealTypeView) {
        verifyEntityId(id, mealTypeView);
        LOG.debug("Updating meal type {}", mealTypeView);
        mealTypesService.updateMealType(mealTypeView);
    }

    public void deleteMealType(@PathVariable("id") long id) {
        LOG.debug("Deleting meal type id = {}", id);
        mealTypesService.deleteMealType(id);
    }
}
