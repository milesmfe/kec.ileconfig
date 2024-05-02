package com.kec.ileconfig.controllers;
import java.net.URL;
import java.util.ResourceBundle;

import com.kec.ileconfig.App;
import com.kec.ileconfig.settings.Settings;
import com.kec.ileconfig.settings.Theme;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class DefaultController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Menu changeThemeMenu;

    @FXML
    private MenuItem resetApp;

    @FXML
    private CheckMenuItem toggleTesting;


    @FXML
    void onClose(ActionEvent event) {
        App.close();
    }

    @FXML
    void onResetApp(ActionEvent event) {
        Settings.clear();
        App.close();
    }

    @FXML
    void onToggleTesting(ActionEvent event) {
        Boolean current = Settings.getTestingMode();
        Settings.setTestingMode(!current);
        App.close();
    }

    @FXML
    void initialize() {
        assert changeThemeMenu != null : "fx:id=\"changeThemeMenu\" was not injected: check your FXML file 'test.fxml'.";
        assert resetApp != null : "fx:id=\"resetApp\" was not injected: check your FXML file 'test.fxml'.";
        assert toggleTesting != null : "fx:id=\"toggleTesting\" was not injected: check your FXML file 'test.fxml'.";

        for (Theme theme : Theme.class.getEnumConstants()) {
            CheckMenuItem muItem = new CheckMenuItem(theme.getFriendlyName());
            if (Settings.getUserTheme().equals(theme)) {
                muItem.setSelected(true);
            }
            muItem.setOnAction(e -> {
                changeThemeMenu.getItems().forEach(m -> ((CheckMenuItem) m).setSelected(false));
                muItem.setSelected(true);
                App.setTheme(theme);
                Settings.setUserTheme(theme);
            });
            changeThemeMenu.getItems().add(muItem);
        }
        toggleTesting.setSelected(Settings.getTestingMode());
    }
}
