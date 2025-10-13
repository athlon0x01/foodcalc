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

    // ========= ПІДГОТОВКА ДАНИХ =========
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

    // ========= ОСНОВНИЙ МЕТОД =========
    public List<HikerState> findBestDistribution(FoodPlan plan, List<PackageWithProducts> packages) {
        prepareData(packages);
        membersCount = plan.getMembers().size();

        log.info("Початок генетичного розподілу для {} туристів і {} пакунків", membersCount, packages.size());

        // --- 1. Генотип: для кожного пакунка вказуємо індекс туриста ---
        Factory<Genotype<IntegerGene>> genotypeFactory =
                Genotype.of(IntegerChromosome.of(0, membersCount - 1, packages.size()));

        // --- 2. Еволюційний двигун ---
        Engine<IntegerGene, Double> engine = Engine
                .builder(gt -> evaluateFitness(gt, plan, packages), genotypeFactory)
                .populationSize(250)
                .optimize(Optimize.MAXIMUM)
                .alterers(
                        new Mutator<>(0.2),
                        new UniformCrossover<>(0.5)
                )
                .build();

        // --- 3. Запуск еволюції ---
        Phenotype<IntegerGene, Double> best = engine.stream()
                .limit(150)
                .collect(EvolutionResult.toBestPhenotype());

        log.info("GA завершено, fitness = {}", best.fitness());

        // --- 4. Перетворюємо рішення у HikerState ---
        return buildResult(best.genotype(), plan, packages);
    }

    // ========= ОЦІНКА ПРИСТОСОВАНОСТІ =========
    private double evaluateFitness(Genotype<IntegerGene> gt, FoodPlan plan, List<PackageWithProducts> packages) {
        Chromosome<IntegerGene> chromosome = gt.chromosome();
        int n = chromosome.length();

        // 1. Обчислюємо сумарну вагу для кожного туриста
        double[] hikerWeights = new double[membersCount];
        for (int i = 0; i < n; i++) {
            int hikerIndex = chromosome.get(i).allele();
            PackageWithProducts pack = packages.get(i);
            double totalWeight = pack.getProductsWeight();
            hikerWeights[hikerIndex] += totalWeight;
        }

        // 2. Обчислюємо середню вагу та стандартне відхилення
        double mean = Arrays.stream(hikerWeights).average().orElse(0);
        double variance = 0;
        for (double w : hikerWeights) {
            variance += Math.pow(w - mean, 2);
        }
        double stdev = Math.sqrt(variance / membersCount);

        // 3. Обчислюємо коефіцієнт коригування за перевищення (штраф)
        double penalty = 0.0;
        for (int i = 0; i < membersCount; i++) {
            double coef = plan.getMembers().get(i).getWeightCoefficient();
            double allowed = mean * coef * 1.1; // 10% допуск
            if (hikerWeights[i] > allowed) {
                penalty += (hikerWeights[i] - allowed);
            }
        }

        // 4. Чим менше стандартне відхилення і штраф — тим краще
        double fitness = 1.0 / (1.0 + stdev + penalty / 1000.0);

        return fitness;
    }

    // ========= ПЕРЕТВОРЕННЯ У HikerState =========
    private List<HikerState> buildResult(Genotype<IntegerGene> gt,
                                         FoodPlan plan,
                                         List<PackageWithProducts> packages) {

        List<HikerState> result = plan.getMembers().stream()
                .map(HikerState::new)
                .collect(Collectors.toList());

        // --- Додаємо розрахунок Target For Day (з урахуванням коефіцієнтів туристів) ---
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

    // ========= МЕТОД ДЛЯ ExcelExportGeneticService =========
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