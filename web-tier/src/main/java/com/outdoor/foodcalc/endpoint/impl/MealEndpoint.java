package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.model.dish.DishView;
import com.outdoor.foodcalc.model.meal.MealInfo;
import com.outdoor.foodcalc.model.meal.MealView;
import com.outdoor.foodcalc.service.DishService;
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

    private static final Logger LOG = LoggerFactory.getLogger(MealEndpoint.class);
    private final MealService mealService;
    private final DishService dishService;

    public MealEndpoint(MealService mealService, DishService dishService) {
        this.mealService = mealService;
        this.dishService = dishService;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<MealView> getAllMealsForDay(@PathVariable("planId") long planId,
                                      @PathVariable("dayId") long dayId) {
        LOG.debug("Getting meals for day id = {}, plan id = {}", dayId, planId);
        return mealService.getDayMeals(dayId);
    }

    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    public MealView getMeal(@PathVariable("planId") long planId,
                            @PathVariable("dayId") long dayId,
                            @PathVariable("id") long id) {
        LOG.debug("Getting meal = {} day - {}, plan - {}", id, dayId, planId);
        return mealService.getMeal(id);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMeal(@PathVariable("planId") long planId,
                           @PathVariable("dayId") long dayId,
                           @PathVariable("id") long id) {
        LOG.debug("Removing meal id = {}, day - {}", id, dayId);
        mealService.deleteMeal(planId, id);
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
        mealService.updateMeal(planId, newMeal);
    }

    @PostMapping(path = "{mealId}/dishes/{id}", produces = APPLICATION_JSON_VALUE)
    public DishView addMealDish(@PathVariable("planId") long planId,
                                @PathVariable("dayId") long dayId,
                                @PathVariable("mealId") long mealId,
                                @PathVariable("id") long id) {
        LOG.debug("Adding new dish {} to plan - {}, day - {}, meal - {}", id, planId, dayId, mealId);
        return dishService.addMealDish(planId, mealId, id);
    }

    @DeleteMapping("{mealId}/dishes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMealDish(@PathVariable("planId") long planId,
                                @PathVariable("dayId") long dayId,
                                @PathVariable("mealId") long mealId,
                                @PathVariable("id") long id) {
        LOG.debug("Removing dish - {} from plan - {}, day - {}, meal - {}", id, planId, dayId, mealId);
        dishService.deleteMealDish(planId, mealId, id);
    }
}
