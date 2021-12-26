package com.mindspore.ide.toolkit.wizard;

import com.intellij.ide.util.projectWizard.AbstractNewProjectStep;
import com.intellij.ide.util.projectWizard.CustomStepProjectGenerator;
import com.intellij.ide.util.projectWizard.WebProjectTemplate;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.impl.welcomeScreen.AbstractActionWithPanel;
import com.intellij.platform.DirectoryProjectGenerator;
import com.mindspore.ide.toolkit.common.beans.NormalInfoConstants;
import com.mindspore.ide.toolkit.common.enums.EnumHardWarePlatform;
import com.mindspore.ide.toolkit.common.utils.PropertiesUtil;
import com.mindspore.ide.toolkit.ui.wizard.WizardMsSettingProjectPeer;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class MindSporeProjectGenerator extends WebProjectTemplate implements CustomStepProjectGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(MindSporeProjectGenerator.class);

    private static final String PROJECT_WIZARD_NAME = PropertiesUtil.getProperty("project.wizard.name");

    private static final String PROJECT_WIZARD_DESCRIPTION = PropertiesUtil.getProperty("project.wizard.description");

    private WizardMsSettingProjectPeer msSettingProjectPeer = new WizardMsSettingProjectPeer();

    private CustomMSProjectStep customMSProjectStep;

    @Override
    public AbstractActionWithPanel createStep(DirectoryProjectGenerator projectGenerator, AbstractNewProjectStep.AbstractCallback callback) {
        customMSProjectStep = new CustomMSProjectStep(this, callback, msSettingProjectPeer);
        return customMSProjectStep;
    }

    @Override
    public String getDescription() {
        return PROJECT_WIZARD_DESCRIPTION;
    }

    public @NotNull @Nls(capitalization = Nls.Capitalization.Title) String getName() {
        return PROJECT_WIZARD_NAME;
    }

    @Override
    public @Nullable Icon getLogo() {
        return NormalInfoConstants.MS_ICON_16PX;
    }

    @Override
    public void generateProject(@NotNull Project project, @NotNull VirtualFile baseDir, @NotNull Object settings, @NotNull Module module) {
        MindSporeServiceImpl.getInstance().createMindSporeTemplate(baseDir.getPresentableUrl(), msSettingProjectPeer.getTemplateSelector().getSelectedItem().toString());
        MindSporeServiceImpl.getInstance().createStructure(baseDir.getPresentableUrl());
        Task.WithResult condaEnv = new Task.WithResult<Sdk, Exception>(project, "create conda env", true) {
            @Override
            protected Sdk compute(@NotNull ProgressIndicator indicator) {
                if (msSettingProjectPeer.getNewEnvironmentUsingRadioButton().isSelected()) {
                    return MsCondaFlow.newFlow(project, module, msSettingProjectPeer.getCondaPath(), msSettingProjectPeer.getCondaEnvPath(), msSettingProjectPeer.getPythonVersionCombo().getSelectedItem().toString());
                } else {
                    return MsCondaFlow.oldFlow(project, module, msSettingProjectPeer.getCondaSdk(msSettingProjectPeer.getExistEnv().getSelectedItem().toString()));
                }
            }
        };
        Sdk sdk = (Sdk) ProgressManager.getInstance().run(condaEnv);
        if (sdk == null || sdk.getHomePath() == null) {
            Messages.showErrorDialog("Conda environment creation failed. Please check network.", "Conda environment creation error");
            return;
        }
        Task.WithResult task = new Task.WithResult<Integer, Exception>(project, "Install MindSpore into conda", false) {
            @Override
            public Integer compute(@NotNull ProgressIndicator indicator) {
                return CondaCmdProcessor.parseCondaResponse(installMindSporeIntoConda(sdk), "Install MindSpore into conda");
            }
        };
        int result = (int) ProgressManager.getInstance().run(task);
        if (result != 0) {
            Messages.showErrorDialog("MindSpore installation failed.", "MindSpore installation error");
            return;
        }
    }

    private CondaCmdProcessor.CondaResponse installMindSporeIntoConda(Sdk sdk) {
        String hardwarePlatform = msSettingProjectPeer.getHardwareValue();
        List<String> cmdList;
        //conda这里需要完善获取命令的方法
        if (hardwarePlatform.contains(EnumHardWarePlatform.GPU.getCode())) {
            String version = hardwarePlatform.split(" ")[2];
            cmdList = Arrays.asList("install", String.format("mindspore-%s=1.5.0", EnumHardWarePlatform.GPU.getCode()), String.format("cudatoolkit=%s", version), "-c", "mindspore", "-c", "conda-forge", "-y");
        } else {
            cmdList = Arrays.asList("install", String.format("mindspore-%s=1.5.0", hardwarePlatform), "-c", "mindspore", "-c", "conda-forge", "-y");
        }
        return CondaCmdProcessor.executeCondaCmd(sdk, cmdList);
    }
}

