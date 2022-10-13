/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2022. All rights reserved.
 */

package com.mindspore.ide.toolkit.ui.robot;

import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import com.mindspore.ide.toolkit.common.config.GlobalConfig;
import com.mindspore.ide.toolkit.common.events.EventCenter;
import com.mindspore.ide.toolkit.common.events.ToolWindowEvents;
import org.jetbrains.annotations.NotNull;
import java.util.List;

/**
 * 对话ui监听器
 *
 * @author zhangruojin
 * @since 2.0.0
 */
public class MindSporeToolWindowListener implements ToolWindowManagerListener {
    @Override
    public void toolWindowsRegistered(@NotNull List<String> ids, @NotNull ToolWindowManager toolWindowManager) {
        ToolWindowManagerListener.super.toolWindowsRegistered(ids, toolWindowManager);
    }

    @Override
    public void toolWindowUnregistered(@NotNull String id, @NotNull ToolWindow toolWindow) {
        ToolWindowManagerListener.super.toolWindowUnregistered(id, toolWindow);
    }

    @Override
    public void stateChanged(@NotNull ToolWindowManager toolWindowManager) {
        ToolWindowManagerListener.super.stateChanged(toolWindowManager);
    }

    @Override
    public void toolWindowShown(@NotNull ToolWindow toolWindow) {
        ToolWindowManagerListener.super.toolWindowShown(toolWindow);
        if (toolWindow.getId().equals(GlobalConfig.get().getChatWindowName())) {
            EventCenter.INSTANCE.publish(new ToolWindowEvents());
        }
    }
}
