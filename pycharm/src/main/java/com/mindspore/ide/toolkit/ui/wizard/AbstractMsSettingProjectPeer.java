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

package com.mindspore.ide.toolkit.ui.wizard;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;

@SuppressFBWarnings({"EI_EXPOSE_REP", "UWF_UNWRITTEN_FIELD"})
public abstract class AbstractMsSettingProjectPeer {
    private JPanel mainPanel;
    private JLabel hardwareLabel;
    private JComboBox hardwareSelector;
    private JCheckBox templateCheckBox;
    private JLabel pyVersionWarnLabel;
    private JLabel templateLabel;
    private JLabel osLabel;
    private TextFieldWithBrowseButton browseButton;
    private JButton downloadMiniCondaButton;
    private JLabel textJLabel;
    private TextFieldWithBrowseButton condaEnvBrowserButton;
    private JRadioButton newEnvironmentUsingRadioButton;
    private JRadioButton existingEnvironmentRadioButton;
    private JComboBox existEnv;
    private JComboBox pythonVersionCombo;
    private JLabel pythonVersion;
    private JComboBox templateSelector;
    private JLabel pipLabel;
    private JLabel osName;

    public AbstractMsSettingProjectPeer() {
        newEnvironmentUsingRadioButton.addChangeListener(this::changeEnvState);
        existingEnvironmentRadioButton.addChangeListener(this::changeEnvState);
    }

    private void changeEnvState(ChangeEvent actionEvent){
        condaEnvBrowserButton.setEnabled(newEnvironmentUsingRadioButton.isSelected());
        pythonVersionCombo.setEnabled(newEnvironmentUsingRadioButton.isSelected());
        existEnv.setEnabled(existingEnvironmentRadioButton.isSelected());
    }

    public ButtonGroup getEnvButtons() {
        return envButtons;
    }

    private ButtonGroup envButtons;

    public JComboBox getExistEnv() {
        return existEnv;
    }

    public abstract void addItemsToHardwareSelector();

    public JRadioButton getNewEnvironmentUsingRadioButton() {
        return newEnvironmentUsingRadioButton;
    }

    public JRadioButton getExistingEnvironmentRadioButton() {
        return existingEnvironmentRadioButton;
    }

    public abstract void addItemsToOsSelector(String parSelectStr);

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JComboBox getHardwareSelector() {
        return hardwareSelector;
    }

    public JCheckBox getTemplateCheckBox() {
        return templateCheckBox;
    }

    public JLabel getPyVersionWarnLabel() {
        return pyVersionWarnLabel;
    }

    public JLabel getHardwareLabel() {
        return hardwareLabel;
    }

    public JLabel getTemplateLabel() {
        return templateLabel;
    }

    public JLabel getOsLabel() {
        return osLabel;
    }

    public JButton getDownloadMiniCondaButton() {
        return downloadMiniCondaButton;
    }

    public TextFieldWithBrowseButton getBrowseButton() {
        return browseButton;
    }

    public JLabel getTextJLabel() {
        return textJLabel;
    }

    public TextFieldWithBrowseButton getCondaEnvBrowserButton() {
        return condaEnvBrowserButton;
    }

    public JComboBox getPythonVersionCombo() {
        return pythonVersionCombo;
    }

    public JComboBox getTemplateSelector() {
        return templateSelector;
    }

    public JLabel getOsName() {
        return osName;
    }
}
