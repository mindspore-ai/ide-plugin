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

import com.intellij.ide.util.projectWizard.AbstractNewProjectStep;
import com.intellij.ide.util.projectWizard.CustomStepProjectGenerator;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.impl.welcomeScreen.AbstractActionWithPanel;
import com.intellij.platform.DirectoryProjectGenerator;
import com.jetbrains.python.newProject.PyNewProjectSettings;
import com.jetbrains.python.newProject.PythonProjectGenerator;
import com.jetbrains.python.packaging.PyCondaPackageService;
import com.jetbrains.python.remote.PyProjectSynchronizer;
import com.jetbrains.python.sdk.PyLazySdk;
import com.mindspore.ide.toolkit.common.beans.NormalInfoConstants;
import com.mindspore.ide.toolkit.common.enums.EnumProperties;
import com.mindspore.ide.toolkit.ui.wizard.WizardMsSettingProjectPeer;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * mindspore project generator
 *
 * @author hanguisen
 * @since 2022-1-3
 */
@Slf4j
public class MindSporeProjectGenerator extends PythonProjectGenerator<PyNewProjectSettings>
        implements CustomStepProjectGenerator<PyNewProjectSettings> {
    private static final String PROJECT_WIZARD_NAME =
            EnumProperties.MIND_SPORE_PROPERTIES.getProperty("project.wizard.name");

    private static final String PROJECT_WIZARD_DESCRIPTION =
            EnumProperties.MIND_SPORE_PROPERTIES.getProperty("project.wizard.description");

    private WizardMsSettingProjectPeer msSettingProjectPeer = new WizardMsSettingProjectPeer();

    @Override
    public AbstractActionWithPanel createStep(DirectoryProjectGenerator projectGenerator,
                                              AbstractNewProjectStep.AbstractCallback callback) {
        msSettingProjectPeer.initCondaMap(); // refresh exist interpreter
        msSettingProjectPeer.resetBrowserButton();
        return new CustomMSProjectStep(this, callback, msSettingProjectPeer);
    }

    @Override
    public String getDescription() {
        return PROJECT_WIZARD_DESCRIPTION;
    }

    @Override
    public
    @NotNull
    @Nls(capitalization = Nls.Capitalization.Title)
    String getName() {
        return PROJECT_WIZARD_NAME;
    }

    @Override
    public @Nullable Icon getLogo() {
        return NormalInfoConstants.MS_ICON_16PX;
    }

    @Override
    protected void configureProject(@NotNull Project project,
                                    @NotNull VirtualFile baseDir,
                                    @NotNull PyNewProjectSettings settings,
                                    @NotNull Module module,
                                    @Nullable PyProjectSynchronizer synchronizer) {
        super.configureProject(project, baseDir, settings, module, synchronizer);
        MindSporeService.createMindSporeTemplate(baseDir.getPresentableUrl(),
                msSettingProjectPeer.getTemplate());
        MindSporeService.createStructure(baseDir.getPresentableUrl());
        Sdk sdk = settings.getSdk();
        if (sdk instanceof PyLazySdk) {
            sdk = ((PyLazySdk) sdk).create();
        }
        log.info("generator configuration");
        log.info("sdk home path : {}", sdk.getHomePath());
        if (!MsCondaEnvService.isValid(sdk)) {
            return;
        } else {
            PyCondaPackageService.onCondaEnvCreated(msSettingProjectPeer.getCondaPath());
        }
        Task.WithResult installMindSporeTask = MindSporeService.installMindSporeTask(
                project,
                msSettingProjectPeer.getHardwareValue(),
                sdk);
        boolean isMindSporeInstalled = (Boolean) ProgressManager.getInstance().run(installMindSporeTask);
        if (!isMindSporeInstalled) {
            log.info("MindSpore install failed, check by validate");
            return;
        }
        boolean isNewSdk = msSettingProjectPeer.isUsingNewCondaEnv();
        Task.WithResult setSdkTask = MsCondaEnvService.setSdkTask(project, module, sdk, isNewSdk);
        Long sessionId = (Long) ProgressManager.getInstance().run(setSdkTask);
        log.info("session ID {}", sessionId);
    }

    @Override
    public
    @Nullable
    String getNewProjectPrefix() {
        return "mindspore";
    }
}