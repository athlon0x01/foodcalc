package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.FoodDetailsInstance;
import com.outdoor.foodcalc.domain.model.plan.FoodPlan;
import com.outdoor.foodcalc.domain.model.plan.PlanDay;
import com.outdoor.foodcalc.domain.service.plan.FoodPlanDomainService;
import com.outdoor.foodcalc.model.plan.FoodPlanView;
import com.outdoor.foodcalc.model.plan.FoodPlanInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class FoodPlanService {

    private final FoodPlanDomainService planDomainService;
    private final FoodDayService dayService;

    public FoodPlanService(FoodPlanDomainService planDomainService, FoodDayService foodDayService) {
        this.planDomainService = planDomainService;
        this.dayService = foodDayService;
    }

    public List<FoodPlanInfo> getAllFoodPlans() {
        return planDomainService.getAllFoodPlans().stream()
                .map(this::mapPlan)
                .collect(Collectors.toList());
    }

    public FoodPlanView getFoodPlan(long id) {
        return planDomainService.getFoodPlan(id)
                .map(this::mapPlanView)
                .orElseThrow(() -> new NotFoundException("Food plan with id = " + id + " wasn't found"));
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void deleteFoodPlan(long id) {
        planDomainService.deleteFoodPlan(id);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public FoodPlanInfo addFoodPlan(FoodPlanInfo foodPlan) {
        var now = ZonedDateTime.now();
        FoodPlan plan = FoodPlan.builder()
                .name(foodPlan.getName())
                .members(foodPlan.getMembers())
                .createdOn(now)
                .lastUpdated(now)
                .build();
        return mapPlan(planDomainService.addFoodPlan(plan));
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void updateFoodPlan(FoodPlanInfo foodPlan) {
        List<PlanDay> days = foodPlan.getDays().stream()
                .map(dayId -> PlanDay.builder().dayId(dayId).build())
                .collect(Collectors.toList());
        FoodPlan plan = FoodPlan.builder()
                .id(foodPlan.getId())
                .name(foodPlan.getName())
                .members(foodPlan.getMembers())
                .description(foodPlan.getDescription())
                .lastUpdated(ZonedDateTime.now())
                .days(days)
                .build();
        planDomainService.updateFoodPlan(plan);
    }

    private FoodPlanInfo mapPlan(FoodPlan plan) {
        return FoodPlanInfo.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .members(plan.getMembers())
                .duration(plan.getDays().size())
                .createdOn(plan.getCreatedOn().withZoneSameInstant(ZoneId.systemDefault()))
                .lastUpdated(plan.getLastUpdated().withZoneSameInstant(ZoneId.systemDefault()))
                .build();
    }

    private FoodPlanView mapPlanView(FoodPlan plan) {
        FoodDetailsInstance planDetails = plan.getFoodDetails();
        return FoodPlanView.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .members(plan.getMembers())
                .createdOn(plan.getCreatedOn())
                .lastUpdated(plan.getLastUpdated())
                .days(plan.getDays().stream()
                        .map(dayService::mapView)
                        .collect(Collectors.toList()))
                .calorific(planDetails.getCalorific())
                .carbs(planDetails.getCarbs())
                .fats(planDetails.getFats())
                .proteins(planDetails.getProteins())
                .weight(planDetails.getWeight())
                .build();
    }
}
