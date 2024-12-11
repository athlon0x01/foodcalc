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
    private final PlanDay currentDay;
    private final Set<Long> allDays;
    private final double currentWeight;
    private double minDeviation;
    private double threshold;
    private Set<FoodDistribution> dayDistributions;
    private FoodDistribution best;
}
