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

package com.mindspore.ide.toolkit.demo;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.cef.browser.CefMessageRouter;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/**
 * 浏览器侧边栏
 *
 * @since 2022-04-18
 */
public class DemoBrowserWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentManager contentManager = toolWindow.getContentManager();
        DemoBrowserWindowContent browserWindow = DemoBrowserWindowManager.getBrowserWindow(project);
        Content msContent = contentManager.getFactory().createContent(browserWindow.getContent(), null, true);
        List<AnAction> actionList = new LinkedList<>();
        actionList.add(ActionManager.getInstance().getAction("PlatformioActionGroup1"));
        actionList.add(ActionManager.getInstance().getAction("demoRefreshAction"));
        toolWindow.setTitleActions(actionList);
        contentManager.addContent(msContent);
        browserWindow.refreshBrowser();
        CefMessageRouter router = CefMessageRouter.create(new CefMessageRouter.CefMessageRouterConfig());
        router.addHandler(new DemoMessageRouterHandlerAdapter(), true);
        browserWindow.getCefBrowser().getClient().addMessageRouter(router);
        Disposer.register(project, browserWindow);
    }
}