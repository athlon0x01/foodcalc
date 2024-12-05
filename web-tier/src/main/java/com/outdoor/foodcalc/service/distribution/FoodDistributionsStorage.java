package com.outdoor.foodcalc.service.distribution;

import com.outdoor.foodcalc.domain.model.plan.Hiker;
import com.outdoor.foodcalc.domain.model.plan.PlanDay;
import com.outdoor.foodcalc.domain.model.plan.pack.FoodDistribution;
import com.outdoor.foodcalc.domain.model.plan.pack.HikerWithPackages;
import com.outdoor.foodcalc.domain.model.plan.pack.PackageWithProducts;
import com.outdoor.foodcalc.domain.model.plan.pack.SameDayFoodDistributions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class FoodDistributionsStorage {
    private final List<Hiker> allHikers;
    private final List<PlanDay> allDays;
    private final List<PackageWithProducts> allPackages;
    private final Map<Long, List<PackageWithProducts>> packagesByDay;
    private final List<SameDayFoodDistributions> allDistributions;
    private final double eps;

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
                    .minDeviation(weight / 4) //threshold for obviously NOT effective distributions
                    .build());
            packagesByDay.put(theDay.getDayId(), new ArrayList<>());
        }
        //put packages into days \ packages map
        this.allPackages.forEach(pack -> pack.getDayProducts().keySet()
                .forEach(k -> packagesByDay.get(k).add(pack)));
        //threshold of weight deviation in percentage of mean weight (10% for now)
        eps = allDistributions.get(allDistributions.size() - 1).getCurrentWeight() / allHikers.size() * 0.1;
    }

    private double calculatePackagesEstimatedWeightForDays(Set<Long> days) {
        return allPackages.stream()
                .mapToDouble(pack -> pack.getEstimatedWeight(days, allHikers.size()))
                .sum();
    }

    public List<PackageWithProducts> getPackagesForDays(Set<Long> days) {
        return days.stream()
                .map(packagesByDay::get)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<PackageWithProducts> getNewPackagesForDays(Set<Long> days, long newDay) {
        Set<Long> previousDays = new HashSet<>(days);
        previousDays.remove(newDay);
        var previousPackageIds = previousDays.stream()
                .map(packagesByDay::get)
                .flatMap(List::stream)
                .map(pack -> pack.getFoodPackage().getId())
                .collect(Collectors.toSet());
        return packagesByDay.get(newDay).stream()
                .filter(pack -> !previousPackageIds.contains(pack.getFoodPackage().getId()))
                .collect(Collectors.toList());
    }

    //deep copy
    public FoodDistribution copyDistribution(FoodDistribution distribution) {
        List<HikerWithPackages> newHikerPackages = new ArrayList<>(distribution.getHikerPackages().size());
        for (HikerWithPackages hiker : distribution.getHikerPackages()) {
            newHikerPackages.add(copyHikerWithPackages(hiker));
        }
        return FoodDistribution.builder()
                .day(distribution.getDay())
                .allDays(new HashSet<>(distribution.getAllDays()))
                .hikerPackages(newHikerPackages)
                .build();
    }

    public HikerWithPackages copyHikerWithPackages(HikerWithPackages hikerPackages) {
        HashSet<PackageWithProducts> packagesCopy;
        if (hikerPackages.getPackages() == null) {
            packagesCopy = new HashSet<>();
        } else {
            packagesCopy = new HashSet<>(hikerPackages.getPackages());
        }
        return hikerPackages.toBuilder()
                .packages(packagesCopy)
                .build();
    }

    public FoodDistribution copyDistributionWithNewPackage(FoodDistribution distribution, long hikerId, PackageWithProducts productsPackage) {
        FoodDistribution copy = copyDistribution(distribution);
        for (HikerWithPackages hikerPackages : copy.getHikerPackages()) {
            if (hikerId == hikerPackages.getHiker().getId()) {
                hikerPackages.getPackages().add(productsPackage);
                break;
            }
        }
        return copy;
    }

    public Set<FoodDistribution> generateDistributionsAddingPackage(FoodDistribution initialDistribution, List<PackageWithProducts> productsPackage) {
        Set<FoodDistribution> generatedDistributions = new HashSet<>();
        PackageWithProducts firstPackage = productsPackage.get(0);
        //next set of productPackage distributed across all hikers
        Set<FoodDistribution> nextDistributions = new HashSet<>();
        for (HikerWithPackages hikerPackages : initialDistribution.getHikerPackages()) {
            FoodDistribution newDistribution = copyDistributionWithNewPackage(initialDistribution, hikerPackages.getHiker().getId(), firstPackage);
            nextDistributions.add(newDistribution);
        }
        if (productsPackage.size() > 1) {
            ArrayList<PackageWithProducts> remainingProducts = new ArrayList<>(productsPackage);
            remainingProducts.remove(0);
            //continue recursion for remaining packages
            nextDistributions.forEach(
                    newDistribution -> {
                        Set<FoodDistribution> nextPackageDistributions =
                                generateDistributionsAddingPackage(newDistribution, remainingProducts);
                        //collecting all results in one set
                        generatedDistributions.addAll(nextPackageDistributions);
                    });
        } else {
            //complete generation
            generatedDistributions.addAll(nextDistributions);
        }
        return generatedDistributions;
    }

    public void buildInitialDistributionSetForFirstDay(FoodDistribution emptyHikersDistribution, Set<Long> days) {
        Set<FoodDistribution> lastDayDistributions = generateDistributionsAddingPackage(emptyHikersDistribution, getPackagesForDays(days));
        SameDayFoodDistributions lastDayDistributionsContainer = allDistributions.get(0);
        lastDayDistributionsContainer.getDayDistributions()
                .addAll(lastDayDistributions.stream()
                        .filter(dist -> dist.deviation() < lastDayDistributionsContainer.getMinDeviation())
                        .collect(Collectors.toSet()));
    }

    Optional<FoodDistribution> recursiveSearch(int ndx, Set<Long> days) {
        SameDayFoodDistributions container = allDistributions.get(ndx);
        if (ndx < allDays.size() - 1) {
            //go through unprocessed distribution of the current day
            var currentDistribution = getFirstUnprocessed(container);
            while (currentDistribution.isPresent()) {
                //build distributions for next day
                Set<Long> nextDays = new HashSet<>(days);
                SameDayFoodDistributions nextDayContainer = allDistributions.get(ndx + 1);
                long nextDayId = allDays.get(ndx + 1).getDayId();
                nextDays.add(nextDayId);
                FoodDistribution nextDayDistribution = copyDistribution(currentDistribution.get()).toBuilder()
                        .day(allDays.get(ndx + 1))
                        .allDays(nextDays)
                        .build();
                Set<FoodDistribution> filteredDistributions = Collections.emptySet();
                List<PackageWithProducts> newPackages = getNewPackagesForDays(nextDays, nextDayId);
                if (!newPackages.isEmpty()) {
                    Set<FoodDistribution> nextDayDistributions = generateDistributionsAddingPackage(nextDayDistribution, newPackages);
                    filteredDistributions = nextDayDistributions.stream()
                            .filter(dist -> dist.deviation() < nextDayContainer.getMinDeviation())
                            .collect(Collectors.toSet());
                }
                //and continue recursion
                if (filteredDistributions.isEmpty()) {
                    //no food packages were added or for some reason all new distributions were filtered out
                    //so we proceed forward with nextDistribution
                    filteredDistributions = Collections.singleton(nextDayDistribution);
                }
                nextDayContainer.getDayDistributions().addAll(filteredDistributions);
                currentDistribution.get().setProcessed(true);
                //go recursively through all currentDistribution for next day
                var best = recursiveSearch(ndx + 1, nextDays);
                if (best.isPresent()) {
                    return best;
                }
                currentDistribution = getFirstUnprocessed(container);
            }
            return Optional.empty();
        } else {
            return finalSearch(container);
        }
    }

    Optional<FoodDistribution> finalSearch(SameDayFoodDistributions container) {
        //comparing with best distribution and marking as processed
        container.getDayDistributions().stream()
                .filter(d -> !d.isProcessed())
                .forEach(distribution -> compareWithBest(container, distribution));
        return Optional.ofNullable(container.getBest())
                .filter(best -> best.deviation() < eps);
    }

    void compareWithBest(SameDayFoodDistributions container, FoodDistribution distribution) {
        double deviation = distribution.deviation();
        if (deviation < container.getMinDeviation()) {
            container.setBest(distribution);
            container.setMinDeviation(deviation);
        }
        distribution.setProcessed(true);
    }

    Optional<FoodDistribution> getFirstUnprocessed(SameDayFoodDistributions container) {
        return container.getDayDistributions().stream()
                .filter(d -> !d.isProcessed())
                .findFirst();
    }

    public FoodDistribution getBest() {
        return Optional.ofNullable(allDistributions.get(allDistributions.size() - 1).getBest())
                .orElseThrow();
    }
}
