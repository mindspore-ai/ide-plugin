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

import com.intellij.execution.Executor;
import com.intellij.execution.ExecutorRegistry;
import com.intellij.execution.actions.ChooseRunConfigurationPopup;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowId;
import org.jetbrains.annotations.NotNull;

/**
 * 点击事件
 *
 * @since 2022-04-18
 */
public class TestRunAnAction2 extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        final Project project = event.getData(CommonDataKeys.PROJECT);
        assert project != null;
        new ChooseRunConfigurationPopup(project,
                getAdKey(),
                getDefaultExecutor(),
                getAlternativeExecutor()).show();
    }

    /**
     * get Default Executor
     *
     * @return Executor
     */
    protected Executor getDefaultExecutor() {
        return DefaultRunExecutor.getRunExecutorInstance();
    }

    /**
     * get Alternative Executor
     *
     * @return Executor
     */
    protected Executor getAlternativeExecutor() {
        return ExecutorRegistry.getInstance().getExecutorById(ToolWindowId.DEBUG);
    }

    protected String getAdKey() {
        return "run.configuration.alternate.action.ad";
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        final Presentation presentation = event.getPresentation();
        final Project project = event.getData(CommonDataKeys.PROJECT);
        presentation.setEnabled(true);
        if (project == null || project.isDisposed()) {
            presentation.setEnabledAndVisible(false);
            return;
        }
        if (getDefaultExecutor() == null) {
            presentation.setEnabledAndVisible(false);
            return;
        }
        presentation.setEnabledAndVisible(true);
    }

    @Override
    public boolean isDumbAware() {
        return true;
    }
}