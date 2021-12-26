package com.mindspore.ide.toolkit.wizard;

import com.intellij.ide.util.projectWizard.AbstractNewProjectStep;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.platform.DirectoryProjectGenerator;
import com.jetbrains.python.newProject.steps.ProjectSpecificSettingsStep;
import com.mindspore.ide.toolkit.ui.wizard.WizardMsSettingProjectPeer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class CustomMSProjectStep extends ProjectSpecificSettingsStep {
    public WizardMsSettingProjectPeer getProjectPeer() {
        return projectPeer;
    }

    WizardMsSettingProjectPeer projectPeer ;
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
        if (projectPeer.getNewEnvironmentUsingRadioButton().isSelected() && Files.exists(Path.of(projectPeer.getCondaEnvPath()))) {
            setWarningText("Env dir is exist!");
            return false;
        }
        if (!projectPeer.getNewEnvironmentUsingRadioButton().isSelected() && projectPeer.getExistEnv().getSelectedItem() == null) {
            setWarningText("please choose a conda env!");
            return false;
        }
        return super.checkValid();
    }
}
