package com.mindspore.ide.toolkit.statusbar;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.impl.status.widget.StatusBarEditorBasedWidgetFactory;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class MindSporeStatusBarFactory extends StatusBarEditorBasedWidgetFactory {
    private static final String ID = "com.mindspore";

    @NonNls
    @NotNull
    @Override
    public String getId() {
        return ID;
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "MindSpore Dev ToolKit";
    }

    @NotNull
    @Override
    public StatusBarWidget createWidget(@NotNull Project project) {
        return new MindSporeStatusBarWidget(project);
    }

    @Override
    public void disposeWidget(@NotNull StatusBarWidget widget) {}

    @Override
    public boolean isConfigurable() {
        return false;
    }
}
