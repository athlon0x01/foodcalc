package com.outdoor.foodcalc.domain.repository.plan;

import com.outdoor.foodcalc.domain.model.plan.PlanDay;

import java.util.List;
import java.util.Optional;

public interface IPlanDayRepo {

    List<PlanDay> getPlanDays(long planId);

    Optional<PlanDay> getPlanDay(long planId, long id);

    long addPlanDay(long planId, PlanDay day);

    boolean updatePlanDay(PlanDay day);

    void updatePlanDayIndex(long dayId, int index);

    boolean deletePlanDay(long planId, long id);

    boolean existsPlanDay(long id);
}
