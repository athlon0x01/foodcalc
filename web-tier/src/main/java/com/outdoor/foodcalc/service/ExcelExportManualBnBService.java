package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.model.plan.FoodPlan;
import com.outdoor.foodcalc.domain.model.plan.pack.HikerState;
import com.outdoor.foodcalc.domain.model.plan.pack.PackageWithProducts;
import com.outdoor.foodcalc.domain.service.plan.FoodPlanDomainService;
import com.outdoor.foodcalc.service.distributionBnbManual.ManualBnBDistributionService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

        List<PackageWithProducts> packagesWithProducts =
                engine.getPackagesWithProductsForPlan(planId, plan.getMembers().size());

        List<HikerState> bestStates =
                engine.findBestDistribution(plan, packagesWithProducts);

        XSSFWorkbook workbook = new XSSFWorkbook();
        workbook.getProperties().getCoreProperties().setTitle(plan.getName());

        Sheet sheet = workbook.createSheet("Distribution");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Tourist");
        header.createCell(1).setCellValue("Assigned Packages");

        int rowNum = 1;
        for (HikerState hiker : bestStates) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(hiker.getHiker().getName());

            // отримаємо Map<LocalDate, Set<PackageWithProducts>> із HikerState
            Map<LocalDate, Set<PackageWithProducts>> byDay =
                    hiker.getAssignedByDay() != null ? hiker.getAssignedByDay() : Map.of();

            // Формуємо текст по днях
            StringBuilder sb = new StringBuilder();
            engine.getSortedDates().forEach(day -> {
                Set<PackageWithProducts> dayPacks = byDay.getOrDefault(day, Set.of());
                if (!dayPacks.isEmpty()) {
                    sb.append(day).append(": ");
                    sb.append(dayPacks.stream()
                            .map(p -> p.getFoodPackage().getName())
                            .sorted()
                            .collect(Collectors.joining(", ")));
                    sb.append("\n");
                }
            });

            row.createCell(1).setCellValue(sb.toString().trim());
            row.getCell(1).getCellStyle().setWrapText(true);

        }

        // автоширина колонок
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        return workbook;
    }
}
