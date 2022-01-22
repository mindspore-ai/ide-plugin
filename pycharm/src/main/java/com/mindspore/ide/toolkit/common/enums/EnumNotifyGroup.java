package com.mindspore.ide.toolkit.common.enums;

public enum EnumNotifyGroup {
    MIND_SPORE(EnumProperties.MIND_SPORE_PROPERTIES.getProperty("project.ward.name"),
            EnumProperties.MIND_SPORE_PROPERTIES.getProperty("project.ward.name"),
            EnumProperties.MIND_SPORE_PROPERTIES.getProperty("project.ward.name"));

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
