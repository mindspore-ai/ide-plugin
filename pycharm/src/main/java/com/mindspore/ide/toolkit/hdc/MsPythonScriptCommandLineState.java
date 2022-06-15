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

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.jetbrains.python.run.CommandLinePatcher;
import com.jetbrains.python.run.PythonRunConfiguration;
import com.jetbrains.python.run.PythonScriptCommandLineState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

/**
 * MsPythonScriptCommandLineState
 *
 * @since 2022-04-18
 */
public class MsPythonScriptCommandLineState extends PythonScriptCommandLineState {
    private Project project;

    /**
     * construction method
     *
     * @param runConfiguration runConfiguration
     * @param env              env
     * @param project          project
     */
    public MsPythonScriptCommandLineState(PythonRunConfiguration runConfiguration,
            ExecutionEnvironment env, Project project) {
        super(runConfiguration, env);
        this.project = project;
    }

    @Override
    @Nullable
    public ExecutionResult execute(Executor executor, PythonProcessStarter processStarter,
            CommandLinePatcher... patchers) throws ExecutionException {
        ExecutionResult result = super.execute(executor, processStarter, patchers);
        if (result != null) {
            ProcessHandler processHandler = result.getProcessHandler();
            if (processHandler instanceof OSProcessHandler) {
                OSProcessHandler osProcessHandler = (OSProcessHandler) processHandler;
                osProcessHandler.startNotify();
                ProcessTerminatedListener.attach(osProcessHandler);
                List<String> stringAllList = new LinkedList<>();
                osProcessHandler.addProcessListener(new ProcessAdapter() {
                    @Override
                    public void startNotified(@NotNull ProcessEvent event) {
                        super.startNotified(event);
                    }

                    @Override
                    public void processTerminated(@NotNull ProcessEvent event) {
                        showErrorDialog(stringAllList, project);
                        super.processTerminated(event);
                    }

                    @Override
                    public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                        stringAllList.add(event.getText());
                        super.onTextAvailable(event, outputType);
                    }
                });
            }
        }
        return result;
    }

    private void showErrorDialog(List<String> stringAllList, Project project) {
        if (stringAllList.size() > 0) {
            List<String> stringList = HdcStringUtils.allListToErrorList(stringAllList);
            if (stringList.size() >= 6) {
                ErrorDataInfo errorDataInfo = HdcStringUtils.errorListToErrorDataInfo(stringList);
                ErrorDialog errorDialog = new ErrorDialog(errorDataInfo, project);
                errorDialog.setVisible(true);
            }
        }
    }
}