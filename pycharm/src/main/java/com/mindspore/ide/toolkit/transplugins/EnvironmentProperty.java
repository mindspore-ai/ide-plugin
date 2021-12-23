package com.mindspore.ide.toolkit.transplugins;

public class EnvironmentProperty {
    private String hardware;
    private String os;

    public String getHardware() {
        return hardware;
    }

    public void setHardware(String hardware) {
        this.hardware = hardware;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public static boolean valid(EnvironmentProperty property) {
        if (property == null) {
            return false;
        }
        if (property.getHardware() == null || property.getOs() == null) {
            return false;
        }
        return true;
    }
}