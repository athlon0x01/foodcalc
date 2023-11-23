package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.plan.FoodPlan;
import com.outdoor.foodcalc.model.plan.SimpleFoodPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("${spring.data.rest.basePath}/plans")
public class FoodPlanEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(FoodPlanEndpoint.class);

    private final List<FoodPlan> foodPlans;

    public FoodPlanEndpoint() {
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
    public SimpleFoodPlan getFoodPlan(@PathVariable("id") long id) {
        LOG.debug("Getting food plan id = {}", id);
        Optional<FoodPlan> first = foodPlans.stream().filter(foodPlan -> id == foodPlan.getId()).findFirst();
        return first.map(this::mapPlan)
                .orElseThrow(() -> new NotFoundException("Food plan with id = " + id + " wasn't found"));
    }

    private SimpleFoodPlan mapPlan(FoodPlan plan) {
        return SimpleFoodPlan.builder()
                .id(plan.getId())
                .name(plan.getName())
                .members(plan.getMembers())
                .duration(plan.getDuration())
                .build();
    }
}
