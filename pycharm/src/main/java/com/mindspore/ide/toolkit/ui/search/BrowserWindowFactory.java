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

package com.mindspore.ide.toolkit.ui.search;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.ui.content.Content;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class BrowserWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentManager contentManager = toolWindow.getContentManager();
        BrowserWindowContent browserWindow = BrowserWindowManager.getBrowserWindow(project);
        Content msContent = contentManager.getFactory().createContent(browserWindow.getContent(), null, true);
        toolWindow.setTitleActions(Collections.singletonList(ActionManager.getInstance().getAction("mindSporeRefreshAction")));
        contentManager.addContent(msContent);
        Disposer.register(project,browserWindow);
    }
}
