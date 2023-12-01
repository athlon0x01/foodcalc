package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.plan.FoodPlan;
import com.outdoor.foodcalc.model.plan.FoodDayView;
import com.outdoor.foodcalc.model.plan.FoodPlanView;
import com.outdoor.foodcalc.model.plan.SimpleFoodPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("${spring.data.rest.basePath}/plans")
public class FoodPlanEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(FoodPlanEndpoint.class);
    private final FoodDayEndpoint foodDayEndpoint;
    private final List<FoodPlan> foodPlans;

    public FoodPlanEndpoint(FoodDayEndpoint foodDayEndpoint) {
        this.foodDayEndpoint = foodDayEndpoint;
        FoodPlan planA = new FoodPlan(1L, "Test plan A", "", 2, 5, Collections.emptyList());
        FoodPlan planB = new FoodPlan(2L, "Test food plan B", "", 3, 8, Collections.emptyList());
        this.foodPlans = new ArrayList<>();
        foodPlans.add(planA);
        foodPlans.add(planB);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<SimpleFoodPlan> getAllFoodPlans() {
        LOG.debug("Getting all food plans");
        return foodPlans.stream()
                .map(this::mapPlan)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    public FoodPlanView getFoodPlan(@PathVariable("id") long id) {
        LOG.debug("Getting food plan id = {}", id);
        return getFirstPlan(id).map(this::mapPlanView)
                .orElseThrow(() -> new NotFoundException("Food plan with id = " + id + " wasn't found"));
    }

    private Optional<FoodPlan> getFirstPlan(long id) {
        return foodPlans.stream()
                .filter(foodPlan -> id == foodPlan.getId())
                .findFirst();
    }

    private SimpleFoodPlan mapPlan(FoodPlan plan) {
        List<FoodDayView> days = foodDayEndpoint.getAllDays(plan.getId());
        return SimpleFoodPlan.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .members(plan.getMembers())
                .duration(days.size())
                .build();
    }

    private FoodPlanView mapPlanView(FoodPlan plan) {
        return FoodPlanView.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .members(plan.getMembers())
                .days(foodDayEndpoint.getAllDays(plan.getId()))
                .calorific(plan.getCalorific())
                .carbs(plan.getCarbs())
                .fats(plan.getFats())
                .proteins(plan.getProteins())
                .weight(plan.getWeight())
                .build();
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFoodPlan(@PathVariable("id") long id) {
        LOG.debug("Removing food plan id = {}", id);
        getFirstPlan(id).ifPresent(foodPlans::remove);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public SimpleFoodPlan addFoodPlan(@RequestBody @Valid SimpleFoodPlan foodPlan) {
        LOG.debug("Adding new food");
        long maxId = foodPlans.stream().map(FoodPlan::getId).max(Long::compareTo).orElse(1L) + 1L;
        FoodPlan plan = new FoodPlan(maxId, foodPlan.getName(), null, foodPlan.getMembers(), 0, Collections.emptyList());
        foodPlans.add(plan);
        foodPlan.setId(maxId);
        return foodPlan;
    }

    @PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE)
    public void updateFoodPlan(@PathVariable("id") long id,
                               @RequestBody @Valid SimpleFoodPlan foodPlan) {
        LOG.debug("Updating food plan id = {}", id);
        FoodPlan plan = getFirstPlan(id)
                .orElseThrow(() -> new NotFoundException("Food plan with id = " + id + " wasn't found"));
        plan.setName(foodPlan.getName());
        plan.setDescription(foodPlan.getDescription());
        plan.setMembers(foodPlan.getMembers());
    }
}
