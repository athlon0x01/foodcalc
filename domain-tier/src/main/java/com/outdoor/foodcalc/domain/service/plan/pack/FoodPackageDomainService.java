package com.outdoor.foodcalc.domain.service.plan.pack;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.plan.pack.FoodPackage;
import com.outdoor.foodcalc.domain.repository.plan.IFoodPlanRepo;
import com.outdoor.foodcalc.domain.repository.plan.pack.IFoodPackageRepo;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class FoodPackageDomainService {

    private final IFoodPlanRepo planRepo;
    private final IFoodPackageRepo foodPackageRepo;

    public FoodPackageDomainService(IFoodPlanRepo planRepo, IFoodPackageRepo foodPackageRepo) {
        this.planRepo = planRepo;
        this.foodPackageRepo = foodPackageRepo;
    }

    public List<FoodPackage> getPlanPackages(long planId) {
        return foodPackageRepo.getPlanPackages(planId);
    }

    public FoodPackage addPackage(long planId, FoodPackage foodPackage) {
        long id = foodPackageRepo.addPackage(planId, foodPackage);
        planRepo.saveLastUpdated(planId, ZonedDateTime.now());
        return foodPackage.toBuilder()
                .id(id)
                .build();
    }

    public void updatePackage(long planId, FoodPackage foodPackage) {
        if (!foodPackageRepo.existsPackage(foodPackage.getId())) {
            throw new NotFoundException("Package with id=" + foodPackage.getId() + " doesn't exist");
        }
        if (!foodPackageRepo.updatePackage(foodPackage)) {
            throw new FoodcalcDomainException("Failed to update package with id=" + foodPackage.getId());
        }
        planRepo.saveLastUpdated(planId, ZonedDateTime.now());
    }

    public void deletePackage(long planId, long id) {
        if (!foodPackageRepo.existsPackage(id)) {
            throw new NotFoundException("Package with id=" + id + " doesn't exist");
        }
        foodPackageRepo.deletePackage(id);
        planRepo.saveLastUpdated(planId, ZonedDateTime.now());
    }
}
