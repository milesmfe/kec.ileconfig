package com.kec.ileconfig.settings;

public enum Page {
    HOME("home"),
    TEST("test"),
    CONFIG("configuration");

    private final String fileName;

    Page(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
