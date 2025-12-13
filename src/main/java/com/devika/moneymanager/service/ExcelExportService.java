package com.devika.moneymanager.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

@Service
public class ExcelExportService {

    public <T> ByteArrayInputStream export(
            List<T> data,
            String sheetName,
            String[] headers,
            Function<T, Object[]> rowMapper
    ) throws IOException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);

        // Header
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // Rows
        int rowIdx = 1;
        for (T item : data) {
            Row row = sheet.createRow(rowIdx++);
            Object[] values = rowMapper.apply(item);

            for (int col = 0; col < values.length; col++) {
                Cell cell = row.createCell(col);
                Object value = values[col];

                if (value instanceof Number) {
                    cell.setCellValue(((Number) value).doubleValue());
                } else {
                    cell.setCellValue(value != null ? value.toString() : "");
                }
            }
        }

        // Auto-size
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new ByteArrayInputStream(out.toByteArray());
    }
}

