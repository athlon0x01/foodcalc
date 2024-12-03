package com.outdoor.foodcalc.domain.model.plan.pack;

import com.outdoor.foodcalc.domain.model.plan.PlanDay;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;

@Data
@Jacksonized
@Builder
public class SameDayFoodDistributions {
    private final PlanDay day;
    private Set<FoodDistribution> dayDistributions;
}
