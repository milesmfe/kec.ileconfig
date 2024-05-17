package com.kec.ileconfig.settings;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.kec.ileconfig.App;

public class Settings {
    private static final Preferences prefs = Preferences.userNodeForPackage(App.class);

    public enum Setting {
        USER_THEME(Theme.CUPERTINO_LIGHT),
        TESTING_MODE(false),
        WORKING_DIR_HISTORY(""),
        OUTPUT_FOLDER_HISTORY(""),
        FIRST_ILE_NO_HISTORY(""),
        FIRST_VE_NO_HISTORY(""),
        REGEX_DELIMITER_HISTORY(""),
        FOREX_FILE_HISTORY("");

        private final Object defaultValue;

        Setting(Object defaultValue) {
            this.defaultValue = defaultValue;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }
    }

    public static String getForexFileHistory() {
        return getSetting(Setting.FOREX_FILE_HISTORY);
    }

    public static void setForexFileHistory(String forexFile) {
        setSetting(Setting.FOREX_FILE_HISTORY, forexFile);
    }

    public static String getWorkingDirHistory() {
        return getSetting(Setting.WORKING_DIR_HISTORY);
    }

    public static void setWorkingDirHistory(String workingDir) {
        setSetting(Setting.WORKING_DIR_HISTORY, workingDir);
    }

    public static String getOutputFolderHistory() {
        return getSetting(Setting.OUTPUT_FOLDER_HISTORY);
    }

    public static void setOutputFolderHistory(String outputFolder) {
        setSetting(Setting.OUTPUT_FOLDER_HISTORY, outputFolder);
    }

    public static String getFirstILENoHistory() {
        return getSetting(Setting.FIRST_ILE_NO_HISTORY);
    }

    public static void setFirstILENoHistory(String firstILENo) {
        setSetting(Setting.FIRST_ILE_NO_HISTORY, firstILENo);
    }

    public static String getFirstVENoHistory() {
        return getSetting(Setting.FIRST_VE_NO_HISTORY);
    }

    public static void setFirstVENoHistory(String firstVENo) {
        setSetting(Setting.FIRST_VE_NO_HISTORY, firstVENo);
    }

    public static String getRegexDelimiterHistory() {
        return getSetting(Setting.REGEX_DELIMITER_HISTORY);
    }

    public static void setRegexDelimiterHistory(String regexDelimiter) {
        setSetting(Setting.REGEX_DELIMITER_HISTORY, regexDelimiter);
    }

    public static Theme getUserTheme() {
        return Theme.valueOf(getSetting(Setting.USER_THEME));
    }
    
        public static void setUserTheme(Theme theme) {
            setSetting(Setting.USER_THEME, theme.toString());
        }

    public static boolean getTestingMode() {
        return Boolean.parseBoolean(getSetting(Setting.TESTING_MODE));
    }
    
        public static void setTestingMode(boolean flag) {
            setSetting(Setting.TESTING_MODE, String.valueOf(flag));
        }
        
    private static String getSetting(Setting setting) {
        return prefs.get(setting.name(), setting.getDefaultValue().toString());
    }

    private static void setSetting(Setting setting, String value) {
        prefs.put(setting.name(), value);
    }

    public static void clear() {
        try {
            prefs.clear();
        } catch (BackingStoreException e) {
            System.err.println("Error clearing preferences: " + e.getMessage());
        }
    }
}
