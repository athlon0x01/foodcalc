package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.model.plan.FoodPlanInfo;
import com.outdoor.foodcalc.model.plan.FoodPlanView;
import com.outdoor.foodcalc.service.FoodPlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("${spring.data.rest.basePath}/plans")
public class FoodPlanEndpoint extends AbstractEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(FoodPlanEndpoint.class);
    private final FoodPlanService foodPlanService;

    public FoodPlanEndpoint(FoodPlanService foodPlanService) {
        this.foodPlanService = foodPlanService;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<FoodPlanInfo> getAllFoodPlans() {
        LOG.debug("Getting all food plans");
        return foodPlanService.getAllFoodPlans();
    }

    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    public FoodPlanView getFoodPlan(@PathVariable("id") long id) {
        LOG.debug("Getting food plan id = {}", id);
        return foodPlanService.getFoodPlan(id);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFoodPlan(@PathVariable("id") long id) {
        LOG.debug("Removing food plan id = {}", id);
        foodPlanService.deleteFoodPlan(id);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public FoodPlanInfo addFoodPlan(@RequestBody @Valid FoodPlanInfo foodPlan) {
        LOG.debug("Adding new food plan");
        return foodPlanService.addFoodPlan(foodPlan);
    }

    @PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateFoodPlanInfo(@PathVariable("id") long id,
                                   @RequestBody @Valid FoodPlanInfo foodPlan) {
        verifyEntityId(id, foodPlan);
        LOG.debug("Updating food plan id = {}", id);
        foodPlanService.updateFoodPlan(id, foodPlan);
    }
}
