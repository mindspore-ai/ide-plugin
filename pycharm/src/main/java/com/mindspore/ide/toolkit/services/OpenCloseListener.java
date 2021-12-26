package com.mindspore.ide.toolkit.services;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.mindspore.ide.toolkit.common.events.EventCenter;
import com.mindspore.ide.toolkit.common.events.ProjectEvents;
import com.mindspore.ide.toolkit.transplugins.Communicator;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class OpenCloseListener implements ProjectManagerListener {
    @Override
    public void projectOpened(@NotNull Project project) {
        Communicator.INSTANCE.setParams(project);
        EventCenter.INSTANCE.publish(new ProjectEvents.ProjectOpen());
    }

    @Override
    public void projectClosing(@NotNull Project project) {
    }
}