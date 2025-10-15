package com.outdoor.foodcalc.service.distributionBnbManual;

import com.outdoor.foodcalc.domain.exception.FoodcalcException;
import com.outdoor.foodcalc.domain.model.plan.FoodPlan;
import com.outdoor.foodcalc.domain.model.plan.pack.HikerState;
import com.outdoor.foodcalc.domain.model.plan.pack.PackageDayProducts;
import com.outdoor.foodcalc.domain.model.plan.pack.PackageWithProducts;
import com.outdoor.foodcalc.domain.service.plan.FoodPackageDomainService;
import lombok.Getter;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;


import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ManualBnBDistributionService {
    private final FoodPackageDomainService foodPackageDomainService;

    @Getter
    private List<LocalDate> sortedDates;
    private Map<LocalDate, List<PackageWithProducts>> packagesByDate;
    private List<HikerState> bestSolution;
    private int membersCount;
    // Метрики для вибору найкращого рішення
    private double bestDeviation = Double.MAX_VALUE;  // найменше середнє відхилення серед усіх знайдених рішень

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
    public List<HikerState> findBestDistribution(FoodPlan plan, List<PackageWithProducts> packages) {
        prepareData(packages);
        log.info("Днів у плані: {}", sortedDates.size());
        sortedDates.forEach(d -> log.info("  {} -> {} пакунків", d, packagesByDate.get(d).size()));

        this.membersCount = plan.getMembers().size();

        Map<LocalDate, Double> groupTargets = calculateGroupTargets(sortedDates);

        // Ініціалізація станів
        List<HikerState> states = plan.getMembers().stream()
                .map(HikerState::new)
                .collect(Collectors.toList());

        // Розрахунок індивідуальних таргетів
        calculateIndividualTargets(plan, states, groupTargets);

        branchAndBound(0, states);

        if (bestSolution == null) {
            throw new FoodcalcException("Не вдалося знайти допустимий розподіл пакунків");
        }

        log.info("Знайдено розподіл для {} днів і {} туристів", sortedDates.size(), plan.getMembers().size());

        log.info("=== ФІНАЛЬНИЙ РОЗПОДІЛ ПО ТУРИСТАХ ===");

        for (HikerState hikerState : bestSolution) {
            log.info("Турист: {}", hikerState.getHiker().getName());

            Map<LocalDate, Set<PackageWithProducts>> byDay = hikerState.getAssignedByDay();
            if (byDay == null || byDay.isEmpty()) {
                log.info("  (немає пакунків)");
                continue;
            }

            // проходимо по кожному дню у правильному порядку
            for (LocalDate day : sortedDates) {
                Set<PackageWithProducts> packs = byDay.get(day);
                if (packs != null && !packs.isEmpty()) {
                    String joined = packs.stream()
                            .map(p -> {
                                // знайти загальну вагу цього пакунка на поточний день
                                double dayWeight = p.getPackageDays().stream()
                                        .filter(pd -> pd.getDate().equals(day))
                                        .mapToDouble(PackageDayProducts::getWeight)
                                        .sum();
                                return p.getFoodPackage().getName() + "(" + String.format("%.1f", dayWeight) + "г)";
                            })
                            .collect(Collectors.joining(", "));
                    log.info("  {} -> {}", day, joined);
                }
            }
        }

        log.info("=========================================");

        return bestSolution;
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
        sortedDates.sort(Comparator.naturalOrder()); // D0 → D1 → D2...
    }

    // Рекурсивний обхід дерева рішень (Branch and Bound)
    private void branchAndBound(int dayIndex, List<HikerState> states) {

        // базовий випадок: усі дні розподілені
        if (dayIndex >= sortedDates.size()) {

            // зберігаємо тільки найкраще рішення
            double currentDeviation = calculateTotalDeviation(states);
            if (currentDeviation < bestDeviation) {
                bestDeviation = currentDeviation;
                bestSolution = states.stream()
                        .map(HikerState::cloneState)
                        .collect(Collectors.toList());
                log.info("Нове найкраще рішення: середнє відхилення = {}%", String.format("%.2f", bestDeviation));
            }

            log.info("== РОЗПОДІЛ ПЕРЕД ЗБЕРЕЖЕННЯМ В bestSolution ==");
            for (HikerState h : states) {
                log.info("Турист: {}", h.getHiker().getName());
                for (LocalDate day : sortedDates) {
                    String assigned = h.getAssignedByDay()
                            .getOrDefault(day, Set.of()).stream()
                            .map(p -> p.getFoodPackage().getName())
                            .collect(Collectors.joining(", "));
                    log.info("  {} -> {}", day, assigned);
                }
            }
            log.info("===============================================");

            return; // рішення вже збережено в assignPackagesOfDay
        }

        LocalDate currentDay = sortedDates.get(dayIndex);
        List<PackageWithProducts> dayPackages = getUnassignedPackages(states, currentDay);

        // Сортування пакунків
        dayPackages.sort(Comparator.comparingDouble(
                p -> -p.getWeightForDay(currentDay, membersCount)));

        // для логування
        log.info("\n День " + currentDay + ": " + dayPackages.size() + " пакунків");
        // Логування пакунків поточного дня
        log.info("Пакунки на день " + currentDay + ":");
        for (PackageWithProducts pack : dayPackages) {
            double total = pack.getProductsWeight(); // загальна вага всіх продуктів
            double dayWeight = pack.getPackageDays().stream()
                    .filter(pd -> pd.getDate().equals(currentDay))
                    .mapToDouble(PackageDayProducts::getWeight)
                    .sum();

            log.info("  - {} (загальна={}г; {}={}г)",
                    pack.getFoodPackage().getName(),
                    String.format("%.1f", total),
                    currentDay,
                    String.format("%.1f", dayWeight)
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
            log.info("\nСортування D" + dayIndex + " (" + sortedDates.get(dayIndex) + ") за силою:");
            for (HikerState h : states) {
                log.info("  {} (coeff={})", h.getHiker().getName(), String.format("%.2f", h.getHiker().getWeightCoefficient()));
            }
        } else {
            states.sort(Comparator.comparingDouble(s -> s.getCumulativeLoadFromLastDay(currentDay))); // менше навантажені спочатку

            log.info("\nСортування D" + dayIndex + " (" + currentDay + ") за сумарним навантаженням:");
            for (HikerState h : states) {
                double load = h.getTotalWeightUpTo(currentDay);
                log.info("  {} -> loadUpTo[{}]={} г", h.getHiker().getName(), currentDay, String.format("%.2f", load));
            }
        }

        // розподіляємо всі пакунки поточного дня
        assignPackagesOfDay(currentDay, dayPackages, states, dayIndex);

        // переходимо далі
        branchAndBound(dayIndex + 1, states);
    }

    private void assignPackagesOfDay(LocalDate currentDay,
                                     List<PackageWithProducts> remainingPacks,
                                     List<HikerState> states,
                                     int dayIndex) {

        // якщо всі пакунки поточного дня вже розподілені
        if (remainingPacks.isEmpty()) {
            log.info("Всі пакунки дня {} розподілено.", currentDay);

            // Перевіряємо, що всі туристи мають навантаження не менше 70% (перший день) або 90% (інші)
            for (HikerState h : states) {
                double target = h.getTargetByDay().getOrDefault(currentDay, 0.0);
                double load = h.getWeight(currentDay);
                double tol = 0.10;
                double minAllowed = target * (1 - tol);

                if (load < minAllowed) {
                    log.info("[{}] має занадто мале навантаження на {}: {} < {} ({}%)",
                            h.getHiker().getName(),
                            currentDay,
                            String.format("%.2f", load),
                            String.format("%.2f", minAllowed),
                            String.format("%.0f", (1 - tol) * 100)
                    );
                    return; // день недопустимий, не продовжуємо гілку
                }
            }

            if (dayIndex < sortedDates.size() - 1) {
                // Якщо ще є наступні дні — переходимо далі
                branchAndBound(dayIndex + 1, states);
            } else {
                // === Перевіряємо остаточну допустимість рішення перед збереженням ===
                boolean valid = true;
                for (HikerState h : states) {
                    for (LocalDate day : sortedDates) {
                        Double target = h.getTargetByDay().get(day);
                        if (target == null) continue;
                        double load = h.getWeight(day);
                        double tol = 0.10;
                        double minAllowed = target * (1 - tol);
                        double maxAllowed = target * (1 + tol);

                        if (load < minAllowed || load > maxAllowed) {
                            log.warn("[{}] день {}: вихід за межі допустимого ({} < {} або {} > {})",
                                    h.getHiker().getName(),
                                    day,
                                    String.format("%.1f", load),
                                    String.format("%.1f", minAllowed),
                                    String.format("%.1f", load),
                                    String.format("%.1f", maxAllowed)
                            );
                            valid = false;
                        }
                    }
                }
                if (!valid) {
                    log.warn("Рішення не збережено — перевищено допустимі межі навантаження");
                    return;
                }

                // якщо це останній день — зберігаємо рішення
                double currentDeviation = calculateTotalDeviation(states);
                if (currentDeviation < bestDeviation) {
                    bestDeviation = currentDeviation;
                    bestSolution = states.stream()
                            .map(HikerState::cloneState)
                            .collect(Collectors.toList());
                    log.info("🔹 Нове найкраще рішення: середнє відхилення = {}%", String.format("%.2f", bestDeviation));
                }

                log.info("== РОЗПОДІЛ ПЕРЕД ЗБЕРЕЖЕННЯМ В bestSolution ==");
                for (HikerState h : states) {
                    log.info("Турист: {}", h.getHiker().getName());
                    for (LocalDate day : sortedDates) {
                        String assigned = h.getAssignedByDay()
                                .getOrDefault(day, Set.of()).stream()
                                .map(p -> p.getFoodPackage().getName())
                                .collect(Collectors.joining(", "));
                        log.info("  {} -> {}", day, assigned);
                    }
                }
                log.info("===============================================");
            }
            return;
        }

        // поточний пакунок
        PackageWithProducts pack = remainingPacks.get(0);
        // решта пакунків поточного дня
        List<PackageWithProducts> next = remainingPacks.subList(1, remainingPacks.size());

        // пробуємо призначити цей пакунок кожному туристу
        for (HikerState hiker : states) {
            // Створюємо копію всього списку станів (щоб гілка була незалежною)
            List<HikerState> nextStates = states.stream()
                    .map(HikerState::cloneState)
                    .collect(Collectors.toList());

            // Знаходимо відповідного туриста у копії
            HikerState current = nextStates.stream()
                    .filter(s -> s.getHiker().equals(hiker.getHiker()))
                    .findFirst()
                    .orElseThrow();

            // додаємо пакунок туристу
            double before = current.getWeight(currentDay);
            double added = pack.getWeightForDay(currentDay, membersCount);
            log.debug("Пробуємо дати {} туристу {} (частина на {} = {}г, до додавання мав {}г)",
                    pack.getFoodPackage().getName(),
                    hiker.getHiker().getName(),
                    currentDay,
                    String.format("%.2f", added),
                    String.format("%.2f", before)
            );

            // Додаємо пакунок у копію (а не в оригінал)
            current.addPackage(pack, membersCount);

            double after = current.getWeight(currentDay);
            log.debug("  Після додавання має {} г у день {}", String.format("%.2f", after), currentDay);

            // Перевіряємо допустимість
            if (isFeasible(current, pack, currentDay, nextStates)) {
                log.debug("можна додати {}, пробуємо далі", pack.getFoodPackage().getName());
                // рекурсія з новою копією станів
                assignPackagesOfDay(currentDay, next, nextStates, dayIndex);
            } else {
                log.debug("не можна додати {}, перевищено вагу", pack.getFoodPackage().getName());
            }
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

    // Перевірка меж (чи припустиме поточне рішення). Враховує поточний і всі майбутні дні, коли пакунок використовується.
    private boolean isFeasible(HikerState current, PackageWithProducts pack,
                               LocalDate currentDay, List<HikerState> all) {

        // Отримуємо всі дні, коли пакунок використовується
        List<LocalDate> usageDays = pack.getPackageDays().stream()
                .map(PackageDayProducts::getDate)
                .sorted()
                .collect(Collectors.toList());

        // Для кожного дня, починаючи з поточного і далі
        for (LocalDate day : usageDays) {
            if (day.isBefore(currentDay)) continue; // пропускаємо попередні дні

            // Отримуємо вже розрахований таргет
            Double target = current.getTargetByDay().get(day);
            if (target == null) {
                log.warn("Target for day {} not initialized for {}", day, current.getHiker().getName());
                return false;
            }

            // Поточне навантаження (включно з усіма призначеними пакунками)
            double currentLoad = current.getWeight(day);

            // Вказуємо толеранс
            double tol = 0.1;

            // Визначаємо межі допустимого відхилення
            double maxAllowed = target * (1 + tol);

            // Перевіряємо чи навантаження в межах
            // Якщо розподіл ще триває — перевіряємо тільки верхню межу
            boolean feasible = currentLoad <= maxAllowed;

            log.info(
                    "    [{}] day={} load={} target={} max={} tol={} -> {}",
                    current.getHiker().getName(),
                    day,
                    String.format("%.2f", currentLoad),
                    String.format("%.2f", target),
                    String.format("%.2f", maxAllowed),
                    String.format("%.2f", tol),
                    feasible ? "OK" : "TOO HEAVY"
            );

            if (!feasible) return false;
        }

        return true;
    }

    // Розрахунок загального добового таргету для всієї групи
    private double dailyWeightTarget(LocalDate currentDay) {
        // Сумуємо реальну вагу всіх пакунків на цей день з урахуванням коефіцієнтів і тари
        return packagesByDate.getOrDefault(currentDay, Collections.emptyList())
                .stream()
                .mapToDouble(p -> p.getWeightForDay(currentDay, membersCount))
                .sum();
    }

    // === Розрахунок групових таргетів для кожного дня ===
    private Map<LocalDate, Double> calculateGroupTargets(List<LocalDate> sortedDates) {
        Map<LocalDate, Double> result = new HashMap<>();
        for (LocalDate day : sortedDates) {
            double total = dailyWeightTarget(day);
            result.put(day, total);
        }
        return result;
    }

    // === Розрахунок індивідуальних таргетів для кожного туриста ===
    private void calculateIndividualTargets(FoodPlan plan, List<HikerState> states, Map<LocalDate, Double> groupTargets) {
        double totalCoeff = plan.getMembers().stream()
                .mapToDouble(h -> h.getWeightCoefficient())
                .sum();

        for (HikerState h : states) {
            for (LocalDate day : sortedDates) {
                double groupTarget = groupTargets.getOrDefault(day, 0.0);
                double target = groupTarget * (h.getHiker().getWeightCoefficient() / totalCoeff);
                h.setTargetForDay(day, target);
            }
        }
    }

    // Обчислення середнього відхилення від таргету по всіх туристах і днях
    private double calculateTotalDeviation(List<HikerState> states) {
        double total = 0;
        int count = 0;
        for (HikerState h : states) {
            for (LocalDate day : sortedDates) {
                Double target = h.getTargetByDay().get(day);
                if (target == null || target == 0) continue;
                double load = h.getWeight(day);
                double deviation = Math.abs(load - target) / target;
                total += deviation;
                count++;
            }
        }
        return (count == 0) ? Double.MAX_VALUE : (total / count) * 100.0;
    }
}