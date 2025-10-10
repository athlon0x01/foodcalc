package com.outdoor.foodcalc.domain.model.plan.pack;

import com.outdoor.foodcalc.domain.model.plan.Hiker;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Jacksonized
@NoArgsConstructor(force = true)
public class HikerState {

    @EqualsAndHashCode.Include
    private final Hiker hiker;

    // Вага туриста за кожен день
    private final Map<LocalDate, Double> hikerLoadByDay = new HashMap<>();

    // Призначені пакунки
    private final Set<PackageWithProducts> assignedPackages = new HashSet<>();

    // Цільова вага туриста (target) за кожен день
    private final Map<LocalDate, Double> targetByDay = new HashMap<>();

    public HikerState(Hiker hiker) {
        this.hiker = hiker;
    }

    // Отримати поточну вагу для конкретного дня
    public double getWeight(LocalDate day) {
        return hikerLoadByDay.getOrDefault(day, 0.0);
    }

    // Додати пакунок на всі дні
    public void addPackage(PackageWithProducts pack, int members) {
        // додаємо вагу на КОЖЕН день пакунку, вже зі всіма коефіцієнтами
        for (PackageDayProducts pd : pack.getPackageDays()) {
            double addWeight = pack.getWeightForDay(pd.getDate(), members);
            hikerLoadByDay.merge(pd.getDate(), addWeight, Double::sum);
        }
        assignedPackages.add(pack);
    }

    // Прибрати пакунок при відкаті (backtrack)
    public void removePackage(PackageWithProducts pack, int members) {
        // знімаємо вагу на КОЖЕН день пакунку
        for (PackageDayProducts pd : pack.getPackageDays()) {
            double removeWeight = pack.getWeightForDay(pd.getDate(), members);
            hikerLoadByDay.merge(pd.getDate(), -removeWeight, Double::sum);
            if (hikerLoadByDay.get(pd.getDate()) != null && hikerLoadByDay.get(pd.getDate()) <= 0.0)
                hikerLoadByDay.remove(pd.getDate());
        }
        assignedPackages.remove(pack);
    }

    // Сумарна вага для всіх днів
    public double totalWeight() {
        return hikerLoadByDay.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    // Задати цільову вагу (target) для конкретного дня
    public void setTargetForDay(LocalDate day, double target) {
        targetByDay.put(day, target);
    }

    // Отримати цільову вагу (target) для дня
    public double getTargetForDay(LocalDate day) {
        return targetByDay.getOrDefault(day, 0.0);
    }

    // Скільки загальної ваги турист уже ніс (або несе) до певного дня включно
    public double getTotalWeightUpTo(LocalDate day) {
        return hikerLoadByDay.entrySet().stream()
                .filter(e -> !e.getKey().isAfter(day))
                .mapToDouble(Map.Entry::getValue)
                .sum();
    }

    // Клонування стану для нової гілки BnB
    public HikerState cloneState() {
        HikerState clone = new HikerState(this.hiker);
        clone.hikerLoadByDay.putAll(this.hikerLoadByDay);
        clone.targetByDay.putAll(this.targetByDay);
        clone.assignedPackages.addAll(this.assignedPackages);
        return clone;
    }

    @Override
    public String toString() {
        String weights = hikerLoadByDay.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + String.format("%.1f", e.getValue()))
                .collect(Collectors.joining(", "));

        String targets = targetByDay.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + String.format("%.1f", e.getValue()))
                .collect(Collectors.joining(", "));

        String packagesInfo = assignedPackages.stream()
                .map(p -> p.getFoodPackage() != null ? p.getFoodPackage().getName() : "null")
                .collect(Collectors.joining(", "));

        return "[hiker=" + (hiker != null ? hiker.getName() : "null") +
                ", totalWeight=" + String.format("%.1f", totalWeight()) +
                ", weightByDay={" + weights + "}" +
                ", targetByDay={" + targets + "}" +
                ", assignedPackages=[" + packagesInfo + "]" +
                "]";
    }
}
