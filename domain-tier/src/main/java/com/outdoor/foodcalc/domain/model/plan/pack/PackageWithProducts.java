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
    private final Map<Long, PackageDayProducts> dayProducts;

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

    public double getProductsWeight() {
        return getAllProducts().stream()
                .mapToDouble(ProductRef::getWeight)
                .sum();
    }

    public double getEstimatedWeight(int members) {
        double weight = getProductsWeight() * members * foodPackage.getVolumeCoefficient();
        return weight + foodPackage.getAdditionalWeight();
    }
}
