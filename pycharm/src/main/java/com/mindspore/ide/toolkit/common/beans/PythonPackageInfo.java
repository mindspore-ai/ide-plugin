package com.mindspore.ide.toolkit.common.beans;

public class PythonPackageInfo {
    private String name;
    private String version;

    public PythonPackageInfo(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
