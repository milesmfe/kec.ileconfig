package com.kec.ileconfig.controllers;

import java.io.File;

import com.kec.ileconfig.process.Controller;
import com.kec.ileconfig.settings.Settings;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

public class HomeController extends DefaultController {

    private String workingDir;
    private String outputFolder;
    private String firstILENo;
    private String firstVENo;
    private String regexDelimiter;

    @FXML
    private TextField firstILENoField;

    @FXML
    private TextField firstVENoField;

    @FXML
    private TextField outputFolderField;

    @FXML
    private TextField regexDelimiterField;

    @FXML
    private Button startBtn;

    @FXML
    private Button viewResultBtn;

    @FXML
    private TextField workingDirField;

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
                Controller controllerProcess = new Controller(workingDir, outputFolder, Integer.parseInt(firstILENo), Integer.parseInt(firstVENo), regexDelimiter);
                controllerProcess.run(); // Start the Controller
                return null;
            }
        };

        // Update UI based
        controllerTask.setOnRunning(e -> {
            // Show processing indicator or status
            System.out.println("Processing...");
            viewResultBtn.setVisible(false);
        });

        controllerTask.setOnSucceeded(e -> {
            // Update UI when task completes successfully
            System.out.println("Processing completed.");
            startBtn.setDisable(false);
            viewResultBtn.setVisible(true);
        });

        controllerTask.setOnFailed(e -> {
            // Update UI when task fails
            System.out.println("Processing failed: " + controllerTask.getException().getMessage());
            startBtn.setDisable(false);
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

        // Get the initial values for the fields
        workingDir = Settings.getWorkingDirHistory();
        outputFolder = Settings.getOutputFolderHistory();
        firstILENo = Settings.getFirstILENoHistory();
        firstVENo = Settings.getFirstVENoHistory();
        regexDelimiter = Settings.getRegexDelimiterHistory();

        // Set the initial values for the fields
        workingDirField.setText(workingDir);
        outputFolderField.setText(outputFolder);
        firstILENoField.setText(firstILENo);
        firstVENoField.setText(firstVENo);
        regexDelimiterField.setText(regexDelimiter);

        assert firstILENoField != null : "fx:id=\"firstILENoField\" was not injected: check your FXML file 'home.fxml'.";
        assert firstVENoField != null : "fx:id=\"firstVENoField\" was not injected: check your FXML file 'home.fxml'.";        
        assert outputFolderField != null : "fx:id=\"outputFolderField\" was not injected: check your FXML file 'home.fxml'.";
        assert regexDelimiterField != null : "fx:id=\"regexDelimiterField\" was not injected: check your FXML file 'home.fxml'.";
        assert startBtn != null : "fx:id=\"startBtn\" was not injected: check your FXML file 'home.fxml'.";
        assert workingDirField != null : "fx:id=\"workingDirField\" was not injected: check your FXML file 'home.fxml'.";
        assert viewResultBtn != null : "fx:id=\"viewResultBtn\" was not injected: check your FXML file 'home.fxml'.";
    }
}
