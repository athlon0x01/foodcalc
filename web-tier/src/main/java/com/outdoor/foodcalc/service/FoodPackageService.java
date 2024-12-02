package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.model.plan.pack.FoodPackage;
import com.outdoor.foodcalc.domain.model.plan.pack.PackageDayProducts;
import com.outdoor.foodcalc.domain.model.plan.pack.PackageWithProducts;
import com.outdoor.foodcalc.domain.service.plan.pack.FoodPackageDomainService;
import com.outdoor.foodcalc.model.plan.HikerInfo;
import com.outdoor.foodcalc.model.plan.pack.DayProductsView;
import com.outdoor.foodcalc.model.plan.pack.FoodPackageInfo;
import com.outdoor.foodcalc.model.plan.pack.FoodPackageView;
import com.outdoor.foodcalc.model.plan.pack.PlanWithPackagesView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class FoodPackageService {

    private final FoodPackageDomainService foodPackageDomainService;
    private final HikerService hikerService;
    private final ProductService productService;

    public FoodPackageService(FoodPackageDomainService foodPackageDomainService, HikerService hikerService, ProductService productService) {
        this.foodPackageDomainService = foodPackageDomainService;
        this.hikerService = hikerService;
        this.productService = productService;
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

    DayProductsView mapDayView(PackageDayProducts dayProducts) {
        return DayProductsView.builder()
                .dayId(dayProducts.getDayId())
                .date(dayProducts.getDate())
                .products(dayProducts.getProducts().stream()
                        .map(productService::mapProductRef)
                        .collect(Collectors.toList()))
                .build();
    }

    FoodPackageView mapView(int members, PackageWithProducts packageWithProducts) {
        return FoodPackageView.builder()
                .foodPackage(mapInfo(packageWithProducts.getFoodPackage()))
                .dayProducts(packageWithProducts.getPackageDays().stream()
                        .sorted(Comparator.comparingLong(PackageDayProducts::getDayId))
                        .map(this::mapDayView)
                        .collect(Collectors.toList()))
                .weight(packageWithProducts.getProductsWeight())
                .estimatedWeight(Math.round(packageWithProducts.getEstimatedWeight(members) * 100) / 100.0)
                .build();
    }

    public PlanWithPackagesView getPlanPackagesMembers(long planId) {
        List<HikerInfo> hikers = hikerService.getPlanHikers(planId);
        var packages = foodPackageDomainService.getPackagesWithProductsForPlan(planId);
        List<PackageWithProducts> sortedPackages = packages.values().stream()
                .sorted(Comparator.comparingDouble(pack -> pack.getEstimatedWeight(hikers.size())))
                .collect(Collectors.toList());
        return PlanWithPackagesView.builder()
                .members(hikers)
                .packages(sortedPackages.stream()
                        .map(pack -> mapView(hikers.size(), pack))
                        .collect(Collectors.toList()))
                .build();
    }
}
