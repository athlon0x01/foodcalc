package com.outdoor.foodcalc.domain.repository.plan;

import com.outdoor.foodcalc.domain.model.plan.PlanDay;

import java.util.List;
import java.util.Optional;

public interface IPlanDayRepo {

    List<PlanDay> getAllPlanDays();

    Optional<PlanDay> getPlanDay(long id);

    long addPlanDay(PlanDay day);

    boolean updatePlanDay(PlanDay day);

    boolean deletePlanDay(long id);

    boolean existsPlanDay(long id);
}
