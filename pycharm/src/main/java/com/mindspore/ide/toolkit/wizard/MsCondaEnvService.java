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

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.jetbrains.python.sdk.PythonSdkType;
import com.jetbrains.python.sdk.PythonSdkUtil;
import com.jetbrains.python.sdk.PythonSdkAdditionalData;
import com.jetbrains.python.sdk.PySdkUtil;
import com.jetbrains.python.sdk.PySdkExtKt;
import com.jetbrains.python.sdk.flavors.CondaEnvSdkFlavor;
import com.mindspore.ide.toolkit.common.dialog.DialogInfo;
import com.mindspore.ide.toolkit.common.events.EventCenter;
import com.mindspore.ide.toolkit.ui.errordialog.CmdDialogInfo;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * conda env service
 *
 * @since 1.0
 */
@Slf4j
public class MsCondaEnvService {
    /**
     * new environment location
     *
     * @param project current project
     * @param module current module
     * @param condaExecutor conda path
     * @param condaEnvPath conda env path
     * @param version python version
     * @return sdk
     */
    public static Sdk newEnvironmentLocation(Project project, Module module,
        String condaExecutor, String condaEnvPath, String version) {
        String name = Paths.get(condaEnvPath).getFileName().toString();
        if (!Files.exists(Paths.get(condaEnvPath))) {
            CondaCmdProcessor.executeCondaCmd(null, condaExecutor, Arrays.asList("env", "list", "-q"));
            DialogInfo dialogInfo = createCondaEnv(condaExecutor, condaEnvPath, version);
            dialogInfo.setTitle("Create conda environment");
            EventCenter.INSTANCE.publish(dialogInfo);
        }
        ProjectJdkImpl sdk = new ProjectJdkImpl(name, PythonSdkType.getInstance());
        sdk.setHomePath(PythonSdkUtil.getPythonExecutable(condaEnvPath));
        sdk.setVersionString(version);
        PythonSdkAdditionalData additionalData = new PythonSdkAdditionalData(CondaEnvSdkFlavor.getInstance());
        sdk.setSdkAdditionalData(additionalData);
        exitingCondaEnvironment(project, module, sdk, true);
        return sdk;
    }

    /**
     * get exist conda environment
     *
     * @param project current project
     * @param module current module
     * @param sdk sdk
     * @return sdk
     */
    public static Sdk exitingCondaEnvironment(Project project, Module module, Sdk sdk) {
        return exitingCondaEnvironment(project, module, sdk, true);
    }

    /**
     * get exist conda environment
     *
     * @param project current project
     * @param module current module
     * @param sdk sdk
     * @param isNewSdk is new sdk
     * @return sdk
     */
    public static Sdk exitingCondaEnvironment(Project project, Module module, Sdk sdk, boolean isNewSdk) {
        VirtualFileManager.getInstance().asyncRefresh(() -> {
            PySdkUtil.activateVirtualEnv(sdk);
            if (isNewSdk) {
                ProjectJdkTable.getInstance().addJdk(sdk);
            }
            ProjectRootManager.getInstance(project).setProjectSdk(sdk);
            PySdkExtKt.setPythonSdk(project, sdk);
            PySdkExtKt.setPythonSdk(module, sdk);
        });
        return sdk;
    }

    private static DialogInfo createCondaEnv(String condaExecutable, String condaEnvPath, String version) {
        if (condaExecutable == null || condaEnvPath == null || version == null) {
            log.info("create conda environment failed. condaExecutable: {}, condaEnvPath: {}, version: {}",
                    condaExecutable, condaEnvPath, version);
            return new CmdDialogInfo.Builder().isSuccessful(false)
                    .description("conda executable or env path or version is null")
                    .output("")
                    .build();
        }
        List<String> cmdList = Arrays.asList("create", "-p", condaEnvPath, "-y", "python=" + version);
        return CondaCmdProcessor.executeCondaCmd(null, condaExecutable, cmdList);
    }
}
