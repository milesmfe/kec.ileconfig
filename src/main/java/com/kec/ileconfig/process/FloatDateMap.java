package com.kec.ileconfig.process;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FloatDateMap {
    private TreeMap<Float, DatePair> map;

    public FloatDateMap() {
        map = new TreeMap<>();
    }

    // Method to add a float and its corresponding date pair to the map
    public void add(float floatValue, Date lowerBound, Date upperBound) {
        map.put(floatValue, new DatePair(lowerBound, upperBound));
    }

    // Method to find the float given a date
    public Float findFloatFromDate(Date date) {
        Map.Entry<Float, DatePair> entry = map.floorEntry(Float.MAX_VALUE); // Get the entry with the highest float value
        while (entry != null && !isDateInRange(date, entry.getValue())) {
            entry = map.lowerEntry(entry.getKey()); // Move to the next entry with a lower float value
        }
        return (entry != null) ? entry.getKey() : null;
    }

    // Helper method to check if a date is within the range of a DatePair
    private boolean isDateInRange(Date date, DatePair datePair) {
        return !date.before(datePair.getLowerBound()) && date.before(datePair.getUpperBound());
    }

    // Inner class to represent a pair of dates
    private class DatePair {
        private Date lowerBound;
        private Date upperBound;

        public DatePair(Date lowerBound, Date upperBound) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }

        public Date getLowerBound() {
            return lowerBound;
        }

        public Date getUpperBound() {
            return upperBound;
        }
    }

    public static FloatDateMap populateFromExcel(String filePath) throws IOException {
        FloatDateMap floatDateMap = new FloatDateMap();

        FileInputStream file = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

        // Iterate over rows and populate the map
        for (Row row : sheet) {
            if (row.getRowNum() == 0) // Skip header row
                continue;

            Cell lowerCell = row.getCell(0);
            Cell upperCell = row.getCell(1);
            Cell eurCell = row.getCell(2);
            Cell czkCell = row.getCell(3);

            // Assuming Lower, Upper, EUR, CZK are in columns A, B, C, D respectively
            Date lowerDate = lowerCell.getDateCellValue();
            Date upperDate = upperCell.getDateCellValue();
            float eurValue = (float) eurCell.getNumericCellValue();
            float czkValue = (float) czkCell.getNumericCellValue();

            floatDateMap.add(eurValue, lowerDate, upperDate);
            floatDateMap.add(czkValue, lowerDate, upperDate);
        }

        workbook.close();
        file.close();

        return floatDateMap;
    }
}

