package com.outdoor.foodcalc.domain.model.plan.pack;

import com.outdoor.foodcalc.domain.model.product.ProductRef;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Jacksonized
@Builder(toBuilder = true)
public class PackageDayProducts {

    @EqualsAndHashCode.Include
    private final long dayId;
    private final LocalDate date;
    @Builder.Default
    private final List<ProductRef> products = new ArrayList<>();

    public double getWeight() {
        return products.stream()
                .mapToDouble(ProductRef::getWeight)
                .sum();
    }
}
