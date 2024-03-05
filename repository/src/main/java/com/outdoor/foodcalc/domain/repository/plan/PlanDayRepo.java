package com.outdoor.foodcalc.domain.repository.plan;

import com.outdoor.foodcalc.domain.model.plan.FoodPlan;
import com.outdoor.foodcalc.domain.model.plan.PlanDay;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class PlanDayRepo implements IPlanDayRepo {

    @Override
    public List<PlanDay> getPlanDays(long planId) {
        return Collections.emptyList();
    }

    @Override
    public Optional<PlanDay> getPlanDay(long planId, long id) {
        return Optional.empty();
    }

    @Override
    public long addPlanDay(long planId, PlanDay day) {
        return 0;
    }

    @Override
    public boolean updatePlanDayInfo(PlanDay day) {
        return false;
    }

    @Override
    public boolean deletePlanDay(long planId, long id) {
        return false;
    }

    @Override
    public boolean existsPlanDay(long id) {
        return false;
    }

    @Override
    public boolean addMealsToDay(PlanDay day) {
        return false;
    }

    @Override
    public long deleteDayMeals(long dayId) {
        return 0;
    }

    @Override
    public boolean addDaysToPlan(FoodPlan plan) {
        return false;
    }

    @Override
    public long deleteFoodPlanDays(long planId) {
        return 0;
    }
}
