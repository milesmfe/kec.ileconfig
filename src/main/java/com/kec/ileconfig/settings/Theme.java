package com.kec.ileconfig.settings;

public enum Theme {
    CUPERTINO_DARK("Cupertino Dark", "cupertino-dark"),
    CUPERTINO_LIGHT("Cupertino Light", "cupertino-light"),
    DRACULA("Dracula", "dracula"),
    NORD_DARK("Nord Dark", "nord-dark"),
    NORD_LIGHT("Nord Light", "nord-light"),
    PRIMER_DARK("Primer Dark", "primer-dark"),
    PRIMER_LIGHT("Primer Light", "primer-light");

    private final String friendlyName;
    private final String fileName;

    Theme(String friendlyName, String fileName) {
        this.friendlyName = friendlyName;
        this.fileName = fileName;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public String getFileName() {
        return fileName;
    }
}
