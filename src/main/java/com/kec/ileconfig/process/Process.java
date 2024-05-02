package com.kec.ileconfig.process;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import com.kec.ileconfig.io.CSVManagement;
import com.kec.ileconfig.io.ExcelManagement;
import com.kec.ileconfig.io.FileManagement;

@SuppressWarnings("unused")
public class Process implements Runnable {
    protected final Controller controller; // The controller for this process

    protected final String[] fields; // A list of fields for output headers
    protected final String workingDir; // Location of relevant files
    protected final String outputDir; // Folder name for output contents
    protected final String binDir; // Location for tempory files used in processing to be stored

    protected final String regexDelimiter; // Delimiter for splitting data in the first column of the csv

    protected final HashMap<String, String[][]> outputCSVMap; // Maps csv data structures to file paths

    // Field indices
    protected final int entryNoIdx = 0; // Entry Number
    protected final int custIdx = 4; // Customer (Source) Number
    protected final int locationCodeIdx = 7; // Location Code

    /**
     * Create a new generic {@link #Process} for processing csv files
     * 
     * @param fields     a list of fields for output headers.
     * @param workingDir the location of (path to) all relevant files.
     * 
     */
    public Process(Controller controller, String[] fields, String workingDir, String outputDir, String binDir, String regexDelimiter) {
        this.controller = controller;
        this.fields = fields;
        this.workingDir = workingDir;
        this.outputDir = outputDir;
        this.binDir = binDir;
        this.outputCSVMap = new HashMap<>();
        this.regexDelimiter = regexDelimiter;
    }

    public HashMap<String, String[][]> getCSVMap() {
        FileManagement.createFolder(outputDir);
        HashMap<String, String[][]> output = new HashMap<>();
        for (Entry<String, String[][]> csvEntry : outputCSVMap.entrySet()) {
            String[][] data = updateCSVEntryOnGetCSVMap(updateEntryNo(csvEntry.getValue()));
            output.put(csvEntry.getKey(), data);
        }
        return output;
    }

    @Override
    public void run() {
        try {
            if (!generateProcessedCSVs()) {
                return;
            }

            File[] files = new File(binDir).listFiles();
            if (files == null) {
                return;
            }
            for (File file : files) {
                if (!(file.isFile() && file.getName().endsWith(".csv"))) {
                    continue;
                }
                String[][] processedData = processDataPortCSV(file.getAbsolutePath());
                if (processedData == null) {
                    continue;
                }

                String outputFile = outputDir + File.separator + file.getName().replace(".csv", ".xlsx");
                outputCSVMap.put(outputFile, processedData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error processing files");
        }
    }

    protected String[][] updateCSVEntryOnGetCSVMap(String[][] input) {
        return input.clone();
    }

    protected String[][] updateEntryNo(String[][] input) {
        String[][] output = input.clone();
        for (int rowIdx = 1; rowIdx < output.length; rowIdx++) {
            String entryNoIValue = output[rowIdx][entryNoIdx];
            output[rowIdx][entryNoIdx] = controller.getEntryNo(entryNoIValue);
        }
        return output;
    }

    protected Boolean generateProcessedCSVs() {
        if (!FileManagement.createFolder(workingDir) | !FileManagement.createFolder(binDir)) {
            return false;
        }
        File[] files = new File(workingDir).listFiles();
        if (files == null) {
            return false;
        }
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".csv")) {
                CSVManagement.processCSVFile(file.getAbsolutePath(), binDir);
            }
        }
        return true;
    }

    protected void addEntryNoToSet(String entryNo) {}

    protected String[][] processDataPortCSV(String filePath) {
        try {
            // Read all rows of the CSV file
            String[][] csv = CSVManagement.readCSV(filePath);

            // Initialize processed CSV array and output array
            String[][] processedCSV = new String[csv.length][fields.length];
            String[][] out = new String[processedCSV.length + 1][fields.length];

            // Set the header in the output array
            out[0] = fields;

            // Process each row of the CSV data
            for (int rowIdx = 0; rowIdx < csv.length; rowIdx++) {
                String firstCol = csv[rowIdx][0].replaceAll("\"", ""); // Get the first column of the current row
                processedCSV[rowIdx] = firstCol.split("\\" + regexDelimiter); // Split the first column by the delimiter

                // Update customer values to NAV17
                String custValue = processedCSV[rowIdx][custIdx];
                if (ConfigMaps.getMappedCustomers().contains(custValue)) {
                    processedCSV[rowIdx][custIdx] = ConfigMaps.getCustomerMapFor(custValue).getNAV17();
                }
                // Change all location codes to HISTORY
                processedCSV[rowIdx][locationCodeIdx] = "HISTORY";

                // Update the controller entry number set
                this.addEntryNoToSet(processedCSV[rowIdx][entryNoIdx]);
            }

            // Copy processed CSV data to the output array)
            System.arraycopy(processedCSV, 0, out, 1, processedCSV.length);

            return out;
        } catch (IOException | IndexOutOfBoundsException e) {
            // Handle IOException
            System.err.println("Error processing file: " + filePath);
            return null;
        }
    }
}