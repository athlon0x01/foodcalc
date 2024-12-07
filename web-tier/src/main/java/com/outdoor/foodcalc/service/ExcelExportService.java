package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.meal.Meal;
import com.outdoor.foodcalc.domain.model.plan.FoodPlan;
import com.outdoor.foodcalc.domain.model.plan.pack.FoodDistribution;
import com.outdoor.foodcalc.domain.model.plan.pack.PackageWithProducts;
import com.outdoor.foodcalc.domain.model.plan.PlanDay;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import com.outdoor.foodcalc.domain.service.plan.FoodPlanDomainService;
import com.outdoor.foodcalc.service.distribution.FoodDistributionEngine;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
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


        int rowNum = 0;
        Sheet sheet = workbook.createSheet("Food Plan By Days");

        // for headers
        CellStyle cellStyleHeader = sheet.getWorkbook().createCellStyle();
        // borders
        cellStyleHeader.setBorderBottom(BorderStyle.MEDIUM);
        cellStyleHeader.setBorderTop(BorderStyle.MEDIUM);
        cellStyleHeader.setBorderLeft(BorderStyle.MEDIUM);
        cellStyleHeader.setBorderRight(BorderStyle.MEDIUM);
        // alignment
        cellStyleHeader.setAlignment(HorizontalAlignment.CENTER);
        cellStyleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        // font stile
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 16);
        cellStyleHeader.setFont(headerFont);

        // for other headers
        CellStyle cellStyleHeader2 = sheet.getWorkbook().createCellStyle();
        // borders
        cellStyleHeader2.setBorderBottom(BorderStyle.MEDIUM);
        cellStyleHeader2.setBorderTop(BorderStyle.MEDIUM);
        cellStyleHeader2.setBorderLeft(BorderStyle.MEDIUM);
        cellStyleHeader2.setBorderRight(BorderStyle.MEDIUM);
        // alignment
        cellStyleHeader2.setAlignment(HorizontalAlignment.CENTER);
        cellStyleHeader2.setVerticalAlignment(VerticalAlignment.CENTER);
        // color
        cellStyleHeader2.setFillForegroundColor(IndexedColors.GREY_80_PERCENT.getIndex());
        cellStyleHeader2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // font stile
        Font headerFont2 = workbook.createFont();
        headerFont2.setBold(true);
        headerFont2.setFontHeightInPoints((short) 13);
        cellStyleHeader2.setFont(headerFont2);

        //for other cells
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        // borders
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        // alignment
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // font stile
        Font font = workbook.createFont();
        cellStyle.setFont(font);
        font.setFontHeightInPoints((short) 11);
        cellStyle.setFont(font);

        //for MealType
        CellStyle mealTypeStyle = sheet.getWorkbook().createCellStyle();
        // borders
        mealTypeStyle.setBorderBottom(BorderStyle.THIN);
        mealTypeStyle.setBorderTop(BorderStyle.THIN);
        mealTypeStyle.setBorderLeft(BorderStyle.THIN);
        mealTypeStyle.setBorderRight(BorderStyle.THIN);
        // alignment
        mealTypeStyle.setAlignment(HorizontalAlignment.LEFT);
        mealTypeStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // color
        mealTypeStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        mealTypeStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // font stile
        Font mealTypeFont = workbook.createFont();
        mealTypeStyle.setFont(mealTypeFont);
        mealTypeFont.setFontHeightInPoints((short) 11);
        mealTypeStyle.setFont(mealTypeFont);

        //for PlanDay
        CellStyle dayStyle = sheet.getWorkbook().createCellStyle();
        // borders
        dayStyle.setBorderBottom(BorderStyle.THIN);
        dayStyle.setBorderTop(BorderStyle.THIN);
        dayStyle.setBorderLeft(BorderStyle.THIN);
        dayStyle.setBorderRight(BorderStyle.THIN);
        // alignment
        dayStyle.setAlignment(HorizontalAlignment.LEFT);
        dayStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // color
        dayStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        dayStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // font stile
        Font dayFont = workbook.createFont();
        dayStyle.setFont(dayFont);
        dayFont.setFontHeightInPoints((short) 11);
        dayStyle.setFont(dayFont);

        //for Dish
        CellStyle dishStyle = sheet.getWorkbook().createCellStyle();
        // borders
        dishStyle.setBorderBottom(BorderStyle.THIN);
        dishStyle.setBorderTop(BorderStyle.THIN);
        dishStyle.setBorderLeft(BorderStyle.THIN);
        dishStyle.setBorderRight(BorderStyle.THIN);
        // alignment
        dishStyle.setAlignment(HorizontalAlignment.LEFT);
        dishStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // font stile
        Font dishFont = workbook.createFont();
        dishFont.setBold(true);
        dishStyle.setFont(dayFont);
        dishFont.setFontHeightInPoints((short) 11);
        dishStyle.setFont(dishFont);

        Row aRow = sheet.createRow(rowNum++);
        for (int i = 0; i < 7; i++) {
            aRow.createCell(i);
            aRow.getCell(i).setCellStyle(cellStyleHeader);
        }
        aRow.getCell(0).setCellValue("?????:");
        aRow.getCell(1).setCellValue(plan.getName());
        sheet.addMergedRegion(new CellRangeAddress(rowNum -1, rowNum -1, 1, 6));

        aRow = sheet.createRow(rowNum++);
        for (int i = 0; i < 7; i++) {
            aRow.createCell(i);
            aRow.getCell(i).setCellStyle(cellStyle);
        }
        aRow.getCell(0).setCellValue("????????");
        aRow.getCell(1).setCellValue(plan.getMembers().size());
        sheet.addMergedRegion(new CellRangeAddress(rowNum -1, rowNum -1, 1, 6));

        if (plan.getDescription() != null) {
            aRow = sheet.createRow(rowNum++);
            for (int i = 0; i < 7; i++) {
                aRow.createCell(i);
                aRow.getCell(i).setCellStyle(cellStyle);
            }
            aRow.getCell(0).setCellValue("????");
            aRow.createCell(1).setCellValue(plan.getDescription());
            sheet.addMergedRegion(new CellRangeAddress(rowNum -1, rowNum -1, 1, 6));
        }

        aRow = sheet.createRow(rowNum++);
        for (int i = 0; i < 7; i++) {
            aRow.createCell(i);
            aRow.getCell(i).setCellStyle(cellStyleHeader);
        }
        aRow.getCell(0).setCellValue("???? ?????? ?? ????:");
        sheet.addMergedRegion(new CellRangeAddress(rowNum -1, rowNum -1, 0, 6));

        aRow = sheet.createRow(rowNum++);
        String[] headers = {"?????", "????", "??????", "?????", "??????????", "????", "???????"};
        for (int i = 0; i < headers.length; i++) {
            aRow.createCell(i);
            aRow.getCell(i).setCellValue(headers[i]);
            aRow.getCell(i).setCellStyle(cellStyleHeader2);
            sheet.autoSizeColumn(i);
        }

        List<PlanDay> days = plan.getDays();
        for (PlanDay day : days) {
            aRow = sheet.createRow(rowNum++);
            aRow.createCell(0).setCellValue("???? ?? " + day.getDate());
            aRow.createCell(1).setCellValue(Math.round(day.getFoodDetails().getCalorific() * 100.0) / 100.0);
            aRow.createCell(2).setCellValue(Math.round(day.getFoodDetails().getProteins() * 100.0) / 100.0);
            aRow.createCell(3).setCellValue(Math.round(day.getFoodDetails().getFats() * 100.0) / 100.0);
            aRow.createCell(4).setCellValue(Math.round(day.getFoodDetails().getCarbs() * 100.0) / 100.0);
            aRow.createCell(5).setCellValue(Math.round(day.getFoodDetails().getWeight() * 100.0) / 100.0);
            aRow.createCell(6).setCellStyle(dayStyle);
            for (int i = 0; i < 7; i++) {
                aRow.getCell(i).setCellStyle(dayStyle);
            }
            if (day.getDescription() != null) {
                aRow = sheet.createRow(rowNum++);
                for (int i = 0; i < 7; i++) {
                    aRow.createCell(i);
                    aRow.getCell(i).setCellStyle(cellStyle);
                }
                aRow.getCell(0).setCellValue(day.getDescription());
                sheet.addMergedRegion(new CellRangeAddress(rowNum -1, rowNum -1, 0, 6));
                sheet.autoSizeColumn(0);
            }

            List<Meal> meals = day.getMeals();
            for (Meal meal : meals) {
                aRow = sheet.createRow(rowNum++);
                aRow.createCell(0).setCellValue(meal.getType().getName());
                aRow.createCell(1).setCellValue(Math.round(meal.getFoodDetails().getCalorific() * 100.0) / 100.0);
                aRow.createCell(2).setCellValue(Math.round(meal.getFoodDetails().getProteins() * 100.0) / 100.0);
                aRow.createCell(3).setCellValue(Math.round(meal.getFoodDetails().getFats() * 100.0) / 100.0);
                aRow.createCell(4).setCellValue(Math.round(meal.getFoodDetails().getCarbs() * 100.0) / 100.0);
                aRow.createCell(5).setCellValue(Math.round(meal.getFoodDetails().getWeight() * 100.0) / 100.0);
                aRow.createCell(6).setCellStyle(mealTypeStyle);
                for (int i = 0; i < 6; i++) {
                    aRow.getCell(i).setCellStyle(mealTypeStyle);
                }

                if (meal.getDescription() != null) {
                    aRow = sheet.createRow(rowNum++);
                    for (int i = 0; i < 7; i++) {
                        aRow.createCell(i);
                        aRow.getCell(i).setCellStyle(cellStyle);
                    }
                    aRow.getCell(0).setCellValue(meal.getDescription());
                    sheet.addMergedRegion(new CellRangeAddress(rowNum -1, rowNum -1, 0, 6));
                    sheet.autoSizeColumn(0);
                }

                List<Dish> dishes = meal.getDishes();
                for (Dish dish : dishes) {
                    aRow = sheet.createRow(rowNum++);
                    aRow.createCell(0).setCellValue(dish.getName());
                    aRow.createCell(1).setCellValue(Math.round(dish.getFoodDetails().getCalorific() * 100.0) / 100.0);
                    aRow.createCell(2).setCellValue(Math.round(dish.getFoodDetails().getProteins() * 100.0) / 100.0);
                    aRow.createCell(3).setCellValue(Math.round(dish.getFoodDetails().getFats() * 100.0) / 100.0);
                    aRow.createCell(4).setCellValue(Math.round(dish.getFoodDetails().getCarbs() * 100.0) / 100.0);
                    aRow.createCell(5).setCellValue(Math.round(dish.getFoodDetails().getWeight() * 100.0) / 100.0);
                    aRow.createCell(6).setCellStyle(dishStyle);
                    for (int i = 0; i < 6; i++) {
                        aRow.getCell(i).setCellStyle(dishStyle);
                    }

                    List<ProductRef> products = dish.getProducts();
                    for(ProductRef product : products) {
                        aRow = sheet.createRow(rowNum++);
                        aRow.createCell(0).setCellValue("   " + product.getName());
                        aRow.createCell(1).setCellValue(Math.round(product.getCalorific() * 100.0) / 100.0);
                        aRow.createCell(2).setCellValue(Math.round(product.getProteins() * 100.0) / 100.0);
                        aRow.createCell(3).setCellValue(Math.round(product.getFats() * 100.0) / 100.0);
                        aRow.createCell(4).setCellValue(Math.round(product.getCarbs() * 100.0) / 100.0);
                        aRow.createCell(5).setCellValue(Math.round(product.getWeight() * 100.0) / 100.0);
                        aRow.createCell(6).setCellStyle(cellStyle);
                        for (int i = 0; i < 6; i++) {
                            aRow.getCell(i).setCellStyle(cellStyle);
                        }
                    }
                }
            }
        }
        return workbook;
    }
}
