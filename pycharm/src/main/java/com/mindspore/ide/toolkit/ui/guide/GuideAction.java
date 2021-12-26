package com.mindspore.ide.toolkit.ui.guide;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.wm.ToolWindowManager;
import com.mindspore.ide.toolkit.common.config.GlobalConfig;
import org.jetbrains.annotations.NotNull;

public class GuideAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ToolWindowManager.getInstance(e.getProject()).getToolWindow(GlobalConfig.get().getToolWindowName()).show();
    }
}
