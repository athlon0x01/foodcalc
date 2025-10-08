package com.outdoor.foodcalc.service.distributionBnbManual;

import com.outdoor.foodcalc.domain.exception.FoodcalcException;
import com.outdoor.foodcalc.domain.model.plan.FoodPlan;
import com.outdoor.foodcalc.domain.model.plan.pack.HikerState;
import com.outdoor.foodcalc.domain.model.plan.pack.HikerWithPackages;
import com.outdoor.foodcalc.domain.model.plan.pack.PackageDayProducts;
import com.outdoor.foodcalc.domain.model.plan.pack.PackageWithProducts;
import com.outdoor.foodcalc.domain.service.plan.FoodPackageDomainService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ManualBnBDistributionService {
    private final FoodPackageDomainService foodPackageDomainService;

    private List<LocalDate> sortedDates;
    private Map<LocalDate, List<PackageWithProducts>> packagesByDate;
    private List<HikerState> bestSolution;
    private double bestDeviation = Double.MAX_VALUE;
    private boolean foundSolution = false;

    public ManualBnBDistributionService(FoodPackageDomainService foodPackageDomainService) {
        this.foodPackageDomainService = foodPackageDomainService;
    }

    // Отримуємо всі пакунки плану з продуктами
    public List<PackageWithProducts> getPackagesWithProductsForPlan(long planId, int members) {
        var packages = foodPackageDomainService.getPackagesWithProductsForPlan(planId);
        return packages.values().stream()
                .sorted(Collections.reverseOrder(
                        Comparator.comparingDouble(pack -> pack.getEstimatedWeight(members))))
                .collect(Collectors.toList());
    }

    // Головний метод — пошук найкращого розподілу
    public List<HikerWithPackages> findBestDistribution(FoodPlan plan, List<PackageWithProducts> packages) {
        prepareData(packages);

        List<HikerState> states = plan.getMembers().stream()
                .map(HikerState::new)
                .collect(Collectors.toList());

        branchAndBound(0, states);

        if (bestSolution == null) {
            throw new FoodcalcException("Не вдалося знайти допустимий розподіл пакунків");
        }

        return bestSolution.stream()
                .map(s -> HikerWithPackages.builder()
                        .hiker(s.getHiker())
                        .packages(new HashSet<>(s.getAssignedPackages()))
                        .build())
                .collect(Collectors.toList());
    }

    // Групує пакунки за датами і сортує дні
    private void prepareData(List<PackageWithProducts> packages) {
        packagesByDate = new HashMap<>();
        for (PackageWithProducts pack : packages) {
            for (PackageDayProducts p : pack.getPackageDays()) {
                packagesByDate.computeIfAbsent(p.getDate(), k -> new ArrayList<>()).add(pack);
            }
        }
        sortedDates = new ArrayList<>(packagesByDate.keySet());
        sortedDates.sort(Comparator.reverseOrder()); // D0 → D1 → D2...
    }

    // Рекурсивний обхід дерева рішень (Branch and Bound)
    private void branchAndBound(int dayIndex, List<HikerState> states) {
        if (foundSolution) return; // можна зупинитися при першому валідному рішенні

        // базовий випадок: усі дні розподілені
        if (dayIndex >= sortedDates.size()) {
            foundSolution = true;
            bestSolution = states.stream()
                    .map(HikerState::cloneState)
                    .collect(Collectors.toList());
            return;
        }

        LocalDate currentDay = sortedDates.get(dayIndex);
        List<PackageWithProducts> dayPackages = getUnassignedPackages(states, currentDay);

        // якщо на день немає пакунків — просто переходимо далі
        if (dayPackages.isEmpty()) {
            branchAndBound(dayIndex + 1, states);
            return;
        }

        // сортування hikers
        if (dayIndex == 0) {
            states.sort(Comparator.comparingDouble(s -> -s.getHiker().getWeightCoefficient())); // сильніші спочатку
        } else {
            states.sort(Comparator.comparingDouble(s -> s.getTotalWeightUpTo(sortedDates.get(dayIndex - 1)))); // менше навантажені спочатку
        }

        // Сортування пакунків
        dayPackages.sort(Comparator.comparingDouble(p -> -p.getProductsWeight()));

        // розподіляємо всі пакунки поточного дня
        assignPackagesOfDay(currentDay, dayPackages, states, dayIndex);

        // після завершення поточного дня переходимо до наступного
        branchAndBound(dayIndex + 1, states);
    }

    private void assignPackagesOfDay(LocalDate currentDay,
                                     List<PackageWithProducts> remainingPacks,
                                     List<HikerState> states,
                                     int dayIndex) {
        if (foundSolution) return; // якщо вже знайшли рішення — далі не перебираємо

        // якщо всі пакунки поточного дня вже розподілені
        if (remainingPacks.isEmpty()) {
            return;
        }

        PackageWithProducts pack = remainingPacks.get(0);
        List<PackageWithProducts> next = remainingPacks.subList(1, remainingPacks.size());

        double tolerance = (dayIndex == 0) ? 0.3 : 0.1;

        // пробуємо призначити цей пакунок кожному туристу
        for (HikerState hiker : states) {
            hiker.addPackage(pack);

            if (isFeasible(hiker, currentDay, states, tolerance)) {
                // розподіляємо решту пакунків цього ж дня
                assignPackagesOfDay(currentDay, next, states, dayIndex);
            }

            // відкат стану (повернення назад)
            hiker.removePackage(pack);

            if (foundSolution) return; // якщо вже знайшли рішення — виходимо раніше
        }
    }

    // Відбираємо непризначені пакунки
    private List<PackageWithProducts> getUnassignedPackages(List<HikerState> states, LocalDate day) {
        Set<PackageWithProducts> assigned = states.stream()
                .flatMap(h -> h.getAssignedPackages().stream())
                .collect(Collectors.toSet());

        return packagesByDate.getOrDefault(day, Collections.emptyList()).stream()
                .filter(p -> !assigned.contains(p))
                .collect(Collectors.toList());
    }

    // Перевірка меж (чи припустиме поточне рішення)
    private boolean isFeasible(HikerState current, LocalDate day, List<HikerState> all, double tolerance) {
        double target = dailyTargetOfHiker(current, day, all);
        current.setTargetForDay(day, target);

        double actual = current.getWeight(day);
        double deviation = Math.abs(actual - target) / (target == 0 ? 1 : target);
        return deviation <= tolerance;
    }

    // Цільова вага туриста
    private double dailyTargetOfHiker(HikerState hikerState, LocalDate currentDay, List<HikerState> allHikers) {
        int totalHikers = allHikers.size();
        int currentIndex = sortedDates.indexOf(currentDay);

        // Знайти попередній день (якщо є)
        LocalDate previousDay = (currentIndex < sortedDates.size() - 1)
                ? sortedDates.get(currentIndex + 1)
                : null;

        // Отримати базову цільову вагу з попереднього дня
        double previousTarget = (previousDay != null)
                ? hikerState.getTargetForDay(previousDay)
                : 0;

        // Обчислити суму ваг пакунків цього дня
        List<PackageWithProducts> currentDayPackages = packagesByDate.getOrDefault(currentDay, Collections.emptyList());
        double totalWeightThisDay = currentDayPackages.stream()
                .flatMap(p -> p.getPackageDays().stream())
                .filter(pd -> pd.getDate().equals(currentDay))
                .mapToDouble(PackageDayProducts::getWeight)
                .sum();

        // Формула цільової ваги для поточного дня
        return previousTarget + (totalWeightThisDay * hikerState.getHiker().getWeightCoefficient() / totalHikers);
    }
}
