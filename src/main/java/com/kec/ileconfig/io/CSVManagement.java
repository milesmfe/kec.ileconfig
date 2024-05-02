package com.kec.ileconfig.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class CSVManagement {

    public static String processCSVFile(String filePath, String outputFolder) {
        try {
            // Read the CSV file
            File inputFile = new File(filePath);
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            StringBuilder csvContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                csvContent.append(line).append("\n");
            }
            reader.close();

            // Replace "," with "."
            String processedCSV = csvContent.toString().replaceAll(",", ".");

            // Write the processed CSV to a new file
            if (!FileManagement.createFolder(outputFolder)) {
                throw new IOException("Couldn't create folder: " + outputFolder);
            }
            String outputFilePath = outputFolder + File.separator + inputFile.getName();
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));
            writer.write(processedCSV);
            writer.close();
        
            System.out.println("CSV file processed successfully. Output file saved at: " + outputFilePath);
            return outputFilePath;
        } catch (IOException e) {
            System.err.println("Error processing CSV file: " + e.getMessage());
            return null;
        }
    }
    
    // Method to read CSV file and return a matrix of Strings representing the CSV data
    public static String[][] readCSV(String filePath) throws IOException {
        File inputFile = new File(filePath);
        StringBuilder csvContent = new StringBuilder();
        
        // Read CSV content as plain text with appropriate character encoding
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                csvContent.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            throw e;
        }

        // Split CSV content by lines and commas to form a matrix
        String[] rows = csvContent.toString().split("\n");
        String[][] matrix = new String[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            String[] columns = rows[i].split(",");
            matrix[i] = columns;
        }

        return matrix;
    }
}
