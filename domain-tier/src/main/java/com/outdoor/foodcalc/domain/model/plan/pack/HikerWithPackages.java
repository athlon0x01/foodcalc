package com.outdoor.foodcalc.domain.model.plan.pack;

import com.outdoor.foodcalc.domain.model.plan.Hiker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Jacksonized
@AllArgsConstructor
@Builder(toBuilder = true)
public class HikerWithPackages {
    @EqualsAndHashCode.Include
    private final Hiker hiker;
    @EqualsAndHashCode.Include
    private Set<PackageWithProducts> packages;

    public double calculateEstimatedWeight(Set<Long> days, int members) {
        return packages.stream()
                .mapToDouble(pack -> pack.getEstimatedWeight(days, members))
                .sum();
    }

    @Override
    public String toString() {
        String packageNames = packages != null
                ? packages.stream()
                .map(p -> p.getFoodPackage() != null ? p.getFoodPackage().getName() : "null")
                .collect(Collectors.joining(", "))
                : "";

        return "[hiker=" + (hiker != null ? hiker.getName() : "null") +
                ", packagesCount=" + (packages != null ? packages.size() : 0) +
                ", packages=[" + packageNames + "]" +
                "]";
    }
}
