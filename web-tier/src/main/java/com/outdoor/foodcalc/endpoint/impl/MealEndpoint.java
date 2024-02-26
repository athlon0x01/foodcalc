package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.model.dish.DishView;
import com.outdoor.foodcalc.model.meal.MealInfo;
import com.outdoor.foodcalc.model.meal.MealView;
import com.outdoor.foodcalc.service.MealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("${spring.data.rest.basePath}/plans/{planId}/days/{dayId}/meals")
public class MealEndpoint extends AbstractEndpoint {
    //TODO add to Dish table `template` column to differentiate template dishes from meal dishes

    private static final Logger LOG = LoggerFactory.getLogger(MealEndpoint.class);
    private final MealService mealService;

    public MealEndpoint(MealService mealService) {
        this.mealService = mealService;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<MealView> getAllMeals(@PathVariable("planId") long planId,
                                      @PathVariable("dayId") long dayId) {
        LOG.debug("Getting food day id = {} meals", dayId);
        return mealService.getAllMeals(planId, dayId);
    }

    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    public MealView getMeal(@PathVariable("planId") long planId,
                            @PathVariable("dayId") long dayId,
                            @PathVariable("id") long id) {
        LOG.debug("Getting meal = {} day - {}", id, dayId);
        return mealService.getMeal(planId, dayId, id);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMeal(@PathVariable("planId") long planId,
                           @PathVariable("dayId") long dayId,
                           @PathVariable("id") long id) {
        LOG.debug("Removing meal id = {}, day - {}", id, dayId);
        mealService.deleteMeal(planId, dayId, id);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public MealInfo addMeal(@PathVariable("planId") long planId,
                            @PathVariable("dayId") long dayId,
                            @RequestBody @Valid MealInfo meal) {
        LOG.debug("Adding new meal, day - {}", dayId);
        return mealService.addMeal(planId, dayId, meal);
    }

    @PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMeal(@PathVariable("planId") long planId,
                           @PathVariable("dayId") long dayId,
                           @PathVariable("id") long id,
                           @RequestBody @Valid MealInfo newMeal) {
        verifyEntityId(id, newMeal);
        LOG.debug("Updating meal id = {}, day - {}", id, dayId);
        mealService.updateMeal(planId, dayId, newMeal);
    }

    @PostMapping(path = "{mealId}/dishes/{id}", produces = APPLICATION_JSON_VALUE)
    public DishView addMealDish(@PathVariable("planId") long planId,
                                @PathVariable("dayId") long dayId,
                                @PathVariable("mealId") long mealId,
                                @PathVariable("id") long id) {
        LOG.debug("Adding new dish to meal - {}, day - {}", mealId, dayId);
        return mealService.addMealDish(planId, dayId, mealId, id);
    }
}
