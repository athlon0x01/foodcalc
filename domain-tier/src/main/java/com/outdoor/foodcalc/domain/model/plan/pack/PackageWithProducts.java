package com.outdoor.foodcalc.domain.model.plan.pack;

import com.outdoor.foodcalc.domain.model.product.ProductRef;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;

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
}
