package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.model.plan.FoodPlan;
import com.outdoor.foodcalc.domain.model.plan.pack.HikerWithPackages;
import com.outdoor.foodcalc.domain.model.plan.pack.PackageWithProducts;
import com.outdoor.foodcalc.domain.service.plan.FoodPlanDomainService;
import com.outdoor.foodcalc.service.distributionBnbManual.ManualBnBDistributionService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExcelExportManualBnBService {

    private final FoodPlanDomainService foodPlanService;
    private final ManualBnBDistributionService engine;

    public ExcelExportManualBnBService(FoodPlanDomainService foodPlanService, ManualBnBDistributionService engine) {
        this.foodPlanService = foodPlanService;
        this.engine = engine;
    }

    public XSSFWorkbook exportFoodPlan(long planId) {
        FoodPlan plan = foodPlanService.getFoodPlan(planId)
                .orElseThrow(() -> new FoodcalcDomainException("Failed to load FoodPlan id = " + planId));
        //TODO export all packages
        List<PackageWithProducts> packagesWithProducts = engine.getPackagesWithProductsForPlan(planId, plan.getMembers().size());
        //TODO export best distribution (each member on separate  sheet)
        List<HikerWithPackages> bestDistribution = engine.findBestDistribution(plan, packagesWithProducts);
        XSSFWorkbook workbook = new XSSFWorkbook();
        workbook.getProperties().getCoreProperties().setTitle(plan.getName());

        Sheet sheet = workbook.createSheet("Distribution");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Tourist");
        header.createCell(1).setCellValue("Assigned Packages");

        int rowNum = 1;
        for (HikerWithPackages hiker : bestDistribution) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(hiker.getHiker().getName());

            // зібрати імена пакунків через кому
            String packagesList = hiker.getPackages().stream()
                    .map(p -> p.getFoodPackage().getName())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");

            row.createCell(1).setCellValue(packagesList);
        }

        // автоширина колонок
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        return workbook;
    }
}
