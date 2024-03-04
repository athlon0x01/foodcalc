package com.outdoor.foodcalc.domain.service.plan;

import com.outdoor.foodcalc.domain.model.plan.FoodPlan;
import com.outdoor.foodcalc.domain.repository.plan.IFoodPlanRepo;
import com.outdoor.foodcalc.domain.service.FoodPlansRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FoodPlanDomainService {

    private final IFoodPlanRepo planRepo;
    private final FoodPlansRepo tmpRepo;

    public FoodPlanDomainService(IFoodPlanRepo planRepo, FoodPlansRepo tmpRepo) {
        this.planRepo = planRepo;
        this.tmpRepo = tmpRepo;
    }

    public List<FoodPlan> getAllFoodPlans() {
        List<FoodPlan> plans = planRepo.getAllFoodPlans();
        plans.forEach(plan -> plan.setDays(tmpRepo.getPlanDays(plan.getId())));
        return plans;
    }

    public Optional<FoodPlan> getFoodPlan(long id) {
        return planRepo.getFoodPlan(id).map(plan -> {
           plan.setDays(tmpRepo.getPlanDays(plan.getId()));
           return plan;
        });
    }

    public void deleteFoodPlan(long id) {
        //TODO please make it similar to other domain delete
        planRepo.deleteFoodPlan(id);
        tmpRepo.deleteFoodPlan(id);
    }

    public FoodPlan addFoodPlan(FoodPlan plan) {
        long id = planRepo.addFoodPlan(plan);
        FoodPlan newPlan = plan.toBuilder()
                .id(id)
                .build();
        tmpRepo.addFoodPlan(id);
        return newPlan;
    }

    public void updateFoodPlan(FoodPlan plan) {
        //TODO make it similar to other domain updates
        planRepo.updateFoodPlan(plan);
    }
}
