package com.outdoor.foodcalc.domain.service.plan.pack;

import com.outdoor.foodcalc.domain.model.plan.Hiker;
import com.outdoor.foodcalc.domain.model.plan.PlanDay;
import com.outdoor.foodcalc.domain.model.plan.pack.*;

import java.util.*;

public class FoodDistributionsStorage {
    private final List<Hiker> allHikers;
    private final List<PlanDay> allDays;
    private final List<PackageWithProducts> allPackages;
    private final Map<Long, List<PackageWithProducts>> packagesByDay;
    private final List<SameDayFoodDistributions> allDistributions;

    public FoodDistributionsStorage(List<Hiker> allHikers, List<PlanDay> allDays, List<PackageWithProducts> allPackages) {
        this.allHikers = allHikers;
        this.allDays = allDays;
        this.allPackages = allPackages;
        this.allDistributions = new ArrayList<>(allDays.size());
        this.packagesByDay = new HashMap<>(allDays.size());
        //initialize packages and distributions per day storages
        Set<Long> days = new HashSet<>();
        for (PlanDay theDay : this.allDays) {
            days.add(theDay.getDayId());
            double weight = calculatePackagesEstimatedWeightForDays(days);
            allDistributions.add(SameDayFoodDistributions.builder()
                    .currentDay(theDay)
                    .allDays(days)
                    .dayDistributions(new HashSet<>())
                    .currentWeight(weight)
                    .minDeviation(weight)
                    .build());
            packagesByDay.put(theDay.getDayId(), new ArrayList<>(allPackages.size()));
        }
        //put packages into days \ packages map
        this.allPackages.forEach(pack -> pack.getDayProducts().keySet()
                .forEach(k -> packagesByDay.get(k).add(pack)));
    }

    private double calculatePackagesEstimatedWeightForDays(Set<Long> days) {
        return allPackages.stream()
                .mapToDouble(pack -> pack.getEstimatedWeight(days, allHikers.size()))
                .sum();
    }

    private boolean isFirstDay(long dayId) {
        return allDays.get(allDays.size() - 1).getDayId() == dayId;
    }

    public FoodDistribution getBest() {
        var def = FoodDistribution.builder()
                .hikerPackages(Collections.singleton(HikerWithPackages.builder()
                        .packages(Collections.singleton(PackageWithProducts.builder()
                                .foodPackage(FoodPackage.builder()
                                        .build())
                                .build()))
                        .build()))
                .build();
        return Optional.ofNullable(allDistributions.get(allDistributions.size() - 1).getBest())
                .orElse(def);
    }
}
