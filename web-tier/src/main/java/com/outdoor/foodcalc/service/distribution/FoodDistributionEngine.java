package com.outdoor.foodcalc.service.distribution;

import com.outdoor.foodcalc.domain.exception.FoodcalcException;
import com.outdoor.foodcalc.domain.model.plan.Hiker;
import com.outdoor.foodcalc.domain.model.plan.PlanDay;
import com.outdoor.foodcalc.domain.model.plan.pack.FoodDistribution;
import com.outdoor.foodcalc.domain.model.plan.pack.HikerWithPackages;
import com.outdoor.foodcalc.domain.model.plan.pack.PackageWithProducts;
import com.outdoor.foodcalc.domain.service.plan.HikerDomainService;
import com.outdoor.foodcalc.domain.service.plan.PlanDayDomainService;
import com.outdoor.foodcalc.domain.service.plan.FoodPackageDomainService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
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
                .sorted(Collections.reverseOrder(
                        Comparator.comparingDouble(pack -> pack.getEstimatedWeight(members))))
                .collect(Collectors.toList());
    }

    public FoodDistribution findBestDistribution(long planId) {
        List<Hiker> hikers = hikerDomainService.getPlanHikers(planId).stream()
                .sorted(Comparator.comparingDouble(Hiker::getWeightCoefficient).reversed())
                .collect(Collectors.toList());
        List<PlanDay> planDays = dayDomainService.getPlanDaysNoProducts(planId).stream()
                .sorted(Comparator.comparing(PlanDay::getDate))
                .collect(Collectors.toList());
        List<PackageWithProducts> packagesWithProductsForPlan = getPackagesWithProductsForPlan(planId, hikers.size());
        if (packagesWithProductsForPlan.isEmpty()) {
            throw new FoodcalcException("Couldn't do food distribution without packages");
        }
        FoodDistributionsStorage storage = new FoodDistributionsStorage(
                hikers, planDays, packagesWithProductsForPlan);

        //here we've got set of food distributions for last day
        PlanDay lastDay = planDays.get(0);
        Set<Long> lastDayIds = Collections.singleton(lastDay.getDayId());
        FoodDistribution emptyHikersDistribution = FoodDistribution.builder()
                .day(lastDay)
                .allDays(lastDayIds)
                .hikerPackages(hikers.stream()
                        .map(hiker -> HikerWithPackages.builder()
                                .hiker(hiker)
                                .build())
                        .collect(Collectors.toList()))
                .build();
        storage.buildInitialDistributionSetForFirstDay(emptyHikersDistribution, lastDayIds);

        //we continue building distributions for pre last day and so forth and going to first day searching for best distribution
        var best = storage.recursiveSearch(0, lastDayIds);
        return best.orElse(storage.getBest());
    }
}
