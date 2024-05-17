package com.kec.ileconfig.process;

import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FloatDateMap {
    private TreeMap<Date, Float> map;

    public FloatDateMap() {
        map = new TreeMap<>();
    }

    // Method to add a float and its corresponding date to the map
    public void add(Date date, float floatValue) {
        map.put(date, floatValue);
    }

    // Method to find the float given a date
    public Float findFloatFromDate(Date date) {
        return map.get(date);
    }

    public static FloatDateMap populateFromExcel(String filePath) {
        FloatDateMap floatDateMap = new FloatDateMap();

        try {
            FileInputStream file = new FileInputStream(filePath);
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            // Iterate over rows and populate the map
            for (Row row : sheet) {
                if (row.getRowNum() == 0) // Skip header row
                    continue;

                Cell dateCell = row.getCell(1);
                Cell czkCell = row.getCell(5);

                String dateString = dateCell.getStringCellValue();
                float czkValue = Float.parseFloat(czkCell.getStringCellValue());

                // Parse Date
                Date date = parseDate(dateString);

                floatDateMap.add(date, czkValue);
            }

            workbook.close();
            file.close();

            return floatDateMap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Helper method to parse Date from string
    private static Date parseDate(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return dateFormat.parse(dateString);
    }
}
