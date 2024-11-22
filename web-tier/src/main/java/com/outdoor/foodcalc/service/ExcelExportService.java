package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.model.plan.FoodPlan;
import com.outdoor.foodcalc.domain.service.plan.FoodPlanDomainService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ExcelExportService {

    private final FoodPlanDomainService foodPlanService;

    public ExcelExportService(FoodPlanDomainService foodPlanService) {
        this.foodPlanService = foodPlanService;
    }

    public XSSFWorkbook exportFoodPlan(long planId) {
        FoodPlan plan = foodPlanService.getFoodPlan(planId)
                .orElseThrow(() -> new FoodcalcDomainException("Failed to load FoodPlan id = " + planId));
        XSSFWorkbook workbook = new XSSFWorkbook();
        workbook.getProperties().getCoreProperties().setTitle(plan.getName());

        Sheet sheet = workbook.createSheet("Food Plan By Days");
        Row aRow = sheet.createRow(0);
        aRow.createCell(0).setCellValue("Name");
        aRow.createCell(1).setCellValue(plan.getName());
        aRow = sheet.createRow(1);
        aRow.createCell(0).setCellValue("Members");
        aRow.createCell(1).setCellValue(plan.getMembers());
        aRow = sheet.createRow(2);
        aRow.createCell(0).setCellValue("Description");
        aRow.createCell(1).setCellValue(plan.getDescription());
        return workbook;
    }
}
