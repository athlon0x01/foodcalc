package com.outdoor.foodcalc.service.distributionGenetic;

import com.outdoor.foodcalc.domain.exception.FoodcalcException;
import com.outdoor.foodcalc.domain.model.plan.FoodPlan;
import com.outdoor.foodcalc.domain.model.plan.pack.HikerState;
import com.outdoor.foodcalc.domain.model.plan.pack.PackageDayProducts;
import com.outdoor.foodcalc.domain.model.plan.pack.PackageWithProducts;
import com.outdoor.foodcalc.domain.service.plan.FoodPackageDomainService;
import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.Factory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GeneticDistributionService {

    private final FoodPackageDomainService foodPackageDomainService;

    @Getter
    private List<LocalDate> sortedDates;
    private Map<LocalDate, List<PackageWithProducts>> packagesByDate;
    private int membersCount;

    public GeneticDistributionService(FoodPackageDomainService foodPackageDomainService) {
        this.foodPackageDomainService = foodPackageDomainService;
    }

    // Підготовка даних
    private void prepareData(List<PackageWithProducts> packages) {
        packagesByDate = new HashMap<>();
        for (PackageWithProducts pack : packages) {
            for (PackageDayProducts pd : pack.getPackageDays()) {
                packagesByDate
                        .computeIfAbsent(pd.getDate(), k -> new ArrayList<>())
                        .add(pack);
            }
        }
        sortedDates = new ArrayList<>(packagesByDate.keySet());
        sortedDates.sort(Comparator.reverseOrder());
    }

    // Основний метод
    public List<HikerState> findBestDistribution(FoodPlan plan, List<PackageWithProducts> packages) {
        prepareData(packages);
        membersCount = plan.getMembers().size();

        // Генотип: для кожного пакунка вказуємо індекс туриста
        Factory<Genotype<IntegerGene>> genotypeFactory =
                Genotype.of(IntegerChromosome.of(0, membersCount - 1, packages.size()));

        // Еволюційний двигун
        Engine<IntegerGene, Double> engine = Engine
                .builder(gt -> evaluateFitness(gt, plan, packages), genotypeFactory)
                .populationSize(400)
                .optimize(Optimize.MAXIMUM)
                .alterers(
                        new Mutator<>(0.25),                 // було 0.2
                        new SwapMutator<>(0.05),             // ← NEW: інколи добре міняє місцями гени
                        new UniformCrossover<>(0.6)          // було 0.5
                )
                .offspringFraction(0.7)                  // ← NEW: більше нащадків
                .survivorsSelector(new EliteSelector<>(3))// ← NEW: зберігаємо 3 кращих у поколінні
                .build();

        // Запуск еволюції
        Phenotype<IntegerGene, Double> best = engine.stream()
                .limit(320)
                .collect(EvolutionResult.toBestPhenotype());

        log.info("GA завершено, fitness = {}", best.fitness());

        return buildResult(best.genotype(), plan, packages);
    }

    //  Оцінка пристосованості: штраф за >10%
    private double evaluateFitness(Genotype<IntegerGene> gt,
                                   FoodPlan plan,
                                   List<PackageWithProducts> packages) {
        final int days = sortedDates.size();
        final int hikers = membersCount;

        // date -> day index
        Map<LocalDate, Integer> dayIndex = new HashMap<>(days);
        for (int d = 0; d < days; d++) dayIndex.put(sortedDates.get(d), d);

        // Фактично призначені ваги на кожен день для кожного туриста
        double[][] load = new double[hikers][days];

        Chromosome<IntegerGene> chromosome = gt.chromosome();
        for (int i = 0; i < chromosome.length(); i++) {
            int hIdx = chromosome.get(i).allele();
            PackageWithProducts pack = packages.get(i);
            for (PackageDayProducts pd : pack.getPackageDays()) {
                Integer dIdx = dayIndex.get(pd.getDate());
                if (dIdx == null) continue;
                load[hIdx][dIdx] += pack.getWeightForDay(pd.getDate(), membersCount);
            }
        }

        // Групова вага на день
        double[] groupPerDay = new double[days];
        for (int d = 0; d < days; d++) {
            double s = 0.0;
            for (int h = 0; h < hikers; h++) s += load[h][d];
            groupPerDay[d] = s;
        }

        // Персональні таргети на день
        double totalCoef = plan.getMembers().stream()
                .mapToDouble(m -> m.getWeightCoefficient())
                .sum();

        double[][] target = new double[hikers][days];
        for (int h = 0; h < hikers; h++) {
            double share = (totalCoef == 0.0) ? 0.0 : plan.getMembers().get(h).getWeightCoefficient() / totalCoef;
            for (int d = 0; d < days; d++) target[h][d] = groupPerDay[d] * share;
        }

        // Штрафи
        double penaltyOutsideDaily = 0.0; // вихід за ±10% у будь-який день
        double penaltyInsideDaily  = 0.0; // легкий штраф всередині допуску
        double penaltyTripTotals   = 0.0; // відхилення сумарної ваги туриста від його сумарного таргету

        final double DAILY_TOL = 0.10;  // ±10% на день
        final double TRIP_TOL  = 0.05;  // ±5% на підсумок по туристу

        // Денний штраф
        for (int d = 0; d < days; d++) {
            for (int h = 0; h < hikers; h++) {
                double t = target[h][d];
                if (t <= 1e-9) continue;

                double rel  = (load[h][d] - t) / t;     // відносне відхилення
                double over = Math.abs(rel) - DAILY_TOL;

                if (over > 0) {
                    // Сильний штраф за вихід за ±10% (кубічний)
                    penaltyOutsideDaily += Math.pow(over, 3);
                } else {
                    // Усередині допуску — легке “притиснення” до таргету
                    double inside = Math.abs(rel) / DAILY_TOL;
                    penaltyInsideDaily += 0.1 * inside * inside;
                }
            }
        }

        // Підсумковий штраф по туристу
        for (int h = 0; h < hikers; h++) {
            double totLoad = 0.0, totTarget = 0.0;
            for (int d = 0; d < days; d++) {
                totLoad   += load[h][d];
                totTarget += target[h][d];
            }
            if (totTarget > 1e-9) {
                double relTot = (totLoad - totTarget) / totTarget;
                double overTot = Math.abs(relTot) - TRIP_TOL;
                if (overTot > 0) {
                    // квадратний штраф достатньо агресивний
                    penaltyTripTotals += overTot * overTot;
                }
            }
        }

        // Ваги: щоденний вихід за межі — найсильніший; всередині — м’яко; підсумок по туристу — середньо-сильний
        final double A = 200.0;  // поза ±10%/день
        final double B = 0.4;    // усередині ±10%/день
        final double D = 150.0;  // підсумок по туристу (±5%)

        double cost = A * penaltyOutsideDaily + B * penaltyInsideDaily + D * penaltyTripTotals;

        return 1.0 / (1.0 + cost);
    }

    //  перетворення у HikerState
    private List<HikerState> buildResult(Genotype<IntegerGene> gt,
                                         FoodPlan plan,
                                         List<PackageWithProducts> packages) {

        List<HikerState> result = plan.getMembers().stream()
                .map(HikerState::new)
                .collect(Collectors.toList());

        // Додаємо розрахунок Target For Day (з урахуванням коефіцієнтів туристів)
        Map<LocalDate, Double> totalPerDay = calculateDailyTargets(packages, membersCount);

        // Знаходимо суму коефіцієнтів усіх туристів
        double totalCoef = plan.getMembers().stream()
                .mapToDouble(h -> h.getWeightCoefficient())
                .sum();

        // Для кожного туриста розраховуємо індивідуальний target на кожен день
        for (HikerState hiker : result) {
            double coef = hiker.getHiker().getWeightCoefficient();
            for (LocalDate date : sortedDates) {
                double groupTarget = totalPerDay.getOrDefault(date, 0.0);
                double personalTarget = groupTarget * (coef / totalCoef);
                hiker.setTargetForDay(date, personalTarget);
            }
        }

        Chromosome<IntegerGene> chromosome = gt.chromosome();
        for (int i = 0; i < chromosome.length(); i++) {
            int hikerIndex = chromosome.get(i).allele();
            PackageWithProducts pack = packages.get(i);

            HikerState hiker = result.get(hikerIndex);
            hiker.addPackage(pack, membersCount);
        }

        log.info("=== ФІНАЛЬНИЙ РОЗПОДІЛ (GA) ===");
        for (HikerState hiker : result) {
            log.info("{}: {} пакунків", hiker.getHiker().getName(), hiker.getAssignedPackages().size());
        }

        if (result.isEmpty()) {
            throw new FoodcalcException("Не вдалося знайти допустимий розподіл (Genetic Algorithm)");
        }

        return result;
    }

    // метод для ExcelExportGeneticService
    public List<PackageWithProducts> getPackagesWithProductsForPlan(long planId, int members) {
        var packages = foodPackageDomainService.getPackagesWithProductsForPlan(planId);
        return packages.values().stream()
                .sorted(Collections.reverseOrder(
                        Comparator.comparingDouble(pack -> pack.getEstimatedWeight(members))))
                .collect(Collectors.toList());
    }

    // Розрахунок загального добового таргету для всієї групи
    private Map<LocalDate, Double> calculateDailyTargets(List<PackageWithProducts> packages, int membersCount) {
        Map<LocalDate, Double> dailyTargets = new HashMap<>();

        for (PackageWithProducts pack : packages) {
            for (PackageDayProducts pd : pack.getPackageDays()) {
                // Використовуємо саме вагу пакунка на цей день
                double dayWeight = pack.getWeightForDay(pd.getDate(), membersCount);
                dailyTargets.merge(pd.getDate(), dayWeight, Double::sum);
            }
        }

        return dailyTargets;
    }
}