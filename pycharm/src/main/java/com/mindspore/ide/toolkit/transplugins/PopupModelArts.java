package com.mindspore.ide.toolkit.transplugins;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class PopupModelArts extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Communicator.INSTANCE.invokeModelArts(event);
    }
}
