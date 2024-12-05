package com.outdoor.foodcalc.domain.model.plan.pack;

import com.outdoor.foodcalc.domain.model.plan.PlanDay;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Jacksonized
@Builder(toBuilder = true)
public class FoodDistribution {
    @EqualsAndHashCode.Include
    private final PlanDay day;
    private final Set<Long> allDays;
    //each distribution must include all food plan members
    @EqualsAndHashCode.Include
    private List<HikerWithPackages> hikerPackages;
    private boolean processed;

    public double deviation() {
        double allWeight = 0.0;
        double[] coef = new double[hikerPackages.size()];
        double[] weights = new double[hikerPackages.size()];
        int ndx = 0;
        for (HikerWithPackages hp : hikerPackages) {
            weights[ndx] = hp.calculateEstimatedWeight(allDays, hikerPackages.size());
            coef[ndx] = hp.getHiker().getWeightCoefficient();
            allWeight += weights[ndx++];
        }
        double n = hikerPackages.size();
        double mean = allWeight / n;
        double deviation = 0.0;
        for (int i = 0; i < hikerPackages.size(); i++) {
            double delta = weights[i] - mean * coef[i];
            deviation += delta * delta;
        }
        return Math.sqrt(deviation / n);
    }
}
