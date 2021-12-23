package com.mindspore.ide.toolkit.common.enums;


import com.mindspore.ide.toolkit.common.utils.PropertiesUtil;

public enum EnumNotifyGroup {
    MIND_SPORE(PropertiesUtil.getProperty("project.ward.name"),
            PropertiesUtil.getProperty("project.ward.name"),
            PropertiesUtil.getProperty("project.ward.name"));

    private String displayId;
    private String title;
    private String description;

    EnumNotifyGroup(String displayId, String title, String description) {
        this.displayId = displayId;
        this.title = title;
        this.description = description;
    }

    public String getDisplayId() {
        return displayId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
