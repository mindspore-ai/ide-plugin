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
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.jetbrains.python.sdk.PyLazySdk;
import com.jetbrains.python.sdk.PySdkExtKt;
import com.jetbrains.python.sdk.PySdkUtil;
import com.jetbrains.python.sdk.PythonSdkAdditionalData;
import com.jetbrains.python.sdk.PythonSdkType;
import com.jetbrains.python.sdk.PythonSdkUpdater;
import com.jetbrains.python.sdk.PythonSdkUtil;
import com.jetbrains.python.sdk.flavors.CondaEnvSdkFlavor;
import com.mindspore.ide.toolkit.common.dialoginfo.DialogInfo;
import com.mindspore.ide.toolkit.common.dialoginfo.ExceptionDialogInfo;
import com.mindspore.ide.toolkit.common.exceptions.MsToolKitException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

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
     * @param condaExecutor conda path
     * @param condaEnvPath  conda env path
     * @param version       python version
     * @return sdk
     */
    public static Sdk newSdk(String condaExecutor, String condaEnvPath, String version) {
        String name = Paths.get(condaEnvPath).getFileName().toString();
        if (!Files.exists(Paths.get(condaEnvPath))) {
            try {
                CondaCmdProcessor.executeCondaCmd(null, condaExecutor, Arrays.asList("env", "list", "-q"));
                DialogInfo dialogInfo = createCondaEnv(condaExecutor, condaEnvPath, version);
                dialogInfo.showDialog("Create conda environment");
            } catch (MsToolKitException msToolKitException) {
                ExceptionDialogInfo.parseException(msToolKitException).showDialog("Create conda environment");
            }
        }
        ProjectJdkImpl sdk = new ProjectJdkImpl(name, PythonSdkType.getInstance());
        sdk.setHomePath(PythonSdkUtil.getPythonExecutable(condaEnvPath));
        PythonSdkAdditionalData additionalData = new PythonSdkAdditionalData(CondaEnvSdkFlavor.getInstance());
        sdk.setSdkAdditionalData(additionalData);
        return sdk;
    }

    /**
     * lazy sdk for create step
     *
     * @param condaExecutor conda executable
     * @param condaEnvPath conda environment
     * @param version python version
     * @return lazy sdk
     */
    public static PyLazySdk newLazySdk(String condaExecutor, String condaEnvPath, String version) {
        String name = Paths.get(condaEnvPath).getFileName().toString();
        return new PyLazySdk(name, () -> {
            Task.WithResult newSdkTask = newSdkTask(condaExecutor, condaEnvPath, version);
            return (Sdk) ProgressManager.getInstance().run(newSdkTask);
        });
    }

    /**
     * create a task to create new env
     *
     * @param condaExecutor conda executable path
     * @param condaEnvPath  conda env path
     * @param version       python version
     * @return Task.WithResult
     */
    public static Task.WithResult<Sdk, Exception> newSdkTask(String condaExecutor, String condaEnvPath,
                                                             String version) {
        return new Task.WithResult(null, "Createconda environment", true) {
            @Override
            protected Sdk compute(@NotNull ProgressIndicator indicator) {
                return MsCondaEnvService.newSdk(
                        condaExecutor,
                        condaEnvPath,
                        version);
            }
        };
    }

    /**
     * get exist conda environment
     *
     * @param project  current project
     * @param module   current module
     * @param sdk      sdk
     * @param isNewSdk is new sdk
     * @return sdk
     */
    public static Long setSdk(Project project, Module module, Sdk sdk, boolean isNewSdk) {
        PythonSdkUpdater.updateVersionAndPathsSynchronouslyAndScheduleRemaining(sdk, project);
        return VirtualFileManager.getInstance().asyncRefresh(() -> {
            PySdkUtil.activateVirtualEnv(sdk);
            if (isNewSdk) {
                ProjectJdkTable.getInstance().addJdk(sdk);
            }
            ProjectRootManager.getInstance(project).setProjectSdk(sdk);
            PySdkExtKt.setPythonSdk(project, sdk);
            PySdkExtKt.setPythonSdk(module, sdk);
        });
    }

    /**
     * create a task to do setSdk
     *
     * @param project  project
     * @param module   module
     * @param sdk      sdk
     * @param isNewSdk is sdk new
     * @return task
     */
    public static Task.WithResult<Long, Exception> setSdkTask(Project project, Module module, Sdk sdk,
                                                              boolean isNewSdk) {
        return new Task.WithResult(project, "Refresh Sdk", true) {
            @Override
            protected Long compute(@NotNull ProgressIndicator indicator) {
                return MsCondaEnvService.setSdk(
                        project,
                        module,
                        sdk,
                        isNewSdk);
            }
        };
    }

    /**
     * determine if sdk created successfully
     *
     * @param sdk sdk
     * @return is valid
     */
    public static boolean isValid(Sdk sdk) {
        return !(sdk == null || sdk.getHomePath() == null);
    }

    private static DialogInfo createCondaEnv(String condaExecutable, String condaEnvPath, String version)
            throws MsToolKitException {
        if (condaExecutable == null || condaEnvPath == null || version == null) {
            log.info("create conda environment failed. condaExecutable: {}, condaEnvPath: {}, version: {}",
                    condaExecutable, condaEnvPath, version);
            return new ExceptionDialogInfo.Builder().isSuccessful(false)
                    .description("conda executable or env path or version is null")
                    .output("")
                    .build();
        }
        List<String> cmdList = Arrays.asList("create", "-p", condaEnvPath, "-y", "python=" + version);
        return CondaCmdProcessor.executeCondaCmd(null, condaExecutable, cmdList);
    }
}
