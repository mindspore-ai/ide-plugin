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

import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.openapi.project.Project;
import com.jetbrains.python.run.PythonCommandLineState;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * 处理数据
 *
 * @since 2022-04-18
 */
public class ErrorDialogController {
    private BlockingQueue blockingQueue = new LinkedBlockingQueue(100);
    private ThreadPoolExecutor threadPool;

    /**
     * 数据处理
     *
     * @param runProfileState runProfileState
     * @param executor        executor
     * @param project         project
     */
    public void errorDialogController(RunProfileState runProfileState, Executor executor, Project project) {
        try {
            if (runProfileState instanceof PythonCommandLineState) {
                PythonCommandLineState pythonCommandLineState = (PythonCommandLineState) runProfileState;
                threadPool = new ThreadPoolExecutor(2, 64, 60L,
                        TimeUnit.SECONDS, blockingQueue);
                ExecutionResult executionResult = pythonCommandLineState.execute(executor,
                        pythonCommandLineState.getEnvironment().getRunner());
                ProcessHandler processHandler = executionResult.getProcessHandler();
                if (processHandler instanceof OSProcessHandler) {
                    OSProcessHandler osProcessHandler = (OSProcessHandler) processHandler;
                    InputStream inputStream = osProcessHandler.getProcess().getInputStream();
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    inputStream.available();
                    StringBuilder stringBuilder = new StringBuilder();
                    List<String> stringAllList = new LinkedList<>();
                    Future<String> future = threadPool.submit(() -> {
                        String line = " ";
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line).append(File.separator);
                            stringAllList.add(line);
                        }
                        if (stringAllList.size() > 0) {
                            ErrorDialog errorDialog =
                                    new ErrorDialog(HdcStringUtils
                                            .errorListToErrorDataInfo(HdcStringUtils
                                                    .allListToErrorList(stringAllList)), project);
                            errorDialog.setVisible(true);
                        }
                        return stringBuilder.toString();
                    });
                    String result = future.get(10, TimeUnit.MILLISECONDS);
                    ProcessTerminatedListener.attach(osProcessHandler);
                }
            }
        } catch (IOException | ExecutionException | InterruptedException
                | TimeoutException | com.intellij.execution.ExecutionException exception) {
            return;
        }
    }
}