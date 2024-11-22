package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.service.ExcelExportService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("${spring.data.rest.basePath}/plans")
public class ExportEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(ExportEndpoint.class);

    private final ExcelExportService excelExportService;

    public ExportEndpoint(ExcelExportService excelExportService) {
        this.excelExportService = excelExportService;
    }

    @GetMapping(path = "/{planId}/download" )
    public ResponseEntity<byte[]> exportFoodPlan(@PathVariable("planId") long planId) throws IOException {
        LOG.debug("Exporting food plan id = {}", planId);
        try (XSSFWorkbook workbook = excelExportService.exportFoodPlan(planId)) {
            String fileName = workbook.getProperties().getCoreProperties().getTitle() + ".xlsx";
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            ContentDisposition disposition = ContentDisposition.attachment()
                    .filename(fileName)
                    .build();
            responseHeaders.setContentDisposition(disposition);
            return new ResponseEntity<>(outputStream.toByteArray(), responseHeaders, HttpStatus.OK);
        }
    }
}
