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

import com.intellij.execution.process.BaseProcessHandler;
import com.intellij.execution.process.OSProcessHandler;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
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
public class Communicator {
    private OutputStream outputStream;
    private InputStream inputStream;
    private BufferedReader reader;
    private HdcPythonScriptCommandLineState pythonScriptCommandLineState;
    private int timeout = 10;
    private boolean isInit = false;
    private BlockingQueue blockingQueue = new LinkedBlockingQueue(100);
    private ThreadPoolExecutor threadPool;

    public Communicator(HdcPythonScriptCommandLineState pythonScriptCommandLineState) {
        this.pythonScriptCommandLineState = pythonScriptCommandLineState;
        threadPool = new ThreadPoolExecutor(2, 64, 60L,
                TimeUnit.SECONDS, blockingQueue);
    }

    /**
     * 输入搜索
     *
     * @param request 输入值
     * @return 返回值
     */
    public Optional<String> query(String request) {
        try {
            if (!isInit) {
                init();
            }
            outputStream.write((request + File.separator).getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            inputStream.available();
            Future<String> future = threadPool.submit(() -> reader.readLine());
            String result = future.get(timeout, TimeUnit.SECONDS);
            while (!result.startsWith("final_result:")) {
                result = reader.readLine();
            }
            result = result.replace("final_result：", "");
            return Optional.of(result);
        } catch (IOException | ExecutionException | InterruptedException | TimeoutException exception) {
            return Optional.empty();
        }
    }

    private void init() throws ExecutionException, InterruptedException, TimeoutException {
        Future<Boolean> future = threadPool.submit(() -> {
            while (pythonScriptCommandLineState.getProcessHandler() == null) {
                pythonScriptCommandLineState.getProcessHandler();
            }
            return true;
        });
        boolean isRes = future.get(timeout, TimeUnit.SECONDS);
        if (pythonScriptCommandLineState.getProcessHandler() instanceof BaseProcessHandler) {
            BaseProcessHandler processHandler = (BaseProcessHandler) pythonScriptCommandLineState.getProcessHandler();
            outputStream = processHandler.getProcess().getOutputStream();
            inputStream = processHandler.getProcess().getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        } else if (pythonScriptCommandLineState.getProcessHandler() instanceof OSProcessHandler) {
            OSProcessHandler processHandler = (OSProcessHandler) pythonScriptCommandLineState.getProcessHandler();
            outputStream = processHandler.getProcess().getOutputStream();
            inputStream = processHandler.getProcess().getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            isInit = isRes;
        } else {
            pythonScriptCommandLineState.getProcessHandler();
        }
    }
}