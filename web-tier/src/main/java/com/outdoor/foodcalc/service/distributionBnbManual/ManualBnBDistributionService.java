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
    public List<HikerState> findBestDistribution(FoodPlan plan, List<PackageWithProducts> packages) {
        prepareData(packages);
        log.info("Днів у плані: {}", sortedDates.size());
        sortedDates.forEach(d -> log.info("  {} -> {} пакунків", d, packagesByDate.get(d).size()));

        this.membersCount = plan.getMembers().size();

        List<HikerState> states = plan.getMembers().stream()
                .map(HikerState::new)
                .collect(Collectors.toList());

        branchAndBound(0, states);

        if (bestSolution == null) {
            log.error("Не знайдено допустимий розподіл (foundSolution={})", foundSolution);
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
        sortedDates.sort(Comparator.reverseOrder()); // D0 → D1 → D2...
    }

    // Рекурсивний обхід дерева рішень (Branch and Bound)
    private void branchAndBound(int dayIndex, List<HikerState> states) {

        if (foundSolution) return; // можна зупинитися при першому валідному рішенні

        // базовий випадок: усі дні розподілені
        if (dayIndex >= sortedDates.size()) {
            foundSolution = true;
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
            states.sort(Comparator.comparingDouble(s -> s.getTotalWeightUpTo(currentDay))); // менше навантажені спочатку

            log.info("\nСортування D" + dayIndex + " (" + currentDay + ") за сумарним навантаженням:");
            for (HikerState h : states) {
                double load = h.getTotalWeightUpTo(currentDay);
                log.info("  {} -> loadUpTo[{}]={} г", h.getHiker().getName(), currentDay, String.format("%.2f", load));
            }
        }

        // розподіляємо всі пакунки поточного дня
        assignPackagesOfDay(currentDay, dayPackages, states, dayIndex);

        // переходимо далі лише якщо рішення ще не знайдене
        if (!foundSolution) {
            branchAndBound(dayIndex + 1, states);
        }
    }

    private void assignPackagesOfDay(LocalDate currentDay,
                                     List<PackageWithProducts> remainingPacks,
                                     List<HikerState> states,
                                     int dayIndex) {
        if (foundSolution) return; // якщо вже знайшли рішення — далі не перебираємо

        // якщо всі пакунки поточного дня вже розподілені
        if (remainingPacks.isEmpty()) {
            log.info("Всі пакунки дня {} розподілено.", currentDay);

            // якщо ще є наступні дні — переходимо далі
//            if (dayIndex < sortedDates.size() - 1) {
//                LocalDate nextDay = sortedDates.get(dayIndex + 1);
//                List<PackageWithProducts> nextDayPacks = packagesByDate.getOrDefault(nextDay, List.of());
//                assignPackagesOfDay(nextDay, nextDayPacks, states, dayIndex + 1);
//                return;
//            }
            if (dayIndex < sortedDates.size() - 1) {
                branchAndBound(dayIndex + 1, states);
                return;
            }


            // якщо це останній день — зберігаємо рішення
            foundSolution = true;
            bestSolution = states.stream()
                    .map(HikerState::cloneState)
                    .collect(Collectors.toList());

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
            return;
        }

        // поточний пакунок
        PackageWithProducts pack = remainingPacks.get(0);
        // решта пакунків поточного дня
        List<PackageWithProducts> next = remainingPacks.subList(1, remainingPacks.size());

        double tolerance = (dayIndex == 0) ? 0.3 : 0.1;

        // пробуємо призначити цей пакунок кожному туристу
        for (HikerState hiker : states) {
            if (foundSolution) return;

            // видаляємо "removePackage" у кінці — тепер не треба відкочувати
            // виправляємо логіку, щоб додавання робилося у копії (не в оригіналі)

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
            if (isFeasible(current, currentDay, nextStates, tolerance)) {
                log.debug("можна додати {}, пробуємо далі", pack.getFoodPackage().getName());
                // рекурсія з новою копією станів
                assignPackagesOfDay(currentDay, next, nextStates, dayIndex);
            } else {
                log.debug("не можна додати {}, перевищено вагу", pack.getFoodPackage().getName());
            }

            if (foundSolution) return; // вихід, якщо знайдено рішення
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
        // Розраховуємо цільову вагу для туриста на цей день
        double target = dailyTargetOfHiker(current, day, all);
        current.setTargetForDay(day, target);

        // Поточне навантаження туриста за цей день
        double currentLoad = current.getWeight(day);

        // Дозволене максимальне навантаження з урахуванням толерансу
        double allowed = target * (1 + tolerance);

        // Перевіряємо, чи не перевищено межу
        boolean feasible = currentLoad <= allowed;

        log.info(
                "    [{}] day={} load={} target={} allowed={} tol={} -> {}",
                current.getHiker().getName(),
                day,
                String.format("%.2f", currentLoad),
                String.format("%.2f", target),
                String.format("%.2f", allowed),
                String.format("%.2f", tolerance),
                feasible ? "OK" : "TOO HEAVY"
        );

        return feasible;
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

        log.info(
                "   [TargetCalc] Hiker={} prev={} totalWeightThisDay={} coeff={} members={} -> target={}",
                hikerState.getHiker().getName(),
                String.format("%.2f", previousTarget),
                String.format("%.2f", totalWeightThisDay),
                String.format("%.2f", hikerState.getHiker().getWeightCoefficient()),
                totalHikers,
                String.format("%.2f", previousTarget + (totalWeightThisDay * hikerState.getHiker().getWeightCoefficient() / totalHikers))
        );

        // Формула цільової ваги для хайкера на поточний день
        return previousTarget + (totalWeightThisDay * hikerState.getHiker().getWeightCoefficient() / totalHikers);
    }
}