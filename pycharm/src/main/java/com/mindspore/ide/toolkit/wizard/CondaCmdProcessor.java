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
import com.intellij.webcore.packaging.PackageManagementService;
import com.jetbrains.python.packaging.ui.PyPackageManagementService;
import com.jetbrains.python.sdk.flavors.PyCondaRunKt;
import com.mindspore.ide.toolkit.common.dialog.DialogInfo;
import com.mindspore.ide.toolkit.ui.errordialog.CmdDialogInfo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class CondaCmdProcessor {
    /**
     * execute conda cmd
     *
     * @param sdk sdk
     * @param condaPath conda exe path
     * @param cmdList cmd info
     * @return dialog
     */
    public static DialogInfo executeCondaCmd(Sdk sdk, String condaPath, List<String> cmdList) {
        try {
            String command = String.join(" ", cmdList);
            final ProcessOutput result = sdk == null ? PyCondaRunKt.runConda(condaPath, cmdList)
                    : PyCondaRunKt.runConda(sdk, cmdList);
            if (log.isDebugEnabled()) {
                log.info("execute conda cmd {}{}." + System.lineSeparator() + "------exitCode: {}"
                                + System.lineSeparator() + "------stdout: {}"
                                + System.lineSeparator() + "------stderr: {}",
                        command,
                        result.getExitCode() == 0 ? "succeed" : "failed",
                        result.getExitCode(), result.getStdout(), result.getStderr());
            }
            return parseProcessOutput(result, command);
        } catch (ExecutionException executionException) {
            log.error("execute conda cmd failed.", executionException);
            return parseExecutionException(executionException, sdk);
        }
    }

    private static DialogInfo parseExecutionException(ExecutionException executionException, Sdk sdk) {
        PackageManagementService.ErrorDescription errorDescription =
                PyPackageManagementService.toErrorDescription(Arrays.asList(executionException), sdk);

        return new CmdDialogInfo.Builder().isSuccessful(false)
                .description(errorDescription.getMessage())
                .command(errorDescription.getCommand())
                .output(errorDescription.getOutput())
                .solution(errorDescription.getSolution() == null
                        ? "Please check your network." : errorDescription.getSolution())
                .build();
    }

    private static DialogInfo parseProcessOutput(@NotNull ProcessOutput processOutput, String command) {
        return new CmdDialogInfo.Builder().isSuccessful(processOutput.getExitCode() == 0)
                .command(command)
                .output(processOutput.getStdout())
                .build();
    }
}
