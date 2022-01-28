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

import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ex.ApplicationEx;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ContainerUtil;
import com.jetbrains.python.sdk.PythonSdkUtil;
import com.mindspore.ide.toolkit.common.utils.FileUtils;
import com.mindspore.ide.toolkit.common.utils.NotificationUtils;
import com.mindspore.ide.toolkit.common.utils.RegularUtils;
import com.mindspore.ide.toolkit.wizard.MSVersionInfo;
import com.mindspore.ide.toolkit.wizard.MindSporeService;
import com.mindspore.ide.toolkit.wizard.MiniCondaService;
import com.mindspore.ide.toolkit.wizard.MsVersionManager;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * ms wizard project peer
 *
 * @since 2021.12.15
 */
@Slf4j
public class WizardMsSettingProjectPeer extends AbstractMsSettingProjectPeer {
    private HashMap<String, Sdk> condaMap = new HashMap<>();

    private HashSet<MSVersionInfo> hardwarePlatformSet;

    /**
     * construct for peer
     */
    public WizardMsSettingProjectPeer() {
        super();
        addItemsToHardwareSelector();
        setCondExePath("");
        buttonListener();
        initCondaMap();
        initTemplateSelector();
    }

    /**
     * reset browser button
     */
    public void resetBrowserButton() {
        buttonListener();
    }

    /**
     * get conda path
     *
     * @return conda path
     */
    public String getCondaPath() {
        return RegularUtils.normalizeFilePath(condaExecutableTextField.getText());
    }

    /**
     * get conda version
     *
     * @return conda version
     */
    public String getPythonVersion() {
        return pythonVersionSelector.getSelectedItem().toString();
    }

    /**
     * is using new conda env
     *
     * @return true or false
     */
    public boolean isUsingNewCondaEnv() {
        return newEnvironmentUsingRadioButton.isSelected();
    }

    /**
     * get template
     *
     * @return template info
     */
    public String getTemplate() {
        return templateSelector.getSelectedItem().toString();
    }

    /**
     * get exist sdk
     *
     * @return sdk info
     */
    public Sdk getExistSdk() {
        return condaMap.get(existEnvSelector.getSelectedItem().toString());
    }

    /**
     * get exist sdk path
     *
     * @return exist string
     */
    public String getExistSdkString() {
        return existEnvSelector.getSelectedItem().toString();
    }

    /**
     * add item to hardware selector
     */
    @Override
    public void addItemsToHardwareSelector() {
        hardwarePlatformSet = MsVersionManager.INSTANCE.hardwarePlatformInfo();
        hardwareSelector.setModel(new DefaultComboBoxModel(hardwarePlatformSet.toArray(new MSVersionInfo[0])));
        hardwareSelector.setRenderer((list, value, index, isSelected, isCellHasFocus) -> {
            JPanel panel = new JPanel();
            if (value instanceof MSVersionInfo) {
                MSVersionInfo info = (MSVersionInfo) value;
                panel.setLayout(new BorderLayout());
                JLabel hardware = new JLabel(info.getName());
                JLabel desc = new JLabel(info.getDes());
                desc.setForeground(new Color(255, 180, 35));
                panel.add(hardware, BorderLayout.WEST);
                panel.add(desc, BorderLayout.EAST);
                if (isSelected) {
                    panel.setBackground(new Color(75, 110, 175));
                }
            }
            return panel;
        });
        hardwareSelector.setSelectedIndex(0);
    }

    /**
     * get hardware value
     *
     * @return hardware value
     */
    public String getHardwareValue() {
        if (hardwareSelector.getSelectedItem() instanceof MSVersionInfo) {
            return ((MSVersionInfo) hardwareSelector.getSelectedItem()).getName();
        }
        return "";
    }

    /**
     * refresh interpreter
     */
    public void initCondaMap() {
        condaMap.clear();
        List<Sdk> condaList = ContainerUtil.filter(ProjectJdkTable.getInstance().getAllJdks(),
                PythonSdkUtil::isConda);
        condaList.forEach((conda) -> condaMap.put(
                String.join(", ", new String[]{conda.getName(), conda.getHomePath(), conda.getVersionString()}),
                conda));
        existEnvSelector.removeAllItems();
        condaMap.keySet().forEach(existEnvSelector::addItem);
    }

