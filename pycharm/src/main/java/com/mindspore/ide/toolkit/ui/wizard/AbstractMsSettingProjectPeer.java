package com.mindspore.ide.toolkit.ui.wizard;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;

import javax.swing.*;
import javax.swing.event.ChangeEvent;

public abstract class AbstractMsSettingProjectPeer {
    private JPanel mainPanel;
    private JLabel hardwareLabel;
    private JComboBox hardwareSelector;
    private JComboBox OsSelector;
    private JCheckBox templateCheckBox;
    private JLabel pyVersionWarnLabel;
    private JLabel templateLabel;
    private JLabel OsLabel;
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
    private JLabel PipLabel;

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

    public JComboBox getOsSelector() {
        return OsSelector;
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
        return OsLabel;
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
}
