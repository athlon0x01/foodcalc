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
        allDays.forEach(theDay -> {
            allDistributions.add(SameDayFoodDistributions.builder()
                    .day(theDay)
                    .dayDistributions(new HashSet<>())
                    .build());
            packagesByDay.put(theDay.getDayId(), new ArrayList<>(allPackages.size()));
        });
        //put packages into days \ packages map
        allPackages.forEach(pack -> pack.getDayProducts().keySet()
                .forEach(k -> packagesByDay.get(k).add(pack)));
    }

    public FoodDistribution getBest() {
        return FoodDistribution.builder()
                .hikerPackages(Collections.singleton(HikerWithPackages.builder()
                        .packages(Collections.singleton(PackageWithProducts.builder()
                                .foodPackage(FoodPackage.builder()
                                        .build())
                                .build()))
                        .build()))
                .build();
    }
}
