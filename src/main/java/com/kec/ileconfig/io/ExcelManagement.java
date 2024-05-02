package com.kec.ileconfig.io;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class ExcelManagement {

    public static void csvToExcel(String[][] data, String filePath) throws FileNotFoundException, IOException {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            int rowCount = 0;

            for (String[] rowData : data) {
                Row row = sheet.createRow(rowCount++);
                int colCount = 0;
                for (String cellData : rowData) {
                    Cell cell = row.createCell(colCount++);
                    cell.setCellValue(cellData);
                }
            }

            try (FileOutputStream fileOut = new FileOutputStream(filePath);
                    Writer writer = new OutputStreamWriter(fileOut, StandardCharsets.UTF_8)) {
                workbook.write(fileOut);
            } catch (IOException e) {
                System.err.println("Error writing Excel file: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Error writing Excel file: " + e.getMessage());
        }
    }
}
