package com.outdoor.foodcalc.domain.model.plan.pack;

import com.outdoor.foodcalc.domain.model.product.ProductRef;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Jacksonized
@Builder(toBuilder = true)
public class PackageWithProducts {

    @EqualsAndHashCode.Include
    private final FoodPackage foodPackage;
    private final Map<Long, PackageDayProducts> dayProducts; // ключ- dayId, значення - частина пакету на цей день

    // дні використання пакунку
    public Collection<PackageDayProducts> getPackageDays() {
        return Optional.ofNullable(dayProducts)
                .map(Map::values)
                .orElse(Collections.emptyList());
    }

    public List<ProductRef> getAllProducts() {
        return getPackageDays().stream()
                .flatMap(value -> value.getProducts().stream())
                .collect(Collectors.toList());
    }

    // обчислюємо вагу всіх продуктів  пакунку
    public double getProductsWeight() {
        return getAllProducts().stream()
                .mapToDouble(ProductRef::getWeight)
                .sum();
    }

    // обчислюємо
    public double getEstimatedWeight(Set<Long> days, int members) {
        double weight = getPackageDays().stream()
                .filter(dayPackages -> days.contains(dayPackages.getDayId()))
                .flatMap(value -> value.getProducts().stream())
                .mapToDouble(ProductRef::getWeight)
                .sum();
        return weight * members * foodPackage.getVolumeCoefficient() + foodPackage.getAdditionalWeight();
    }

    // обчислюємо
    public double getEstimatedWeight(int members) {
        double weight = getProductsWeight() * members * foodPackage.getVolumeCoefficient();
        return weight + foodPackage.getAdditionalWeight();
    }

    // повертаємо останній день використання пакунку
    public LocalDate getLastUsageDay() {
        return getPackageDays().stream()
                .map(PackageDayProducts::getDate)
                .max(LocalDate::compareTo)
                .orElse(null);
    }

    /** Вага цього пакунку, яку реально несе хайкер у конкретний день:
     *  сума продуктів за день * members * volumeCoeff + (additionalWeight лише в останній день)
     */
    public double getWeightForDay(LocalDate day, int members) {
        // 1) вага продуктів за один день
        double dayProductsWeight = getPackageDays().stream()
                .filter(pd -> pd.getDate().equals(day))
                .mapToDouble(PackageDayProducts::getWeight) // у грамах
                .sum();

        // 2) масштабуємо під кількість учасників та коефіцієнт об'єму
        double scaled = dayProductsWeight * members * foodPackage.getVolumeCoefficient();

        // 3) тара тільки в останній день
        LocalDate last = getLastUsageDay();
        if (last != null && last.equals(day)) {
            scaled += foodPackage.getAdditionalWeight();
        }

        return scaled; // грами
    }

    @Override
    public String toString() {
        // Формуємо компактне представлення назв продуктів по днях
        String daysInfo = getPackageDays().stream()
                .map(day -> {
                    String productNames = day.getProducts().stream()
                            .map(ProductRef::getName)
                            .collect(Collectors.joining(", "));
                    return "[dayId=" + day.getDayId() +
                            ", date=" + day.getDate() +
                            ", products=[" + productNames + "]]";
                })
                .collect(Collectors.joining(", "));

        return "[foodPackage=" + (foodPackage != null ? foodPackage.getName() : "null") +
                ", totalWeight=" + getProductsWeight() +
                ", days=" + getPackageDays().size() +
                ", dayProducts=" + daysInfo + "]";
    }
}
