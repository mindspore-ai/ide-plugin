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
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.platform.DirectoryProjectGenerator;
import com.intellij.ui.DocumentAdapter;
import com.jetbrains.python.newProject.steps.ProjectSpecificSettingsStep;
import com.jetbrains.python.sdk.PythonSdkAdditionalData;
import com.jetbrains.python.sdk.PythonSdkType;
import com.jetbrains.python.sdk.PythonSdkUtil;
import com.jetbrains.python.sdk.flavors.CondaEnvSdkFlavor;
import com.mindspore.ide.toolkit.ui.wizard.WizardMsSettingProjectPeer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CustomMSProjectStep extends ProjectSpecificSettingsStep {
    public WizardMsSettingProjectPeer getProjectPeer() {
        return projectPeer;
    }

    private WizardMsSettingProjectPeer projectPeer ;

    /**
     * constructor
     *
     * @param projectGenerator project generator
     * @param callback call back
     * @param projectPeer peer
     */
    public CustomMSProjectStep(@NotNull DirectoryProjectGenerator projectGenerator, AbstractNewProjectStep.@NotNull AbstractCallback callback, WizardMsSettingProjectPeer projectPeer) {
        super(projectGenerator, callback);
        this.projectPeer = projectPeer;
    }

    @Override
    protected JPanel createBasePanel() {
        BorderLayout layout = new BorderLayout();
        JPanel locationPanel = new JPanel(layout);
        JPanel panel = new JPanel(new VerticalFlowLayout(0, 2));
        LabeledComponent<TextFieldWithBrowseButton> location = this.createLocationComponent();
        setProjectPath();
        locationPanel.add(location, "Center");
        panel.add(locationPanel);
        panel.add(projectPeer.getMainPanel());
        return panel;
    }

    @Override
    protected @Nullable JPanel createAdvancedSettings() {
        return null;
    }

    @Override
    public boolean checkValid() {
        if (OSInfoUtils.isLinux() && !projectPeer.getCondaEnvPath().contains(projectPeer.getCondaEnvPathAll())) {
            setWarningText("This environment path is invalid."
                    + " Please choose new environment location in Conda install path.");
            return false;
        }

        if (projectPeer.getNewEnvironmentUsingRadioButton().isSelected()
                && Files.exists(Path.of(projectPeer.getCondaEnvPath()))) {
            setWarningText("Env dir is exist!");
            return false;
        }
        if (!projectPeer.getNewEnvironmentUsingRadioButton().isSelected()
                && projectPeer.getExistEnv().getSelectedItem() == null) {
            setWarningText("please choose a conda env!");
            return false;
        }
        return super.checkValid();
    }

    @Override
    public
    @Nullable
    Sdk getSdk() {
        if (projectPeer.getNewEnvironmentUsingRadioButton().isSelected()) {
            return newFlow(projectPeer.getCondaEnvPath(),
                    projectPeer.getPythonVersionCombo().getSelectedItem().toString());
        } else {
            return projectPeer.getCondaSdk(projectPeer.getExistEnv().getSelectedItem().toString());
        }
    }

    /**
     * new flow
     *
     * @param condaEnvPath conda path
     * @param version python version
     * @return sdk
     */
    public static Sdk newFlow(String condaEnvPath, String version) {
        String name = Paths.get(condaEnvPath).getFileName().toString();
        ProjectJdkImpl sdk = new ProjectJdkImpl(name, PythonSdkType.getInstance());
        sdk.setHomePath(PythonSdkUtil.getPythonExecutable(condaEnvPath));
        sdk.setVersionString(version);
        PythonSdkAdditionalData additionalData = new PythonSdkAdditionalData(CondaEnvSdkFlavor.getInstance());
        sdk.setSdkAdditionalData(additionalData);
        return sdk;
    }

    private void setProjectPath() {
        myLocationField.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent event) {
                projectPeer.setCondaEnvPath(getProjectNameByOs(myLocationField.getText()));
            }
        });
        projectPeer.setCondaEnvPath(getProjectNameByOs(myLocationField.getText()));
    }

    private String getProjectNameByOs(String projectPath) {
        return Path.of(projectPath).getFileName().toString();
    }
}
