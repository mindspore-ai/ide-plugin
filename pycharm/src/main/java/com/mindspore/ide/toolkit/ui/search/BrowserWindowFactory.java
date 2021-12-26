package com.mindspore.ide.toolkit.ui.search;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class BrowserWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolWindow.setTitleActions(Collections.singletonList(ActionManager.getInstance().getAction("mindSporeRefreshAction")));
        ContentManager contentManager = toolWindow.getContentManager();
        BrowserWindowContent browserWindow = BrowserWindowManager.getBrowserWindow(project);
        Content content = contentManager.getFactory().createContent(browserWindow.getContent(), null,true);
        contentManager.addContent(content);
        toolWindow.hide();
        Disposer.register(project,browserWindow);
    }

}
