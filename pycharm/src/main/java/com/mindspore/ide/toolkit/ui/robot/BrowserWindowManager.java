/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2022. All rights reserved.
 */

package com.mindspore.ide.toolkit.ui.robot;

import com.intellij.openapi.project.Project;
import com.mindspore.ide.toolkit.common.config.GlobalConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理IntelliJ project的content
 *
 * @author zhangruojin
 * @since 2.0.0
 */
public class BrowserWindowManager {
    /**
     * 记录IntelliJ project
     */
    private static final Map<Project, BrowserWindowContent> BROWSER_WINDOW_CONTENT_MAP = new HashMap<>();

    /**
     * 获取指定IntelliJ project的content
     *
     * @param project 需要获取的window content的IntelliJ project对象
     * @return 参数IntelliJ project的content
     */
    public static BrowserWindowContent getBrowserWindow(Project project) {
        if (BROWSER_WINDOW_CONTENT_MAP.get(project) == null) {
            BROWSER_WINDOW_CONTENT_MAP.put(project, new BrowserWindowContent(GlobalConfig.get().getChatWindowUrl()));
        }
        return BROWSER_WINDOW_CONTENT_MAP.get(project);
    }
}
