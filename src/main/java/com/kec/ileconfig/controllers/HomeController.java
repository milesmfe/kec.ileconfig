package com.kec.ileconfig.controllers;

import java.beans.PropertyChangeListener;
import java.io.File;

import com.kec.ileconfig.process.Controller;
import com.kec.ileconfig.settings.Settings;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class HomeController extends DefaultController {

    private String workingDir;
    private String outputFolder;
    private String firstILENo;
    private String firstVENo;
    private String regexDelimiter;
    private String forexFile;
    private ObservableList<String> logList;

    @FXML
    private TextField firstILENoField;

    @FXML
    private TextField firstVENoField;

    @FXML
    private TextField outputFolderField;

    @FXML
    private TextField regexDelimiterField;

    @FXML
    private TextField forexFileField;

    @FXML
    private Button startBtn;

    @FXML
    private Button viewResultBtn;

    @FXML
    private TextField workingDirField;

    @FXML
    private ListView<String> logListView;

    @FXML
    private ProgressBar progressBar;

    @FXML
    void onChangeWorkingDir(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Working Directory");
        File file = directoryChooser.showDialog(workingDirField.getScene().getWindow());
        workingDir = file == null ? "" : file.getAbsolutePath();
        workingDirField.setText(workingDir);
        Settings.setWorkingDirHistory(workingDir);
        workingDirField.setStyle("-fx-border-color: none;");
    }

    @FXML
    void onSaveFirstILENo(ActionEvent event) {
        firstILENo = firstILENoField.getText();
        Settings.setFirstILENoHistory(firstILENo);
        firstILENoField.setStyle("-fx-border-color: none;");
    }

    @FXML
    void onSaveFirstVENo(ActionEvent event) {
        firstVENo = firstVENoField.getText();
        Settings.setFirstVENoHistory(firstVENo);
        firstVENoField.setStyle("-fx-border-color: none;");
    }

    @FXML
    void onSaveOutputFolderName(ActionEvent event) {
        outputFolder = outputFolderField.getText();
        Settings.setOutputFolderHistory(outputFolder);
        outputFolderField.setStyle("-fx-border-color: none;");
    }

    @FXML
    void onSaveRegexDelimiter(ActionEvent event) {
        regexDelimiter = regexDelimiterField.getText();
        Settings.setRegexDelimiterHistory(regexDelimiter);
        regexDelimiterField.setStyle("-fx-border-color: none;");
    }

    @FXML
    void onChangeForexFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Forex File");
        File file = fileChooser.showOpenDialog(forexFileField.getScene().getWindow());
        forexFile = file == null ? "" : file.getAbsolutePath();
        forexFileField.setText(forexFile);
        Settings.setForexFileHistory(forexFile);
        forexFileField.setStyle("-fx-border-color: none;");
    }

    @FXML
    void onViewResults(ActionEvent event) {
        // Open the output folder in finder using java.awt.Desktop
        File file = new File(workingDir + File.separator + outputFolder);
        if (file.exists()) {
            try {
                java.awt.Desktop.getDesktop().open(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onStart(ActionEvent event) {
        Boolean flag = false;
        // Validate the fields
        if (workingDir.isEmpty()) {
            workingDirField.setStyle("-fx-border-color: red;");
            flag = true;
        } else {
            workingDirField.setStyle("-fx-border-color: none;");
        }      

        if (outputFolder.isEmpty()) {
            outputFolderField.setStyle("-fx-border-color: red;");
            flag = true;
        } else {
            outputFolderField.setStyle("-fx-border-color: none;");
        }

        if (firstILENo.isEmpty() | !firstILENo.matches("\\d+")) {
            firstILENoField.setStyle("-fx-border-color: red;");
            flag = true;
        } else {
            firstILENoField.setStyle("-fx-border-color: none;");
        }

        if (firstVENo.isEmpty() | !firstVENo.matches("\\d+")) {
            firstVENoField.setStyle("-fx-border-color: red;");
            flag = true;
        } else {
            firstVENoField.setStyle("-fx-border-color: none;");
        }

        if (regexDelimiter.isEmpty()) {
            regexDelimiterField.setStyle("-fx-border-color: red;");
            flag = true;
        } else {
            regexDelimiterField.setStyle("-fx-border-color: none;");
        }

        if (forexFile.isEmpty()) {
            forexFileField.setStyle("-fx-border-color: red;");
            flag = true;
        } else {
            forexFileField.setStyle("-fx-border-color: none;");
        }

        // If any field is invalid, return
        if (flag) {
            return;
        }

        // Disable the startBtn
        startBtn.setDisable(true);

        // Instantiate a Task to run the process
        Task<Void> controllerTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Controller controllerProcess = new Controller(workingDir, outputFolder, Integer.parseInt(firstILENo), Integer.parseInt(firstVENo), regexDelimiter, forexFile);
                controllerProcess.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(java.beans.PropertyChangeEvent evt) {
                        // Update the progress bar
                        if ("log".equals(evt.getPropertyName())) {
                            // Update the logs
                            // logList.add(String.valueOf(evt.getNewValue()));
                            logListView.scrollTo(logList.size() - 1);

                        } else if ("progress".equals(evt.getPropertyName())) {
                            // Update the progress bar
                            progressBar.setProgress((double) evt.getNewValue());
                        }
                    }
                });
                controllerProcess.run(); // Start the Controller
                return null;
            }
        };

        // Update UI based
        controllerTask.setOnRunning(e -> {
            // Show processing indicator or status
            System.out.println("Processing...");
            viewResultBtn.setVisible(false);
            progressBar.setVisible(true);
        });

        controllerTask.setOnSucceeded(e -> {
            // Update UI when task completes successfully
            System.out.println("Processing completed.");
            startBtn.setDisable(false);
            viewResultBtn.setVisible(true);
            progressBar.setVisible(false);
        });

        controllerTask.setOnFailed(e -> {
            // Update UI when task fails
            System.out.println("Processing failed: " + controllerTask.getException().getMessage());
            startBtn.setDisable(false);
            progressBar.setVisible(false);
        });

        // Start the task in a new thread
        Thread controllerThread = new Thread(controllerTask);
        controllerThread.setDaemon(true); // Ensure the thread terminates when the application exits
        controllerThread.start();
    }

    @FXML
    void initialize() {
        super.initialize(); 

        // Hide the viewResultBtn
        viewResultBtn.setVisible(false);
        progressBar.setVisible(false);

        // Get the initial values for the fields
        workingDir = Settings.getWorkingDirHistory();
        outputFolder = Settings.getOutputFolderHistory();
        firstILENo = Settings.getFirstILENoHistory();
        firstVENo = Settings.getFirstVENoHistory();
        regexDelimiter = Settings.getRegexDelimiterHistory();
        forexFile = Settings.getForexFileHistory();
        logList = FXCollections.observableArrayList();

        // Set the initial values for the fields
        workingDirField.setText(workingDir);
        outputFolderField.setText(outputFolder);
        firstILENoField.setText(firstILENo);
        firstVENoField.setText(firstVENo);
        regexDelimiterField.setText(regexDelimiter);
        forexFileField.setText(forexFile);
        logListView.setItems(logList);
    }
}
