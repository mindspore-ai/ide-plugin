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

import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ex.ApplicationEx;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.platform.ProjectGeneratorPeer;
import com.intellij.util.containers.ContainerUtil;
import com.jetbrains.python.newProject.PyNewProjectSettings;
import com.jetbrains.python.packaging.PyCondaPackageService;
import com.jetbrains.python.sdk.PythonSdkUtil;
import com.mindspore.ide.toolkit.common.utils.FileUtils;
import com.mindspore.ide.toolkit.wizard.MsVersionManager;
import com.mindspore.ide.toolkit.wizard.MiniCondaService;
import com.mindspore.ide.toolkit.wizard.MSVersionInfo;
import com.mindspore.ide.toolkit.wizard.OSInfoUtils;
import com.mindspore.ide.toolkit.wizard.MindSporeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.ListCellRenderer;
import javax.swing.JList;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Slf4j
/**
 * project peer
 *
 * @since 1.0
 */
public class WizardMsSettingProjectPeer extends AbstractMsSettingProjectPeer implements ProjectGeneratorPeer {
    private static final String WINDOWS_CONDA_PARENT_PATH_NAME = "Scripts";

    private static final String LINUX_CONDA_PARENT_PATH_NAME = "bin";

    private static final String WINDOWS_CONDA_NAME = "conda.exe";

    private static final String LINUX_CONDA_NAME = "conda";

    public String getCondaPath() {
        return browseButton.getText();
    }

    private String condaPath;

    private JComboBox hardwareSelector = getHardwareSelector();

    private JLabel osName = getOsName();

    private JLabel pyVersionWarnLabel = getPyVersionWarnLabel();

    private JCheckBox templateCheckBox = getTemplateCheckBox();

    private JButton downloadMiniCondaButton = getDownloadMiniCondaButton();

    private TextFieldWithBrowseButton browseButton = getBrowseButton();

    private JLabel textJLabel = getTextJLabel();

    private TextFieldWithBrowseButton condaEnvBrowserButton = getCondaEnvBrowserButton();

    private MsVersionManager msVersionManager = MsVersionManager.INSTANCE;

    private Map<String, String> versionUrlMap;

    private MiniCondaService miniCondaService = new MiniCondaService();

    private HashMap<String, Sdk> condaMap = new HashMap<>();

    private SettingsListener settingsListener;

    private JComboBox templateSelector = getTemplateSelector();

    private String condaEnvPathAll = "";

    private HashSet<MSVersionInfo> hardwarePlatformSet;

    /**
     * construct for peer
     */
    public WizardMsSettingProjectPeer() {
        addMsProjectComboboxListener();
        addItemsToHardwareSelector();
        setCondExePath("");
        buttonListener();
        initCondaMap();
        initTemplate();
        pyVersionWarnLabel.setText("Python version must equal to 3.7.5 or 3.9.0");
    }

    /**
     * get conda env path
     *
     * @return conda env path
     */
    public String getCondaEnvPathAll() {
        return condaEnvPathAll;
    }

    /**
     * reset browser button
     */
    public void resetBrowserButton() {
        buttonListener();
    }

    /**
     * get conda sdk from maps
     *
     * @param key keys
     * @return sdk
     */
    public Sdk getCondaSdk(String key) {
        return condaMap.get(key);
    }

