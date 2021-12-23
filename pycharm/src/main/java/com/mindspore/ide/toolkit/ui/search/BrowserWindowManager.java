package com.mindspore.ide.toolkit.ui.search;

import com.intellij.openapi.project.Project;
import com.mindspore.ide.toolkit.common.config.GlobalConfig;

import java.util.HashMap;
import java.util.Map;

public class BrowserWindowManager {
    private static final Map<Project,BrowserWindowContent> BROWSER_WINDOW_CONTENT_MAP = new HashMap<>();

    public static BrowserWindowContent getBrowserWindow(Project project){
        if(BROWSER_WINDOW_CONTENT_MAP.get(project) == null ){
            BROWSER_WINDOW_CONTENT_MAP.put(project,new BrowserWindowContent(GlobalConfig.get().getToolWindowUrl()));
        }
        return  BROWSER_WINDOW_CONTENT_MAP.get(project);
    }
}
