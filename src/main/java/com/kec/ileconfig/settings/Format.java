package com.kec.ileconfig.settings;

public enum Format {
    STRING("String"),
    BOOLEAN("Boolean"),
    DATE("Date"),
    INTEGER("Integer");

    private final String friendlyName;

    Format(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName() {
        return friendlyName;
    }
}
