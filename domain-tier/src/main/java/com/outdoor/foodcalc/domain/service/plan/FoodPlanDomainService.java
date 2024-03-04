package com.outdoor.foodcalc.domain.service.plan;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
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

    public FoodPlan addFoodPlan(FoodPlan plan) {
        long id = planRepo.addFoodPlan(plan);
        FoodPlan newPlan = plan.toBuilder()
                .id(id)
                .build();
        tmpRepo.addFoodPlan(id);
        return newPlan;
    }

    public void updateFoodPlan(FoodPlan plan) {
        if(!planRepo.existsFoodPlan(plan.getId())) {
            throw new NotFoundException("Food plan with id=" + plan.getId() + " doesn't exist");
        }
        if(!planRepo.updateFoodPlan(plan)) {
            throw new FoodcalcDomainException("Failed to update product category with id=" + plan.getId());
        }
    }
    public void deleteFoodPlan(long id) {
        if(!planRepo.existsFoodPlan(id)) {
            throw new NotFoundException("Food plan with id=" + id + " doesn't exist");
        }
        if (!planRepo.deleteFoodPlan(id)) {
            throw new FoodcalcDomainException("Failed to delete Food plan with id=" + id);
        }
        tmpRepo.deleteFoodPlan(id);
    }


}
