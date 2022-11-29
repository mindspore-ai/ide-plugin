/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2022. All rights reserved.
 */

package com.mindspore.ide.toolkit.ui.robot;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 响应刷新操作
 *
 * @author zhangruojin
 * @since 2.0.0
 */
public class SearchRefreshAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        BrowserWindowManager.getBrowserWindow(event.getProject()).refreshBrowser();
    }
}
