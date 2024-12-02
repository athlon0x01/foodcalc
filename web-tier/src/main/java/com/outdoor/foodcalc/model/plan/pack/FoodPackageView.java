package com.outdoor.foodcalc.model.plan.pack;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Jacksonized
@Builder
public class FoodPackageView {
    private FoodPackageInfo foodPackage;
    private List<DayProductsView> dayProducts;
    private double weight;
    private double estimatedWeight;
}
