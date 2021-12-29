package com.mindspore.ide.toolkit.wizard;

import com.google.common.collect.Lists;
import com.intellij.execution.ExecutionException;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.jetbrains.python.sdk.*;
import com.jetbrains.python.sdk.flavors.CondaEnvSdkFlavor;
import com.jetbrains.python.sdk.flavors.PyCondaRunKt;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class MsCondaFlow {
    public static Sdk newFlow(Project project, Module module, String condaExecutor, String condaEnvPath, String version) {
        Path condaFile = Paths.get(condaEnvPath).getFileName();
        if(condaFile == null){
            return null;
        }
        String name = condaFile.toString();
        if (!Files.exists(Paths.get(condaEnvPath))) {
            CondaCmdProcessor.parseCondaResponse(createCondaEnv(condaExecutor, condaEnvPath, version), "Create conda environment");
        }
        ProjectJdkImpl sdk = new ProjectJdkImpl(name, PythonSdkType.getInstance());
        sdk.setHomePath(PythonSdkUtil.getPythonExecutable(condaEnvPath));
        sdk.setVersionString(version);
        PythonSdkAdditionalData additionalData = new PythonSdkAdditionalData(CondaEnvSdkFlavor.getInstance());
        sdk.setSdkAdditionalData(additionalData);
        oldFlow(project, module, sdk);
        return sdk;
    }

    public static Sdk oldFlow(Project project, Module module, Sdk sdk) {
        VirtualFileManager.getInstance().asyncRefresh(() -> {
            PySdkUtil.activateVirtualEnv(sdk);
            ProjectJdkTable.getInstance().addJdk(sdk);
            ProjectRootManager.getInstance(project).setProjectSdk(sdk);
            PySdkExtKt.setPythonSdk(project, sdk);
            PySdkExtKt.setPythonSdk(module, sdk);
        });
        return sdk;
    }

    private static CondaCmdProcessor.CondaResponse createCondaEnv(String condaExecutable, String condaEnvPath, String version) {
        if (condaExecutable == null || condaEnvPath == null || version == null) {
            log.info("create conda environment failed. condaExecutable: {}, condaEnvPath: {}, version: {}", condaExecutable, condaEnvPath, version);
            return new CondaCmdProcessor.CondaResponse(-1, "", "conda executable or env path or version is null", "");
        }
        List<String> cmdList = Arrays.asList("create", "-p", condaEnvPath, "-y", "python=" + version);
        return CondaCmdProcessor.executeCondaCmd(condaExecutable, cmdList);
    }
}
