package com.outdoor.foodcalc.domain.repository.plan.pack;

import com.outdoor.foodcalc.domain.model.plan.pack.FoodPackage;

import java.util.List;

public interface IFoodPackageRepo {

    List<FoodPackage> getPlanPackages(long planId);

    long addPackage(long planId, FoodPackage foodPackage);

    boolean updatePackage(FoodPackage foodPackage);

    void deletePackage(long id);

    boolean existsPackage(long id);
}