    private void buttonListener() {
        condaEnvTextField.getButton().setEnabled(true);
        condaExecutableTextField.getButton().setEnabled(true);
        if (condaEnvTextField.getButton().getActionListeners().length > 0 || condaExecutableTextField
                .getButton().getActionListeners().length > 0) {
            return;
        }
        condaEnvTextField.addBrowseFolderListener(new TextBrowseFolderListener(
                FileChooserDescriptorFactory.createSingleFolderDescriptor()) {
            @Override
            @NotNull
            @NlsSafe
            protected String chosenFileToResultingText(@NotNull VirtualFile chosenFile) {
                String fileName = chosenFile.getPresentableUrl() + File.separator + "mindspore";
                String condaEnvPath = autoIncrementFileName(fileName);
                log.info("Select the conda env location path : {}", condaEnvPath);
                return condaEnvPath;
            }
        });
        condaExecutableTextField.addBrowseFolderListener(new TextBrowseFolderListener(
                new FileChooserDescriptor(FileChooserDescriptorFactory.createSingleFileDescriptor())) {
            @Override
            @NotNull
            @NlsSafe
            protected String chosenFileToResultingText(@NotNull VirtualFile chosenFile) {
                log.info("Select the conda exe address path : {}", chosenFile.getPath());
                initCondaEnvPath(chosenFile.getPath());
                return super.chosenFileToResultingText(chosenFile);
            }
        });
        addDownloadButtonListener();
    }

    private void addDownloadButtonListener() {
        downloadMiniCondaButton.addActionListener(actionEvent -> {
            CondaDownloadAndInstallDialog condaDownloadAndInstallDialog = new CondaDownloadAndInstallDialog();
            condaDownloadAndInstallDialog.setCondaDownloadAndInstallListener(path -> {
                if (path.isEmpty()) {
                    NotificationUtils.notify(NotificationUtils.NotifyGroup.NEW_PROJECT,
                            NotificationType.INFORMATION,
                            "Please select the conda download and installation path first.");
                } else {
                    condaDownloadAndInstallDialog.dispose();
                    downloadAction(path);
                }
            });
            condaDownloadAndInstallDialog.setVisible(true);
        });
    }

    private void downloadAction(String path) {
        if (MiniCondaService.downloadMiniCondaTask(path)) {
            int result = Messages.showYesNoDialog("Install MiniConda success，Please restart Ide!",
                    "restart ide", "Restart", "Cancel", Messages.getInformationIcon());
            setCondExePath(path);
            if (result == Messages.YES) {
                Application app = ApplicationManager.getApplication();
                if (app instanceof ApplicationEx) {
                    log.info("Restart IDE conda path : {}", path);
                    ((ApplicationEx) app).restart(true);
                }
            }
        } else {
            Messages.showErrorDialog("Miniconda download installation failed. Please check network.",
                    "Miniconda installation failed");
        }
    }

    /**
     * set conda exe path
     *
     * @param path conda exe path
     */
    public void setCondExePath(String path) {
        initCondaEnvPath(MiniCondaService.setCondaExePathToIde(path));
    }

    /**
     * init conda environment path
     *
     * @param condaPath some pre found path
     */
    public void initCondaEnvPath(String condaPath) {
        log.info("condaPath:{}", condaPath);
        boolean isFile = FileUtils.isFile(condaPath);
        if (!StringUtil.isEmptyOrSpaces(condaPath) && isFile) {
            downloadMiniCondaButton.setEnabled(false);
            downloadMiniCondaButton.setVisible(false);
            condaExecutableTextField.getTextField().setText(condaPath);
            setCondaEnvPath("mindspore");
        } else {
            downloadMiniCondaButton.setVisible(true);
            downloadMiniCondaButton.setEnabled(true);
        }
    }

    /**
     * set conda env path
     *
     * @param projectName project name
     */
    public void setCondaEnvPath(final String projectName) {
        String name = projectName;
        if (projectName.contains(":")) {
            name = projectName.replace(":", "");
        }
        String envPath = MiniCondaService.getCondaEnvsPath(getCondaPath()) + name;
        condaEnvTextField.setText(autoIncrementFileName(envPath));
    }

    private String autoIncrementFileName(String fileName) {
        if (Files.exists(Path.of(fileName))) {
            int suffix = 0;
            do {
                suffix++;
            } while (Files.exists(Path.of(fileName + suffix)));
            return fileName + suffix;
        }
        return fileName;
    }

    /**
     * get conda env path
     *
     * @return conda env path
     */
    public String getCondaEnvPath() {
        if (newEnvironmentUsingRadioButton.isSelected()) {
            return RegularUtils.normalizeFilePath(condaEnvTextField.getText());
        } else {
            return condaMap.get(existEnvSelector.getSelectedItem().toString()).getHomePath();
        }
    }

    private void initTemplateSelector() {
        templateSelector.addItem("<empty>");
        MindSporeService.listTemplates().stream().forEach(templateSelector::addItem);
    }
}