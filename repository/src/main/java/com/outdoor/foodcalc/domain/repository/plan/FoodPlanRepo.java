package com.outdoor.foodcalc.domain.repository.plan;

import com.outdoor.foodcalc.domain.model.plan.FoodPlan;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FoodPlanRepo implements IFoodPlanRepo {
    @Override
    public List<FoodPlan> getAllFoodPlans() {
        return null;
    }

    @Override
    public Optional<FoodPlan> getFoodPlan(long id) {
        return Optional.empty();
    }

    @Override
    public long addFoodPlan(FoodPlan plan) {
        return 0;
    }

    @Override
    public boolean updateFoodPlan(FoodPlan foodPlan) {
        return false;
    }

    @Override
    public boolean deleteFoodPlan(long id) {
        return false;
    }

    @Override
    public boolean existsFoodPlan(long id) {
        return false;
    }
}
