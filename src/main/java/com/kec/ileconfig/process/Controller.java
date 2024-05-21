package com.kec.ileconfig.process;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Logger;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import com.kec.ileconfig.io.ExcelManagement;

import javafx.concurrent.Task;

public class Controller implements Runnable {
    private final String outputDir; // Folder name for output contents
    private final String binDir; // Location for tempory files used in processing to be stored

    private Process ileProcess = null; // The attached ileProcess
    private Process veProcess = null; // The attached veProcess

    private volatile double progress = 0.0; // Progress of the this process
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this); // Property change support for progress

    private volatile String logMessage = ""; // Log message
    private static final Logger logger = Logger.getLogger(Controller.class.getName()); // Logger

    private volatile int ileNoCount; // A counter for updating ILE entryNos with a given first index
    private volatile int veNoCount; // A counter for updating VE entryNos with a given first index
    private volatile Set<String> ileNoSet = new HashSet<>(); // A set for storing each unique ILE number
    private volatile Set<String> veNoSet = new HashSet<>(); // A set for storing each unique VE number
    private volatile HashMap<String, String> entryNoMap = new HashMap<>(); // Maps old to new entry numbers

    private HashMap<String, String[][]> outputCSVMap = new HashMap<>(); // Final output from ILE and VE processes

    private final CountDownLatch latch; // Latch for synchronizing threads

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
    public Controller(String workingDir, String outputFolder, int firstILENo, int firstVENo, String regexDelimiter,
            String forexFile) {
        this.outputDir = workingDir + File.separator + outputFolder;
        this.binDir = this.outputDir + File.separator + "IGNORE";
        ileNoCount = firstILENo;
        veNoCount = firstVENo;

        String ileWorkingDir = workingDir + File.separator + "ILE";
        String ileOutputDir = outputDir + File.separator + "ILE";
        String ilebinDir = binDir + File.separator + "ILE";

        ConfigMaps.setForexMap(this, forexFile);

        String veWorkingDir = workingDir + File.separator + "VE";
        String veOutputDir = outputDir + File.separator + "VE";
        String vebinDir = binDir + File.separator + "VE";

        this.ileProcess = new ILEProcess(this, ileWorkingDir, ileOutputDir, ilebinDir, regexDelimiter);
        this.veProcess = new VEProcess(this, veWorkingDir, veOutputDir, vebinDir, regexDelimiter);

        this.latch = new CountDownLatch(2); // Two threads: ILE and VE
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        double oldProgress = this.progress;
        this.progress = progress;
        pcs.firePropertyChange("progress", oldProgress, progress);
    }

    public void log(String logMessage) {
        String oldLogMessage = this.logMessage;
        this.logMessage = logMessage;
        logger.info(logMessage);
        pcs.firePropertyChange("log", oldLogMessage, logMessage);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
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
                latch.countDown(); // Signal the completion of ILE thread
                return null;
            }
        };

        Task<Void> veProcessTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                veProcess.run(); // Process VEs
                latch.countDown(); // Signal the completion of VE thread
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
                        log("Error processing file: " + csvEntry.getKey());
                        e.printStackTrace();
                        continue;
                    }
                    log("Processed file: " + csvEntry.getKey());
                }
                return null;
            }
        };

        // Start the tasks concurrently using Threads
        Thread ileThread = new Thread(ileProcessTask);
        Thread veThread = new Thread(veProcessTask);

        ileThread.start();
        veThread.start();
        setProgress(0.2);

        try {
            // Wait for both threads to complete
            latch.await();
            setProgress(0.6);

            // Run once both processes have completed
            mapEntryNos(); // Map every entry number once all inputs are processed
            outputCSVMap.putAll(ileProcess.getSplitOutputCSVMap(10));
            outputCSVMap.putAll(veProcess.getSplitOutputCSVMap(10));

            Set<String> veProblemEntries = veProcess.getProblemEntries();
            Set<String> ileProblemEntries = ileProcess.getProblemEntries();

            // Create a new map to store problem entries
            HashMap<String, String[][]> newEntriesMap = new HashMap<>();

            // Use an iterator to avoid ConcurrentModificationException
            for (Iterator<Entry<String, String[][]>> it = outputCSVMap.entrySet().iterator(); it.hasNext();) {
                Entry<String, String[][]> entry = it.next();
                String[][] data = entry.getValue();
                ArrayList<String[]> problemLinesList = new ArrayList<>();
                ArrayList<String[]> remainingLinesList = new ArrayList<>();
                remainingLinesList.add(data[0]); // Add header row to remaining lines

                for (int i = 1; i < data.length; i++) {
                    String entryNo = data[i][0];
                    if (veProblemEntries.contains(entryNo) || ileProblemEntries.contains(entryNo)) {
                        problemLinesList.add(data[i]);
                    } else {
                        remainingLinesList.add(data[i]);
                    }
                }

                // Update the original entry with remaining lines if there are changes
                if (remainingLinesList.size() < data.length) {
                    String[][] remainingLines = new String[remainingLinesList.size()][data[0].length];
                    remainingLinesList.toArray(remainingLines);
                    entry.setValue(remainingLines);
                }

                // Add new entry with problem lines to the newEntriesMap
                if (!problemLinesList.isEmpty()) {
                    String[][] problemLines = new String[problemLinesList.size() + 1][data[0].length];
                    problemLines[0] = data[0];
                    for (int i = 0; i < problemLinesList.size(); i++) {
                        problemLines[i + 1] = problemLinesList.get(i);
                    }
                    newEntriesMap.put(entry.getKey().replace(".xlsx", " - PROBLEM.xlsx"), problemLines);
                }
            }

            // Add all new problem entries to the original map
            outputCSVMap.putAll(newEntriesMap);

            setProgress(0.8);
            Thread saveExcelThread = new Thread(saveExcelTask);
            saveExcelThread.start();
            try {
                // Wait for saveExcelThread to complete
                saveExcelThread.join();
            } catch (InterruptedException e) {
                log("Error: Excel save thread interrupted");
                e.printStackTrace();
            }
            log("Final ILE No: " + ileNoCount);
            log("Final VE No: " + veNoCount);

            setProgress(1.0);
        } catch (InterruptedException e) {
            log("Error: ILE and VE threads interrupted");
            e.printStackTrace();
        }
    }

}
