package com.outdoor.foodcalc.domain.repository.plan;

import com.outdoor.foodcalc.domain.model.plan.FoodPlan;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface IFoodPlanRepo {

    List<FoodPlan> getAllFoodPlans();

    Optional<FoodPlan> getFoodPlan(long id);

    long addFoodPlan(FoodPlan plan);

    boolean updateFoodPlan(FoodPlan foodPlan);

    boolean deleteFoodPlan(long id);

    boolean existsFoodPlan(long id);

    void saveLastUpdated(long planId, ZonedDateTime lastUpdated);

    int getDaysCountForPlan(long planId);
}
