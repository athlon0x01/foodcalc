package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.model.plan.FoodPlan;
import com.outdoor.foodcalc.domain.model.plan.pack.FoodDistribution;
import com.outdoor.foodcalc.domain.model.plan.pack.PackageWithProducts;
import com.outdoor.foodcalc.domain.service.plan.FoodPlanDomainService;
import com.outdoor.foodcalc.service.distribution.FoodDistributionEngine;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExcelExportService {

    private final FoodPlanDomainService foodPlanService;
    private final FoodDistributionEngine engine;

    public ExcelExportService(FoodPlanDomainService foodPlanService, FoodDistributionEngine engine) {
        this.foodPlanService = foodPlanService;
        this.engine = engine;
    }

    public XSSFWorkbook exportFoodPlan(long planId) {
        FoodPlan plan = foodPlanService.getFoodPlan(planId)
                .orElseThrow(() -> new FoodcalcDomainException("Failed to load FoodPlan id = " + planId));
        //TODO export all packages
        List<PackageWithProducts> packagesWithProducts = engine.getPackagesWithProductsForPlan(planId, plan.getMembers().size());
        //TODO export best distribution (each member on separate  sheet)
        FoodDistribution bestDistribution = engine.findBestDistribution(plan, packagesWithProducts);
        XSSFWorkbook workbook = new XSSFWorkbook();
        workbook.getProperties().getCoreProperties().setTitle(plan.getName());

        Sheet sheet = workbook.createSheet("Food Plan By Days");
        Row aRow = sheet.createRow(0);
        aRow.createCell(0).setCellValue("Name");
        aRow.createCell(1).setCellValue(plan.getName());
        aRow = sheet.createRow(1);
        aRow.createCell(0).setCellValue("Members");
        aRow.createCell(1).setCellValue(bestDistribution.getHikerPackages().size());
        aRow = sheet.createRow(2);
        aRow.createCell(0).setCellValue("Description");
        aRow.createCell(1).setCellValue(plan.getDescription());
        return workbook;
    }
}
