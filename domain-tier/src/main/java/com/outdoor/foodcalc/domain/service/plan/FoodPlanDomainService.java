package com.outdoor.foodcalc.domain.service.plan;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.plan.FoodPlan;
import com.outdoor.foodcalc.domain.repository.plan.IFoodPlanRepo;
import com.outdoor.foodcalc.domain.service.FoodPlansRepo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        return new ArrayList<>(tmpRepo.getAllPlans());
    }

    public Optional<FoodPlan> getFoodPlan(long id) {
        return Optional.ofNullable(tmpRepo.getFoodPlan(id));
    }

    public void deleteFoodPlan(long id) {
        tmpRepo.deleteFoodPlan(id);
    }

    public FoodPlan addFoodPlan(FoodPlan plan) {
        FoodPlan newPlan = plan.toBuilder()
                .id(tmpRepo.getMaxPlanIdAndIncrement())
                .build();
        tmpRepo.addFoodPlan(newPlan);
        return newPlan;
    }

    public void updateFoodPlan(FoodPlan plan) {
        if(tmpRepo.getFoodPlan(plan.getId()) == null) {
            throw new NotFoundException("FoodPlan with id=" + plan.getId() + " doesn't exist");
        }
        tmpRepo.updateFoodPlan(plan);
    }
}