    @Override
    public void addSettingsListener(@NotNull SettingsListener listener) {
        settingsListener = listener;
        getNewEnvironmentUsingRadioButton().addActionListener(tListener -> settingsListener.stateChanged(false));
        getExistingEnvironmentRadioButton().addActionListener(tListener -> settingsListener.stateChanged(false));
        getExistEnv().addActionListener(tListener -> settingsListener.stateChanged(false));
        getCondaEnvBrowserButton().getTextField().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                settingsListener.stateChanged(false);
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                settingsListener.stateChanged(false);
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                settingsListener.stateChanged(false);
            }
        });
        getMainPanel().addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent event) {

            }

            @Override
            public void componentMoved(ComponentEvent event) {

            }

            @Override
            public void componentShown(ComponentEvent event) {
                settingsListener.stateChanged(false);
            }

            @Override
            public void componentHidden(ComponentEvent event) {

            }
        });
        settingsListener.stateChanged(false);
    }

    @Override
    public
    @NotNull
    JComponent getComponent() {
        return super.getMainPanel();
    }

    @Override
    public void buildUI(@NotNull SettingsStep settingsStep) {
    }

    @NotNull
    @Override
    public Object getSettings() {
        return new PyNewProjectSettings();
    }

    @Override
    public @Nullable ValidationInfo validate() {
        if (getNewEnvironmentUsingRadioButton().isSelected() && Files.exists(Path.of(getCondaEnvPath()))) {
            return new ValidationInfo("Env dir is exist!");
        }
        if (!getNewEnvironmentUsingRadioButton().isSelected() && getExistEnv().getSelectedItem() == null) {
            return new ValidationInfo("please choose a conda env!");
        }
        return null;
    }

    /**
     * is background job running
     *
     * @return true or false
     */
    @Override
    public boolean isBackgroundJobRunning() {
        return false;
    }

    /**
     * add item to hardware selector
     */
    @Override
    public void addItemsToHardwareSelector() {
        hardwarePlatformSet = MsVersionManager.INSTANCE.hardwarePlatformInfo();
        hardwareSelector.setModel(new DefaultComboBoxModel(hardwarePlatformSet.toArray(new MSVersionInfo[0])));
        hardwareSelector.setRenderer(new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean isCellHasFocus) {
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
            }
        });
        hardwareSelector.setSelectedIndex(0);
        if (hardwareSelector.getSelectedItem() instanceof MSVersionInfo) {
            addItemsToOsSelector(((MSVersionInfo) hardwareSelector.getSelectedItem()).getName());
        }
    }

    /**
     * add item to os selector
     *
     * @param parSelectStr select str
     */
    @Override
    public void addItemsToOsSelector(String parSelectStr) {
        Map<String, String> opVersionMap = msVersionManager.operatingSystemInfo(parSelectStr);
        if (opVersionMap == null || opVersionMap.size() == 0) {
            osName.setText("Unsupported operating system");
            return;
        }
        versionUrlMap = opVersionMap;
        Set<String> opVersionSet = opVersionMap.keySet();
        for (String opVersionString : opVersionSet) {
            osName.setText(opVersionString);
        }
    }

    /**
     * add ms project combox listener
     */
    public void addMsProjectComboboxListener() {
        hardwareSelector.addItemListener(event -> {
            if (event.getItem() instanceof String) {
                addItemsToOsSelector((String) event.getItem());
            }
        });
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
     * is created template
     *
     * @return true or false
     */
    public Boolean isCreateTemplate() {
        return templateCheckBox.isSelected();
    }

    /**
     * refresh interpreter
     */
    public void initCondaMap() {
        List<Sdk> condaList = ContainerUtil.filter(ProjectJdkTable.getInstance().getAllJdks(),
                PythonSdkUtil::isConda);
        condaMap.clear();
        condaList.forEach((conda) -> {
            condaMap.put(String.join(", ", new String[]{conda.getName(),
                    conda.getHomePath(), conda.getVersionString()}), conda);
        });
        getExistEnv().removeAllItems();
        condaMap.keySet().forEach(getExistEnv()::addItem);
    }

    private void buttonListener() {
        condaEnvBrowserButton.getButton().setEnabled(true);
        browseButton.getButton().setEnabled(true);
        if (condaEnvBrowserButton.getButton().getActionListeners().length > 0 || browseButton
                .getButton().getActionListeners().length > 0) {
            return;
        }
        FileChooserDescriptor condaEnvBrowserChooser = new FileChooserDescriptor(false,
                true, false, false, false, false);
        if (OSInfoUtils.isLinux()) {
            condaEnvBrowserChooser.setRoots(VirtualFileManager.getInstance()
                    .findFileByNioPath(Path.of(condaEnvPathAll)));
        }
        condaEnvBrowserButton.addBrowseFolderListener(new TextBrowseFolderListener(condaEnvBrowserChooser) {
            @Override
            @NotNull
            @NlsSafe
            protected String chosenFileToResultingText(@NotNull VirtualFile chosenFile) {
                if (chosenFile.findChild("mindspore") == null) {
                    return chosenFile.getPresentableUrl() + File.separator + "mindspore";
                }
                int suffix = 0;
                do {
                    suffix++;
                } while ((chosenFile.findChild("mindspore" + suffix) != null));
                String condaEnvPath = chosenFile.getPresentableUrl() + File.separator + "mindspore" + suffix;
                log.info("Select the conda env address path : {}", condaEnvPath);
                return condaEnvPath;
            }
        });
        browseButton.addBrowseFolderListener(new TextBrowseFolderListener(
                new FileChooserDescriptor(true, false, false,
                        false, false, false)) {
            @Override
            @NotNull
            @NlsSafe
            protected String chosenFileToResultingText(@NotNull VirtualFile chosenFile) {
                condaPath = chosenFile.getPath();
                log.info("Select the conda exe address path : {}", condaPath);
                initCondaEnvPath();
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
                    miniCondaService.dialogNotification(
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
        if (miniCondaService.downloadMiniConda(path)) {
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

    private void setCondExePath(String path) {
        // get conda exe path
        if (!path.equals("")) {
            if (OSInfoUtils.isWindows()) {
                String condaExePath = path + File.separator + "Miniconda3" + File.separator
                        + WINDOWS_CONDA_PARENT_PATH_NAME + File.separator + WINDOWS_CONDA_NAME;
                PyCondaPackageService.onCondaEnvCreated(condaExePath);
                condaPath = condaExePath;
                log.info("windows condaExePath:{}", condaExePath);
            } else {
                String condaExePath = path + File.separator + "Miniconda3" + File.separator
                        + LINUX_CONDA_PARENT_PATH_NAME + File.separator + LINUX_CONDA_NAME;
                PyCondaPackageService.onCondaEnvCreated(condaExePath);
                condaPath = condaExePath;
                log.info("other condaExePath:{}", condaExePath);
            }
        } else {
            condaPath = PyCondaPackageService.getCondaExecutable(null);
            log.info("First entry acquisition path : {}", condaPath);
        }
        initCondaEnvPath();
    }

    private void initCondaEnvPath() {
        log.info("condaPath:{}", condaPath);
        boolean isFile = FileUtils.isFile(condaPath);
        if (!StringUtil.isEmptyOrSpaces(condaPath) && isFile) {
            downloadMiniCondaButton.setEnabled(false);
            downloadMiniCondaButton.setVisible(false);
            browseButton.getTextField().setText(condaPath);
            File file = new File(condaPath);
            String condaEnvPath = file.getParent();
            if (condaEnvPath.endsWith(WINDOWS_CONDA_PARENT_PATH_NAME)
                    || condaEnvPath.endsWith(LINUX_CONDA_PARENT_PATH_NAME)) {
                File fileNew = new File(condaEnvPath);
                condaEnvPath = fileNew.getParent();
            }
            condaEnvPathAll = condaEnvPath + File.separator + "envs" + File.separator;
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
        String envPath = condaEnvPathAll + name;
        if (Files.exists(Path.of(envPath))) {
            int suffix = 0;
            do {
                suffix++;
            } while (Files.exists(Path.of(envPath + suffix)));
            envPath = envPath + suffix;
        }
        condaEnvBrowserButton.setText(envPath);
        validate();
    }

    /**
     * get conda env path
     *
     * @return conda env path
     */
    public String getCondaEnvPath() {
        if (getNewEnvironmentUsingRadioButton().isSelected()) {
            return condaEnvBrowserButton.getText();
        } else {
            return condaMap.get(getExistEnv().getSelectedItem().toString()).getHomePath();
        }
    }

    private void initTemplate() {
        templateSelector.addItem("<empty>");
        MindSporeServiceImpl.getInstance().listTemplates().stream().forEach(templateSelector::addItem);
    }
}