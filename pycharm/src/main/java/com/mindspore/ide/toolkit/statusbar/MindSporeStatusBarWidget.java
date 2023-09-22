package com.mindspore.ide.toolkit.statusbar;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.impl.status.EditorBasedStatusBarPopup;
import com.mindspore.ide.toolkit.smartcomplete.CompleteConfig;
import com.mindspore.ide.toolkit.statusbar.service.MindSporeStatusBarServiceImpl;
import com.mindspore.ide.toolkit.statusbar.utils.MindSporeVersionUtils;
import icons.MsIcons;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Slf4j
public class MindSporeStatusBarWidget extends EditorBasedStatusBarPopup {
    private static final String WIDGET_ID = "com.mindspore.widget";

    public MindSporeStatusBarWidget(@NotNull Project project) {
        super(project, false);
    }

    @NonNls
    @NotNull
    public String ID() {
        return WIDGET_ID;
    }

    public static void update(@NotNull Project project) {
        MindSporeStatusBarWidget widget = findWidget(project);
        if (widget != null) {
            widget.update(() -> widget.myStatusBar.updateWidget(WIDGET_ID));
        }
    }

    @Nullable
    private static MindSporeStatusBarWidget findWidget(@NotNull Project project) {
        StatusBar bar = WindowManager.getInstance().getStatusBar(project);
        if (bar != null) {
            StatusBarWidget widget = bar.getWidget(WIDGET_ID);
            if (widget instanceof MindSporeStatusBarWidget) {
                return (MindSporeStatusBarWidget) widget;
            }
        }
        return null;
    }

    @NotNull
    protected WidgetState getWidgetState(@Nullable VirtualFile file) {
        String version = MindSporeStatusBarServiceImpl.getCurrentSelectedVersion();
        if (StringUtils.isEmpty(version)) {
            version = MindSporeVersionUtils.getBigVersion(CompleteConfig.PLUGIN_VERSION);
            MindSporeStatusBarServiceImpl.setCurrentSelectedVersion(version);
            MindSporeVersionUtils.initVersionMap(version);
        }
        EditorBasedStatusBarPopup.WidgetState state = new EditorBasedStatusBarPopup.WidgetState("", "mindspore " + version, true);
        state.setIcon(MsIcons.MS_ICON_12PX);
        return state;
    }

    @Nullable
    protected ListPopup createPopup(DataContext context) {
        DefaultActionGroup group = MindSporeStatusBarServiceImpl.createVersionActionsGroup();
        DefaultActionGroup popupGroup = new DefaultActionGroup();

        AnAction action = ActionManager.getInstance().getAction("otherVersion");
        if (action != null) {
            popupGroup.add(action);
            popupGroup.addSeparator();
        }
        popupGroup.add(group);
        return JBPopupFactory.getInstance()
                .createActionGroupPopup("Api Mapping",
                        popupGroup,
                        context,
                        JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                        false);
    }

    @NotNull
    protected StatusBarWidget createInstance(@NotNull Project project) {
        return new MindSporeStatusBarWidget(project);
    }


}
