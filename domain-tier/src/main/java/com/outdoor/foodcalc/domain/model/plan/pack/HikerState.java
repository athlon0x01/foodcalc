package com.outdoor.foodcalc.domain.model.plan.pack;

import com.outdoor.foodcalc.domain.model.plan.Hiker;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Jacksonized
@Builder(toBuilder = true)
public class HikerState {

    @EqualsAndHashCode.Include
    private final Hiker hiker;

    // Вага туриста за кожен день
    @Builder.Default
    private final Map<LocalDate, Double> weightByDay = new HashMap<>();

    // Призначені пакунки
    @Builder.Default
    private final Set<PackageWithProducts> assignedPackages = new HashSet<>();

    // Цільова вага туриста (target) за кожен день
    @Builder.Default
    private final Map<LocalDate, Double> targetByDay = new HashMap<>();

    public HikerState(Hiker hiker) {
        this.hiker = hiker;
    }

    // Отримати поточну вагу для конкретного дня
    public double getWeight(LocalDate day) {
        return weightByDay.getOrDefault(day, 0.0);
    }

    // Додати пакунок на всі дні
    public void addPackage(PackageWithProducts pack) {
        // додаємо всі дні пакунку одразу
        for (PackageDayProducts pd : pack.getPackageDays()) {
            double addWeight = pd.getWeight();
            weightByDay.merge(pd.getDate(), addWeight, Double::sum);
        }
        assignedPackages.add(pack);
    }

    // Прибрати пакунок при відкаті (backtrack)
    public void removePackage(PackageWithProducts pack) {
        // знімаємо всі дні пакунку
        for (PackageDayProducts pd : pack.getPackageDays()) {
            double removeWeight = pd.getWeight();
            weightByDay.merge(pd.getDate(), -removeWeight, Double::sum);
            if (weightByDay.get(pd.getDate()) <= 0.0)
                weightByDay.remove(pd.getDate());
        }
        assignedPackages.remove(pack);
    }

    // Сумарна вага для всіх днів
    public double totalWeight() {
        return weightByDay.values().stream().mapToDouble(Double::doubleValue).sum();
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
        return weightByDay.entrySet().stream()
                .filter(e -> !e.getKey().isAfter(day))
                .mapToDouble(Map.Entry::getValue)
                .sum();
    }

    // Клонування стану для нової гілки BnB
    public HikerState cloneState() {
        HikerState clone = new HikerState(this.hiker);
        clone.weightByDay.putAll(this.weightByDay);
        clone.targetByDay.putAll(this.targetByDay);
        clone.assignedPackages.addAll(this.assignedPackages);
        return clone;
    }
}
