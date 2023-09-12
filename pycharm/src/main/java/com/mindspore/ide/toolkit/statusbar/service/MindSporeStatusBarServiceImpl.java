package com.mindspore.ide.toolkit.statusbar.service;

import com.intellij.ide.lightEdit.LightEditCompatible;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.util.ui.EmptyIcon;
import com.mindspore.ide.toolkit.search.OperatorSearchService;
import com.mindspore.ide.toolkit.statusbar.MindSporeStatusBarWidget;
import com.mindspore.ide.toolkit.statusbar.utils.MindSporeVersionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class MindSporeStatusBarServiceImpl implements MindSporeStatusBarService {
    private String selectedVersion;

    public MindSporeStatusBarServiceImpl() {
        ApplicationManager
                .getApplication()
                .getMessageBus()
                .connect()
                .subscribe(MindSporeStatusBarService.TOPIC, this);
    }

    public static void notifyApp(String version) {
        ApplicationManager
                .getApplication()
                .getMessageBus()
                .syncPublisher(MindSporeStatusBarService.TOPIC)
                .mindSporeVersionChanged(version);
    }

    public static String getCurrentSelectedVersion() {
        return ApplicationManager.getApplication().getService(MindSporeStatusBarServiceImpl.class).getVersion();
    }

    public static void setCurrentSelectedVersion(String version) {
        ApplicationManager.getApplication().getService(MindSporeStatusBarServiceImpl.class).setVersion(version);
    }

    private String getVersion() {
        return this.selectedVersion;
    }

    private void setVersion(String version) {
        this.selectedVersion = version;
    }

    @Override
    public void mindSporeVersionChanged(String version) {
        if (StringUtils.isEmpty(version)) {
            return;
        }

        boolean shouldNotify = !version.equals(this.selectedVersion);
        this.selectedVersion = version;

        if (shouldNotify) {
            for(Project project : ProjectManager.getInstance().getOpenProjects()) {
                if (!project.isDisposed()) {
                    MindSporeStatusBarWidget.update(project);
                }
            }
        }
    }

    public static DefaultActionGroup createVersionActionsGroup() {
        String selectedVersion = getCurrentSelectedVersion();
        List<String> versions = new ArrayList<>(MindSporeVersionUtils.VERSION_MARKDOWN_MAP.keySet());
        Collections.sort(versions);
        versions.remove(selectedVersion);
        DefaultActionGroup group = new DefaultActionGroup();
        for (final String version : versions) {
            AnAction action = new VersionAction("mindspore " + version, EmptyIcon.ICON_16) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    log.debug("choose version:{} to mapping", version);
                    OperatorSearchService.INSTANCE.reset(version);
                    notifyApp(version);
                }

                @Override
                public void update(@NotNull AnActionEvent e) {
                    e.getPresentation().setEnabledAndVisible(true);
                }
            };
            group.add(action);
        }
        return group;
    }

    private abstract static class VersionAction extends DumbAwareAction implements LightEditCompatible {
        VersionAction(@NlsSafe String name, Icon icon) {
            super(name, null, icon);
        }
    }
}
