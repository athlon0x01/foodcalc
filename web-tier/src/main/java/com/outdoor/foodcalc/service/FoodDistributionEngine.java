package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.model.plan.Hiker;
import com.outdoor.foodcalc.domain.model.plan.PlanDay;
import com.outdoor.foodcalc.domain.model.plan.pack.FoodDistribution;
import com.outdoor.foodcalc.domain.model.plan.pack.PackageWithProducts;
import com.outdoor.foodcalc.domain.service.plan.HikerDomainService;
import com.outdoor.foodcalc.domain.service.plan.PlanDayDomainService;
import com.outdoor.foodcalc.domain.service.plan.pack.FoodDistributionsStorage;
import com.outdoor.foodcalc.domain.service.plan.pack.FoodPackageDomainService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodDistributionEngine {

    private final FoodPackageDomainService foodPackageDomainService;
    private final HikerDomainService hikerDomainService;
    private final PlanDayDomainService dayDomainService;

    public FoodDistributionEngine(FoodPackageDomainService foodPackageDomainService, HikerDomainService hikerDomainService, PlanDayDomainService dayDomainService) {
        this.foodPackageDomainService = foodPackageDomainService;
        this.hikerDomainService = hikerDomainService;
        this.dayDomainService = dayDomainService;
    }

    public List<PackageWithProducts> getPackagesWithProductsForPlan(long planId, int members) {
        var packages = foodPackageDomainService.getPackagesWithProductsForPlan(planId);
        return packages.values().stream()
                .sorted(Comparator.comparingDouble(pack -> pack.getEstimatedWeight(members)))
                .collect(Collectors.toList());
    }

    public FoodDistribution findBestDistribution(long planId) {
        List<Hiker> hikers = hikerDomainService.getPlanHikers(planId).stream()
                .sorted(Comparator.comparingDouble(Hiker::getWeightCoefficient))
                .collect(Collectors.toList());
        List<PlanDay> planDays = dayDomainService.getPlanDaysNoProducts(planId).stream()
                .sorted(Comparator.comparing(PlanDay::getDate))
                .collect(Collectors.toList());
        FoodDistributionsStorage storage = new FoodDistributionsStorage(
                hikers, planDays, getPackagesWithProductsForPlan(planId, hikers.size()));
        return storage.getBest();
    }
}
