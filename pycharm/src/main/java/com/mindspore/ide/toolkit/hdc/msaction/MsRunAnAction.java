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

package com.mindspore.ide.toolkit.hdc.msaction;

import com.intellij.execution.impl.EditConfigurationsDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.mindspore.ide.toolkit.hdc.MsConfigurationFactory;
import com.mindspore.ide.toolkit.hdc.MsConfigurationType;
import org.jetbrains.annotations.NotNull;

/**
 * MsRunAnAction
 *
 * @since 2022-04-18
 */
public class MsRunAnAction extends AnAction {
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
        MsConfigurationType msConfigurationType = new MsConfigurationType();
        MsConfigurationFactory msConfigurationFactory = new MsConfigurationFactory(msConfigurationType);
        new EditConfigurationsDialog(project, msConfigurationFactory).show();
    }
}