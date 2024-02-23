package com.outdoor.foodcalc.domain.repository.plan;

import com.outdoor.foodcalc.domain.model.plan.PlanDay;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PlanDayRepo implements IPlanDayRepo {

    @Override
    public List<PlanDay> getAllPlanDays() {
        return null;
    }

    @Override
    public Optional<PlanDay> getPlanDay(long id) {
        return Optional.empty();
    }

    @Override
    public long addPlanDay(PlanDay day) {
        return 0;
    }

    @Override
    public boolean updatePlanDay(PlanDay day) {
        return false;
    }

    @Override
    public boolean deletePlanDay(long id) {
        return false;
    }

    @Override
    public boolean existsPlanDay(long id) {
        return false;
    }
}
