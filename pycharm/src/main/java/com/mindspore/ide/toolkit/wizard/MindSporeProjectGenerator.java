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
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.impl.welcomeScreen.AbstractActionWithPanel;
import com.intellij.platform.DirectoryProjectGenerator;
import com.jetbrains.python.newProject.PyNewProjectSettings;
import com.jetbrains.python.newProject.PythonProjectGenerator;
import com.jetbrains.python.remote.PyProjectSynchronizer;
import com.mindspore.ide.toolkit.common.beans.NormalInfoConstants;
import com.mindspore.ide.toolkit.common.dialog.DialogInfo;
import com.mindspore.ide.toolkit.common.dialog.DialogInfoListener;
import com.mindspore.ide.toolkit.common.enums.EnumHardWarePlatform;
import com.mindspore.ide.toolkit.common.events.EventCenter;
import com.mindspore.ide.toolkit.common.utils.PropertiesUtil;
import com.mindspore.ide.toolkit.ui.wizard.WizardMsSettingProjectPeer;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * mindspore project generator
 *
 * @since 2022-1-3
 * @author hanguisen
 */
@Slf4j
public class MindSporeProjectGenerator extends PythonProjectGenerator<PyNewProjectSettings>
        implements CustomStepProjectGenerator<PyNewProjectSettings> {
    private static final String PROJECT_WIZARD_NAME = PropertiesUtil.getProperty("project.wizard.name");

    private static final String PROJECT_WIZARD_DESCRIPTION = PropertiesUtil.getProperty("project.wizard.description");

    static {
        EventCenter.INSTANCE.subscribe(new DialogInfoListener());
    }

    private WizardMsSettingProjectPeer msSettingProjectPeer;

    private CustomMSProjectStep customMSProjectStep;

    @Override
    public AbstractActionWithPanel createStep(DirectoryProjectGenerator projectGenerator,
        AbstractNewProjectStep.AbstractCallback callback) {
        if (msSettingProjectPeer == null) {
            msSettingProjectPeer = new WizardMsSettingProjectPeer();
            customMSProjectStep = new CustomMSProjectStep(this, callback, msSettingProjectPeer);
        }
        msSettingProjectPeer.initCondaMap(); // refresh exist interpreter
        return customMSProjectStep;
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
        MindSporeServiceImpl.getInstance().createMindSporeTemplate(baseDir.getPresentableUrl(),
                msSettingProjectPeer.getTemplateSelector().getSelectedItem().toString());
        MindSporeServiceImpl.getInstance().createStructure(baseDir.getPresentableUrl());
        Task.WithResult condaEnv = new Task.WithResult<Sdk, Exception>(project, "create conda env", true) {
            @Override
            protected Sdk compute(@NotNull ProgressIndicator indicator) {
                if (msSettingProjectPeer.getNewEnvironmentUsingRadioButton().isSelected()) {
                    return MsCondaEnvService.newEnvironmentLocation(project,
                            module,
                            msSettingProjectPeer.getCondaPath(),
                            msSettingProjectPeer.getCondaEnvPath(),
                            msSettingProjectPeer.getPythonVersionCombo().getSelectedItem().toString());
                } else {
                    return MsCondaEnvService.exitingCondaEnvironment(project,
                            module,
                            msSettingProjectPeer.getCondaSdk(msSettingProjectPeer.getExistEnv()
                                    .getSelectedItem().toString()));
                }
            }
        };
        Sdk sdk = (Sdk) ProgressManager.getInstance().run(condaEnv);
        if (sdk == null || sdk.getHomePath() == null) {
            return;
        }
        Task.WithResult task = new Task.WithResult<Integer, Exception>(project,
                "Install MindSpore into conda", false) {
            @Override
            public Integer compute(@NotNull ProgressIndicator indicator) {
                DialogInfo dialogInfo = installMindSporeIntoConda(sdk);
                dialogInfo.setTitle("Install MindSpore into conda");
                EventCenter.INSTANCE.publish(dialogInfo);
                return dialogInfo.isSuccessful() ? 0 : -1;
            }
        };
        int result = (int) ProgressManager.getInstance().run(task);
        if (result != 0) {
            return;
        }
    }

    private DialogInfo installMindSporeIntoConda(Sdk sdk) {
        String hardwarePlatform = msSettingProjectPeer.getHardwareValue();
        List<String> cmdList;
        //conda这里需要完善获取命令的方法
        if (hardwarePlatform.contains(EnumHardWarePlatform.CPU.getCode())) {
            cmdList = Arrays.asList("install", String.format("mindspore-%s=1.5.0",
                    EnumHardWarePlatform.CPU.getCode().toLowerCase(Locale.ROOT)),
                    "-c", "mindspore", "-c", "conda-forge", "-y");
        } else if (hardwarePlatform.contains(EnumHardWarePlatform.GPU.getCode())) {
            String version = hardwarePlatform.split(" ")[2];
            cmdList = Arrays.asList("install", String.format("mindspore-%s=1.5.0",
                    EnumHardWarePlatform.GPU.getCode().toLowerCase(Locale.ROOT)),
                    String.format("cudatoolkit=%s", version), "cudnn", "-c", "mindspore",
                    "-c", "conda-forge", "-y");
        } else {
            cmdList = Arrays.asList("install", String.format("mindspore-%s=1.5.0",
                    EnumHardWarePlatform.ASCEND.getCode().toLowerCase(Locale.ROOT)),
                    "-c", "mindspore", "-c", "conda-forge", "-y");
        }
        return CondaCmdProcessor.executeCondaCmd(sdk, null, cmdList);
    }

    @Override
    public
    @Nullable
    String getNewProjectPrefix() {
        return "mindspore";
    }
}