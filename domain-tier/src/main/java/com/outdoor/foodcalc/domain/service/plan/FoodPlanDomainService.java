package com.outdoor.foodcalc.domain.service.plan;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.plan.FoodPlan;
import com.outdoor.foodcalc.domain.repository.plan.IFoodPlanRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FoodPlanDomainService {

    private final IFoodPlanRepo planRepo;
    private final PlanDayDomainService dayService;

    public FoodPlanDomainService(IFoodPlanRepo planRepo, PlanDayDomainService dayService) {
        this.planRepo = planRepo;
        this.dayService = dayService;
    }

    public List<FoodPlan> getAllFoodPlansNoDays() {
        return planRepo.getAllFoodPlans();
    }

    public Optional<FoodPlan> getFoodPlan(long id) {
        return planRepo.getFoodPlan(id)
                .map(plan -> {
                    plan.setDays(dayService.getPlanDays(plan.getId()));
                    return plan;
                });
    }

    public FoodPlan addFoodPlan(FoodPlan plan) {
        long id = planRepo.addFoodPlan(plan);
        return plan.toBuilder()
                .id(id)
                .build();
    }

    public void updateFoodPlan(FoodPlan plan) {
        if(!planRepo.existsFoodPlan(plan.getId())) {
            throw new NotFoundException("Food plan with id=" + plan.getId() + " doesn't exist");
        }
        if(!planRepo.updateFoodPlan(plan)) {
            throw new FoodcalcDomainException("Failed to update food plan with id=" + plan.getId());
        }
    }

    public void deleteFoodPlan(long id) {
        if(!planRepo.existsFoodPlan(id)) {
            throw new NotFoundException("Food plan with id=" + id + " doesn't exist");
        }
        if (!planRepo.deleteFoodPlan(id)) {
            throw new FoodcalcDomainException("Failed to delete Food plan with id=" + id);
        }
    }
}
