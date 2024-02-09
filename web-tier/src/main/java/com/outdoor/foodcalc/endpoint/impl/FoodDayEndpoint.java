package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.model.dish.DishView;
import com.outdoor.foodcalc.model.plan.FoodDayInfo;
import com.outdoor.foodcalc.model.plan.FoodDayView;
import com.outdoor.foodcalc.service.FoodDayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("${spring.data.rest.basePath}/plans/{planId}/days")
public class FoodDayEndpoint extends AbstractEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(FoodDayEndpoint.class);
    private final FoodDayService foodDayService;

    public FoodDayEndpoint(FoodDayService foodDayService) {
        this.foodDayService = foodDayService;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<FoodDayView> getAllDays(@PathVariable("planId") long planId) {
        LOG.debug("Getting food plan id = {} days", planId);
        return foodDayService.getAllDays(planId);
    }

    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    public FoodDayView getDay(@PathVariable("planId") long planId,
                              @PathVariable("id") long id) {
        LOG.debug("Getting food plan id = {} day - {}", planId, id);
        return foodDayService.getDay(planId, id);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFoodDay(@PathVariable("planId") long planId,
                              @PathVariable("id") long id) {
        LOG.debug("Removing food plan id = {}, day - {}", planId, id);
        foodDayService.deleteFoodDay(planId, id);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public FoodDayInfo addFoodDay(@PathVariable("planId") long planId,
                                  @RequestBody @Valid FoodDayInfo foodDay) {
        LOG.debug("Adding new food day, plan - {}", planId);
        return foodDayService.addFoodDay(planId, foodDay);
    }

    @PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateFoodDay(@PathVariable("planId") long planId,
                              @PathVariable("id") long id,
                              @RequestBody @Valid FoodDayInfo foodDay) {
        verifyEntityId(id, foodDay);
        LOG.debug("Updating food plan id = {}, day - {}", planId, id);
        foodDayService.updateFoodDay(planId, id, foodDay);
    }

    @PostMapping(path = "{dayId}/dishes/{id}", produces = APPLICATION_JSON_VALUE)
    public DishView addDayDish(@PathVariable("planId") long planId,
                               @PathVariable("dayId") long dayId,
                               @PathVariable("id") long id) {
        LOG.debug("Adding new dish to day - {}", dayId);
        return foodDayService.addDayDish(planId, dayId, id);
    }
}
