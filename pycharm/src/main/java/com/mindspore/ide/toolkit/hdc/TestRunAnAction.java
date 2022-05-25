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

package com.mindspore.ide.toolkit.hdc;

import com.intellij.execution.ExecutionBundle;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.impl.EditConfigurationsDialog;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.Experiments;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.jetbrains.annotations.NotNull;

/**
 * 点击事件
 *
 * @since 2022-04-18
 */
public class TestRunAnAction extends DumbAwareAction {
    @Override
    public void actionPerformed(@NotNull final AnActionEvent event) {
        Project project = event.getData(CommonDataKeys.PROJECT);
        if (project != null && project.isDisposed()) {
            return;
        }
        if (project == null) {
            // setup template project configurations
            project = ProjectManager.getInstance().getDefaultProject();
        }
        new EditConfigurationsDialog(project).show();
    }

    @Override
    public void update(@NotNull final AnActionEvent event) {
        Presentation presentation = event.getPresentation();
        Project project = event.getProject();
        boolean isEnabled = isEnabled(project);
        presentation.setEnabled(isEnabled);
        if (ActionPlaces.RUN_CONFIGURATIONS_COMBOBOX.equals(event.getPlace())) {
            presentation.setText(ExecutionBundle.messagePointer("edit.configuration.action"));
            presentation.setDescription(presentation.getText());
        }
    }

    private boolean isEnabled(Project project) {
        return (project == null
                || !DumbService.isDumb(project)
                || (Experiments.getInstance().isFeatureEnabled("edit.run.configurations.while.dumb")
                && ConfigurationType.CONFIGURATION_TYPE_EP
                .extensions()
                .anyMatch(ConfigurationTypeUtil::isEditableInDumbMode)));
    }
}