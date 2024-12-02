package com.outdoor.foodcalc.domain.repository.plan.pack;

import com.outdoor.foodcalc.domain.model.plan.pack.PackageDayProducts;

import java.util.Map;

public interface IFoodPackageProductsRepo {

    Map<Long, Map<Long, PackageDayProducts>> getPackageProductsForPlan(long planId);
}
