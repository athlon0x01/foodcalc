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
    private boolean foundSolution = false;
    private int membersCount;

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

        this.membersCount = plan.getMembers().size();

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

            // для логування
            System.out.println("знайдено розподіл:");
            bestSolution.forEach(s -> System.out.println(s.getHiker().getName() + " -> " + s.getAssignedPackages()));

            return;
        }

        LocalDate currentDay = sortedDates.get(dayIndex);
        List<PackageWithProducts> dayPackages = getUnassignedPackages(states, currentDay);

        // Сортування пакунків
        dayPackages.sort(Comparator.comparingDouble(
                p -> -p.getWeightForDay(currentDay, membersCount)));


        // для логування
        System.out.println("\n День " + currentDay + ": " + dayPackages.size() + " пакунків");
        // Логування пакунків поточного дня
        System.out.println("📦 Пакунки на день " + currentDay + ":");
        for (PackageWithProducts pack : dayPackages) {
            double total = pack.getProductsWeight(); // загальна вага всіх продуктів
            double dayWeight = pack.getPackageDays().stream()
                    .filter(pd -> pd.getDate().equals(currentDay))
                    .mapToDouble(PackageDayProducts::getWeight)
                    .sum();

            System.out.printf("  - %s (загальна=%.1fг; %s=%.1fг)%n",
                    pack.getFoodPackage().getName(),
                    total,
                    currentDay,
                    dayWeight
            );
        }

        // якщо на день немає пакунків — просто переходимо далі
        if (dayPackages.isEmpty()) {
            branchAndBound(dayIndex + 1, states);
            return;
        }

        // сортування hikers
        if (dayIndex == 0) {
            states.sort(Comparator.comparingDouble(s -> -s.getHiker().getWeightCoefficient())); // сильніші спочатку
            System.out.println("\nСортування D" + dayIndex + " (" + sortedDates.get(dayIndex) + ") за силою:");
            for (HikerState h : states) {
                System.out.printf("  %s (coeff=%.2f)%n", h.getHiker().getName(), h.getHiker().getWeightCoefficient());
            }
        } else {
            states.sort(Comparator.comparingDouble(s -> s.getTotalWeightUpTo(sortedDates.get(dayIndex)))); // менше навантажені спочатку

            System.out.println("\nСортування D" + dayIndex + " (" + currentDay + ") за сумарним навантаженням:");
            for (HikerState h : states) {
                double load = h.getTotalWeightUpTo(currentDay);
                System.out.printf("  %s -> loadUpTo[%s]=%.2f г%n", h.getHiker().getName(), currentDay, load);
            }
        }

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
            // для логування
            System.out.println("Пробуємо дати " + pack.getFoodPackage().getName() +
                    " туристу " + hiker.getHiker().getName() +
                    " (dayWeight=" + pack.getWeightForDay(currentDay, membersCount) + ")");

            hiker.addPackage(pack, membersCount);

            if (isFeasible(hiker, currentDay, states, tolerance)) {
                // для логування
                System.out.println("Припустимо, йдемо далі");
                // розподіляємо решту пакунків цього ж дня
                assignPackagesOfDay(currentDay, next, states, dayIndex);
            } else {
                // для логування
                System.out.println("Не припустимо, відкочуємо");
            }

            // відкат стану (повернення назад)
            hiker.removePackage(pack, membersCount);

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

        // для логування
        System.out.printf("    [%s] day=%s actual=%.2f target=%.2f dev=%.2f tol=%.2f%n",
                current.getHiker().getName(), day, actual, target, deviation, tolerance);

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
                : 0.0;

        // Обчислити реальну вагу ДЛЯ ВСІЄЇ ГРУПИ на поточний день (з members, volumeCoeff, тарою в останній день
        List<PackageWithProducts> currentDayPackages = packagesByDate.getOrDefault(currentDay, Collections.emptyList());

        double totalWeightThisDay = currentDayPackages.stream()
                .mapToDouble(p -> p.getWeightForDay(currentDay, membersCount))
                .sum();

        System.out.printf(
                "   [TargetCalc] Hiker=%s prev=%.2f totalWeightThisDay=%.2f coeff=%.2f members=%d -> target=%.2f%n",
                hikerState.getHiker().getName(),
                previousTarget,
                totalWeightThisDay,
                hikerState.getHiker().getWeightCoefficient(),
                totalHikers,
                previousTarget + (totalWeightThisDay * hikerState.getHiker().getWeightCoefficient() / totalHikers)
        );

        // Формула цільової ваги для хайкера на поточний день
        return previousTarget + (totalWeightThisDay * hikerState.getHiker().getWeightCoefficient() / totalHikers);
    }
}
