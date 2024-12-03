package com.outdoor.foodcalc.domain.model.plan.pack;

import com.outdoor.foodcalc.domain.model.plan.PlanDay;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Jacksonized
@Builder(toBuilder = true)
public class FoodDistribution {
    @EqualsAndHashCode.Include
    private final PlanDay day;
    @EqualsAndHashCode.Include
    @Builder.Default
    private Set<HikerWithPackages> hikerPackages = new TreeSet<>(Comparator.comparingDouble(hp -> hp.getHiker().getWeightCoefficient()));
    @Builder.Default
    private boolean processed = false;
}
