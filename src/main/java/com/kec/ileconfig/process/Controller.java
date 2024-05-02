package com.kec.ileconfig.process;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import com.kec.ileconfig.io.ExcelManagement;
import com.kec.ileconfig.io.FileManagement;

import javafx.application.Platform;
import javafx.concurrent.Task;

@SuppressWarnings("unused")
public class Controller implements Runnable {
    private final String workingDir; // Location of relevant files
    private final String outputDir; // Folder name for output contents
    private final String binDir; // Location for tempory files used in processing to be stored

    private Process ileProcess = null; // The attached ileProcess
    private Process veProcess = null; // The attached veProcess

    private volatile int ileNoCount; // A counter for updating ILE entryNos with a given first index
    private volatile int veNoCount; // A counter for updating VE entryNos with a given first index
    private volatile Set<String> ileNoSet = new HashSet<>(); // A set for storing each unique ILE number
    private volatile Set<String> veNoSet = new HashSet<>(); // A set for storing each unique VE number
    private volatile HashMap<String, String> entryNoMap = new HashMap<>(); // Maps old to new entry numbers

    private HashMap<String, String[][]> outputCSVMap = new HashMap<>(); // Final output from ILE and VE processes

    /**
     * Create a new {@link #Controller} for handling the processing of ILE and VE
     * files
     * 
     * @param workingDir   the location of the directory containing all relavant
     *                     files
     * @param outputFolder the chosen name for the folder which will contain any
     *                     output contents
     * 
     * @see Controller
     * 
     */
    public Controller(String workingDir, String outputFolder, int firstILENo, int firstVENo, String regexDelimiter) {
        this.workingDir = workingDir;
        this.outputDir = workingDir + File.separator + outputFolder;
        this.binDir = this.outputDir + File.separator + "IGNORE";
        ileNoCount = firstILENo;
        veNoCount = firstVENo;

        String ileWorkingDir = workingDir + File.separator + "ILE";
        String ileOutputDir = outputDir + File.separator + "ILE";
        String ilebinDir = binDir + File.separator + "ILE";

        String veWorkingDir = workingDir + File.separator + "VE";
        String veOutputDir = outputDir + File.separator + "VE";
        String vebinDir = binDir + File.separator + "VE";

        this.ileProcess = new ILEProcess(this, ileWorkingDir, ileOutputDir, ilebinDir, regexDelimiter);
        this.veProcess = new VEProcess(this, veWorkingDir, veOutputDir, vebinDir, regexDelimiter);
    }

    private void putILENo(String ileNo) {
        entryNoMap.put(ileNo, String.valueOf(ileNoCount++));
    }

    private void putVENo(String veNo) {
        entryNoMap.put(veNo, String.valueOf(veNoCount++));
    }

    /**
     * Sorts entryNoSet and maps each value in ascending order.
     * Invokes {@link #putEntryNo} with each entryNo in the sorted list.
     * 
     */
    private void mapEntryNos() {
        ArrayList<String> sortedILENoList = new ArrayList<String>(ileNoSet);
        Collections.sort(sortedILENoList);
        for (String ileNo : sortedILENoList) {
            putILENo(ileNo);
        }

        ArrayList<String> sortedVENoList = new ArrayList<String>(veNoSet);
        Collections.sort(sortedVENoList);
        for (String veNo : sortedVENoList) {
            putVENo(veNo);
        }
    }

    /**
     * Thread-safe method for processes to access their controller's entry number
     * map.
     * 
     * @param entryNo the entry number to lookup
     * @return the new entry number
     */
    public synchronized String getEntryNo(String entryNo) {
        return entryNoMap.get(entryNo);
    }

    public synchronized void addILENoToSet(String ileNo) {
        ileNoSet.add(ileNo);
    }

    public synchronized void addVENoToSet(String veNo) {
        veNoSet.add(veNo);
    }

    @Override
    public void run() {
        if (ileProcess == null || veProcess == null) {
            return;
        }

        Task<Void> ileProcessTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ileProcess.run(); // Process ILEs
                return null;
            }
        };

        Task<Void> veProcessTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                veProcess.run(); // Process VEs
                return null;
            }
        };

        Task<Void> saveExcelTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                for (Entry<String, String[][]> csvEntry : outputCSVMap.entrySet()) {
                    try {
                        ExcelManagement.csvToExcel(csvEntry.getValue(), csvEntry.getKey());
                    } catch (Exception e) {
                        System.err.println("Error processing file: " + csvEntry.getKey());
                        e.printStackTrace();
                        continue;
                    }
                    System.out.println("Processed file: " + csvEntry.getKey());
                }
                return null;
            }
        };

        // Start the tasks concurrently using Threads
        Thread ileThread = new Thread(ileProcessTask);
        Thread veThread = new Thread(veProcessTask);

        ileThread.start();
        veThread.start();

        try {
            // Wait for both threads to complete
            ileThread.join();
            veThread.join();

            // Run once both processes have completed
            Platform.runLater(() -> {
                mapEntryNos(); // Map every entry number once all inputs are processed
                outputCSVMap.putAll(ileProcess.getCSVMap());
                outputCSVMap.putAll(veProcess.getCSVMap());
                Thread saveExcelThread = new Thread(saveExcelTask);
                saveExcelThread.start();
                System.out.println("Final ILE No: " + ileNoCount);
                System.out.println("Final VE No: " + veNoCount);

            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
