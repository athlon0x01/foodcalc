package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.model.plan.FoodPlan;
import com.outdoor.foodcalc.domain.model.plan.PlanDay;
import com.outdoor.foodcalc.model.plan.FoodPlanView;
import com.outdoor.foodcalc.model.plan.FoodPlanInfo;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FoodPlanService {

    private final FoodPlansRepo repository;
    private final FoodDayService dayService;

    public FoodPlanService(FoodPlansRepo repository, FoodDayService foodDayService) {
        this.repository = repository;
        this.dayService = foodDayService;
    }

    public List<FoodPlanInfo> getAllFoodPlans() {
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

    public FoodPlanInfo addFoodPlan(FoodPlanInfo foodPlan) {
        FoodPlan plan = new FoodPlan(repository.getMaxPlanIdAndIncrement(), foodPlan.getName(), null, foodPlan.getMembers(), Collections.emptyList());
        var now = ZonedDateTime.now();
        plan.setCreatedOn(now);
        plan.setLastUpdated(now);
        foodPlan.setId(plan.getId());
        repository.addFoodPlan(plan);
        return foodPlan;
    }

    public void updateFoodPlan(long id, FoodPlanInfo foodPlan) {
        FoodPlan plan = repository.getFoodPlan(id);
        plan.setName(foodPlan.getName());
        plan.setDescription(foodPlan.getDescription());
        plan.setMembers(foodPlan.getMembers());
        plan.setLastUpdated(ZonedDateTime.now());
        List<PlanDay> newDays = new ArrayList<>();
        //reorder days in plan
        foodPlan.getDays().forEach(dayId -> getDayById(plan.getDays(), dayId)
                .ifPresent(newDays::add));
        plan.setDays(newDays);
    }

    private Optional<PlanDay> getDayById(List<PlanDay> days, long id) {
        return days.stream()
                .filter(day -> day.getDayId() == id)
                .findFirst();
    }

    private FoodPlanInfo mapPlan(FoodPlan plan) {
        return FoodPlanInfo.builder()
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
