package com.mindspore.ide.toolkit.wizard;

import java.util.List;

public class MSVersionInfo {
    private String name;
    private List<OSInfo> osInfoList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<OSInfo> getOsInfoList() {
        return osInfoList;
    }

    public void setOsInfoList(List<OSInfo> osInfoList) {
        this.osInfoList = osInfoList;
    }
}
