package com.kec.ileconfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.kec.ileconfig.settings.Page;
import com.kec.ileconfig.settings.Settings;
import com.kec.ileconfig.settings.Theme;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static final Map<Page, Scene> scenes = new HashMap<>();
    private static Stage root;

    @Override
    public void start(Stage stage) throws IOException {
        root = stage;
        root.setMinWidth(600);
        root.setMinHeight(400);
        if (Settings.getTestingMode()) {
            showScene(Page.TEST);
        } else {
            showScene(Page.HOME);
        }
    }

    public static void close() {
        Platform.exit();
    }

    public static void showScene(Page page) {
        if (!scenes.containsKey(page)) {
            initialiseScene(page);
        }
        root.setScene(scenes.get(page));
        setTheme(Settings.getUserTheme());
        root.show();
    }

    private static void initialiseScene(Page page) {
        try {
            scenes.put(page, new Scene(loadFXML(page.getFileName())));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to initialise scene: " + page);
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        return new FXMLLoader(App.class.getResource("/com/kec/ileconfig/fxml/" + fxml + ".fxml")).load();
    }

    public static void setTheme(Theme theme) {
        root.getScene().setUserAgentStylesheet(
                App.class.getResource("/com/kec/ileconfig/themes/" + theme.getFileName() + ".css").toString());
    }

    public static void main(String[] args) {
        launch();
    }
}
