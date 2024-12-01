package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.model.plan.pack.FoodPackage;
import com.outdoor.foodcalc.domain.service.plan.pack.FoodPackageDomainService;
import com.outdoor.foodcalc.model.plan.pack.FoodPackageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class FoodPackageService {

    private final FoodPackageDomainService foodPackageDomainService;

    public FoodPackageService(FoodPackageDomainService foodPackageDomainService) {
        this.foodPackageDomainService = foodPackageDomainService;
    }

    public List<FoodPackageInfo> getPlanPackages(long planId) {
        return foodPackageDomainService.getPlanPackages(planId).stream()
                .map(this::mapInfo)
                .collect(Collectors.toList());
    }

    public Map<Long, String> getPlanPackagesNames(long planId) {
        return foodPackageDomainService.getPlanPackages(planId).stream()
                .collect(Collectors.toMap(FoodPackage::getId, FoodPackage::getName));
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void deletePackage(long planId, long id) {
        foodPackageDomainService.deletePackage(planId, id);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public FoodPackageInfo addPackage(long planId, FoodPackageInfo packageInfo) {
        FoodPackage foodPackage = FoodPackage.builder()
                .name(packageInfo.getName())
                .volumeCoefficient(packageInfo.getVolumeCoefficient())
                .additionalWeight(Math.round(packageInfo.getAdditionalWeight() * 10))
                .build();
        return mapInfo(foodPackageDomainService.addPackage(planId, foodPackage));
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void updatePackage(long planId, FoodPackageInfo packageInfo) {
        FoodPackage foodPackage = FoodPackage.builder()
                .id(packageInfo.getId())
                .name(packageInfo.getName())
                .description(packageInfo.getDescription())
                .volumeCoefficient(packageInfo.getVolumeCoefficient())
                .additionalWeight(Math.round(packageInfo.getAdditionalWeight() * 10))
                .build();
        foodPackageDomainService.updatePackage(planId, foodPackage);
    }

    public FoodPackageInfo mapInfo(FoodPackage foodPackage) {
        return FoodPackageInfo.builder()
                .id(foodPackage.getId())
                .name(foodPackage.getName())
                .description(foodPackage.getDescription())
                .volumeCoefficient(foodPackage.getVolumeCoefficient())
                .additionalWeight(foodPackage.getAdditionalWeight())
                .build();
    }
}
