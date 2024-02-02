package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.model.plan.FoodPlan;
import com.outdoor.foodcalc.model.plan.FoodPlanView;
import com.outdoor.foodcalc.model.plan.SimpleFoodPlan;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodPlanService {

    private final FoodPlansRepo repository;
    private final FoodDayService dayService;

    public FoodPlanService(FoodPlansRepo repository, FoodDayService foodDayService) {
        this.repository = repository;
        this.dayService = foodDayService;
    }

    public List<SimpleFoodPlan> getAllFoodPlans() {
        return repository.getAllPlans().stream()
                .map(this::mapPlan)
                .collect(Collectors.toList());
    }

    public FoodPlanView getFoodPlan(long id) {
        return mapPlanView(repository.getFoodPlan(id));
    }

    public void deleteFoodPlan(long id) {
        repository.deleteFoodPlan(id);
    }

    public SimpleFoodPlan addFoodPlan(SimpleFoodPlan foodPlan) {
        FoodPlan plan = new FoodPlan(repository.getMaxPlanIdAndIncrement(), foodPlan.getName(), null, foodPlan.getMembers(), 0, Collections.emptyList());
        foodPlan.setId(plan.getId());
        repository.addFoodPlan(plan);
        return foodPlan;
    }

    public void updateFoodPlan(long id, SimpleFoodPlan foodPlan) {
        FoodPlan plan = repository.getFoodPlan(id);
        plan.setName(foodPlan.getName());
        plan.setDescription(foodPlan.getDescription());
        plan.setMembers(foodPlan.getMembers());
    }

    private SimpleFoodPlan mapPlan(FoodPlan plan) {
        return SimpleFoodPlan.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .members(plan.getMembers())
                .duration(plan.getDays().size())
                .build();
    }

    private FoodPlanView mapPlanView(FoodPlan plan) {
        return FoodPlanView.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .members(plan.getMembers())
                .days(plan.getDays().stream()
                        .map(dayService::mapView)
                        .collect(Collectors.toList()))
                .calorific(plan.getCalorific())
                .carbs(plan.getCarbs())
                .fats(plan.getFats())
                .proteins(plan.getProteins())
                .weight(plan.getWeight())
                .build();
    }
}
