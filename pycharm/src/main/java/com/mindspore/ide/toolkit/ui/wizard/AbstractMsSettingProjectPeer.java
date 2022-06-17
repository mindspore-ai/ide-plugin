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
import com.mindspore.ide.toolkit.common.utils.OSInfoUtils;
//import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;

//@SuppressFBWarnings({"EI_EXPOSE_REP", "UWF_UNWRITTEN_FIELD"})
public abstract class AbstractMsSettingProjectPeer {
    protected JPanel mainPanel;
    protected JComboBox hardwareSelector;
    protected TextFieldWithBrowseButton condaExecutableTextField;
    protected JButton downloadMiniCondaButton;
    protected TextFieldWithBrowseButton condaEnvTextField;
    protected JRadioButton newEnvironmentUsingRadioButton;
    protected JRadioButton existingEnvironmentRadioButton;
    protected JComboBox existEnvSelector;
    protected JComboBox pythonVersionSelector;
    protected JComboBox templateSelector;
    protected JLabel osName;
    protected ButtonGroup envButtons;

    public AbstractMsSettingProjectPeer() {
        newEnvironmentUsingRadioButton.addChangeListener(this::changeEnvState);
        existingEnvironmentRadioButton.addChangeListener(this::changeEnvState);
        setOsToLabel();
    }

    private void changeEnvState(ChangeEvent actionEvent){
        condaEnvTextField.setEnabled(newEnvironmentUsingRadioButton.isSelected());
        pythonVersionSelector.setEnabled(newEnvironmentUsingRadioButton.isSelected());
        existEnvSelector.setEnabled(existingEnvironmentRadioButton.isSelected());
    }

    public abstract void addItemsToHardwareSelector();

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void setOsToLabel() {
        osName.setText(OSInfoUtils.INSTANCE.getOsName());
    }
}
