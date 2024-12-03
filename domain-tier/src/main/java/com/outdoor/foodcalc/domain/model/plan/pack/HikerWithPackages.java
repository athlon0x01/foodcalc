package com.outdoor.foodcalc.domain.model.plan.pack;

import com.outdoor.foodcalc.domain.model.plan.Hiker;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Jacksonized
@Builder(toBuilder = true)
public class HikerWithPackages {
    @EqualsAndHashCode.Include
    private final Hiker hiker;
    @EqualsAndHashCode.Include
    @Builder.Default
    private Set<PackageWithProducts> packages = new TreeSet<>(Comparator.comparingDouble(PackageWithProducts::getProductsWeight));

    public double calculateEstimatedWeight(Set<Long> days, int members) {
        return packages.stream()
                .mapToDouble(pack -> pack.getEstimatedWeight(days, members))
                .sum();
    }
}
