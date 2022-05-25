/*
 * Copyright 2021-2022 Huawei Technologies Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mindspore.ide.toolkit.hdc;

import com.intellij.openapi.project.Project;

import java.util.HashMap;
import java.util.Map;

/**
 * 浏览器窗口单例
 *
 * @since 2022-04-18
 */
public class HdcBrowserWindowManager {
    private static final Map<Project, HdcBrowserWindowContent> BROWSER_WINDOW_CONTENT_MAP = new HashMap<>();

    /**
     * get BrowserWindow
     *
     * @param project Project
     * @return HdcBrowserWindowContent
     */
    public static HdcBrowserWindowContent getBrowserWindow(Project project) {
        if (BROWSER_WINDOW_CONTENT_MAP.get(project) == null) {
            BROWSER_WINDOW_CONTENT_MAP.put(project, new HdcBrowserWindowContent());
        }
        return BROWSER_WINDOW_CONTENT_MAP.get(project);
    }
}