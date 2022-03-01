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

package com.mindspore.ide.toolkit.wizard;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.projectRoots.Sdk;
import com.jetbrains.python.packaging.PyCondaPackageService;
import com.jetbrains.python.sdk.flavors.PyCondaRunKt;
import com.mindspore.ide.toolkit.common.dialoginfo.DialogInfo;
import com.mindspore.ide.toolkit.common.dialoginfo.ExceptionDialogInfo;
import com.mindspore.ide.toolkit.common.enums.EnumError;
import com.mindspore.ide.toolkit.common.exceptions.MsToolKitException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CondaCmdProcessor {
    private CondaCmdProcessor() {}

    /**
     * execute conda cmd
     *
     * @param sdk       sdk
     * @param condaPath conda exe path
     * @param cmdList   cmd info
     * @return dialog
     */
    public static DialogInfo executeCondaCmd(Sdk sdk, String condaPath, List<String> cmdList)
            throws MsToolKitException {
        if (sdk == null && (condaPath == null || condaPath.equals(""))) {
            throw new MsToolKitException(EnumError.CONDA_EXECUTABLE_NOT_SPECIFIED);
        }
        try {
            String command = String.join(" ", cmdList);
            if (sdk != null) {
                log.info("conda sdk:{}, exec:{}", sdk, PyCondaPackageService.getCondaExecutable(sdk.getHomePath()));
            }
            final ProcessOutput result = sdk == null ? PyCondaRunKt.runConda(condaPath, cmdList)
                    : PyCondaRunKt.runConda(sdk, cmdList);
            if (log.isInfoEnabled()) {
                log.info("execute conda command {}." + System.lineSeparator()
                                + "------command: {}" + System.lineSeparator()
                                + "------exitCode: {}" + System.lineSeparator()
                                + "------stdout: {}" + System.lineSeparator()
                                + "------stderr: {}",
                        result.getExitCode() == 0 ? "succeed" : "failed",
                        command,
                        result.getExitCode(),
                        result.getStdout(),
                        result.getStderr());
            }
            return new ExceptionDialogInfo.Builder()
                    .isSuccessful(result.getExitCode() == 0)
                    .command(command)
                    .output(result.getStdout())
                    .build();
        } catch (ExecutionException executionException) {
            log.warn("execute conda cmd failed.", executionException);
            return ExceptionDialogInfo.parseException(executionException, sdk);
        }
    }
}
