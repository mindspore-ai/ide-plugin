package com.mindspore.ide.toolkit.ui.wizard;

import com.intellij.ide.util.projectWizard.SettingsStep;
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
import com.intellij.platform.ProjectGeneratorPeer;
import com.intellij.util.ArrayUtilRt;
import com.intellij.util.containers.ContainerUtil;
import com.jetbrains.python.newProject.PyNewProjectSettings;
import com.jetbrains.python.packaging.PyCondaPackageService;
import com.jetbrains.python.sdk.PythonSdkUtil;
import com.mindspore.ide.toolkit.common.utils.FileUtils;
import com.mindspore.ide.toolkit.wizard.MindSporeServiceImpl;
import com.mindspore.ide.toolkit.wizard.MiniCondaService;
import com.mindspore.ide.toolkit.wizard.MsVersionManager;
import com.mindspore.ide.toolkit.wizard.OSInfoUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class WizardMsSettingProjectPeer extends AbstractMsSettingProjectPeer implements ProjectGeneratorPeer {
    public String getCondaPath() {
        return browseButton.getText();
    }

    private String condaPath;

    private String condaEnv;

    private JComboBox hardwareSelector = getHardwareSelector();

    private JComboBox osSelector = getOsSelector();

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

    public WizardMsSettingProjectPeer() {
        addMsProjectComboboxListener();
        addItemsToHardwareSelector();
        addDownloadMiniCondaButton("");
        buttonListener();
        initCondaMap();
        initTemplate();
        pyVersionWarnLabel.setText("Python version must equal to 3.7.5 or 3.9.0");
    }

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
            public void insertUpdate(DocumentEvent e) {
                settingsListener.stateChanged(false);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                settingsListener.stateChanged(false);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                settingsListener.stateChanged(false);
            }
        });
        getMainPanel().addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {

            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {
                settingsListener.stateChanged(false);
            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });
        settingsListener.stateChanged(false);
    }

    @Override
    public @NotNull JComponent getComponent() {
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

    @Override
    public boolean isBackgroundJobRunning() {
        return false;
    }

    @Override
    public void addItemsToHardwareSelector() {
        Set<String> hardwarePlatformSet = msVersionManager.hardwarePlatformInfo();
        hardwareSelector.setModel(new DefaultComboBoxModel(ArrayUtilRt.toStringArray(hardwarePlatformSet)));
        hardwareSelector.setSelectedIndex(0);
        osSelector.removeAllItems();
        if (hardwareSelector.getSelectedItem() instanceof String) {
            addItemsToOsSelector((String) hardwareSelector.getSelectedItem());
        }
    }

    @Override
    public void addItemsToOsSelector(String parSelectStr) {
        Map<String, String> opVersionMap = msVersionManager.operatingSystemInfo(parSelectStr);
        if (opVersionMap == null || opVersionMap.size() == 0) {
            return;
        }
        versionUrlMap = opVersionMap;
        Set<String> opVersionSet = opVersionMap.keySet();
        for (String opVersionString : opVersionSet) {
            osSelector.addItem(opVersionString);
        }
        osSelector.setSelectedIndex(0);
    }

    public void addMsProjectComboboxListener() {
        hardwareSelector.addItemListener(event -> {
            osSelector.removeAllItems();
            if (event.getItem() instanceof String) {
                addItemsToOsSelector((String) event.getItem());
            }
        });
    }

    public String getCurrentSystemUrl() {
        if (versionUrlMap == null) {
            return "";
        }
        return versionUrlMap.get(osSelector.getSelectedItem());
    }

    public String getHardwareValue() {
        if (hardwareSelector.getSelectedItem() instanceof String) {
            return (String) hardwareSelector.getSelectedItem();
        }
        return "";
    }

    public String getOsValue() {
        if (osSelector.getSelectedItem() instanceof String) {
            return (String) osSelector.getSelectedItem();
        }
        return "";
    }

    public Boolean isCreateTemplete() {
        return templateCheckBox.isSelected();
    }

    private void initCondaMap() {
        List<Sdk> condaList = ContainerUtil.filter(ProjectJdkTable.getInstance().getAllJdks(), PythonSdkUtil::isConda);
        condaList.forEach((conda) -> {
            condaMap.put(String.join(", ", new String[]{conda.getName(), conda.getHomePath(), conda.getVersionString()}), conda);
        });
        condaMap.keySet().forEach(getExistEnv()::addItem);
    }

    private void buttonListener() {
        condaEnvBrowserButton.addBrowseFolderListener(new TextBrowseFolderListener(new FileChooserDescriptor(false, true, false, false, false, false)) {
            @Override
            protected @NotNull @NlsSafe String chosenFileToResultingText(@NotNull VirtualFile chosenFile) {
                if (chosenFile.findChild("mindspore") == null) {
                    return chosenFile.getPresentableUrl() + File.separator + "mindspore";
                }
                int suffix = 0;
                do {
                    suffix++;
                } while ((chosenFile.findChild("mindspore" + suffix) != null));
                return chosenFile.getPresentableUrl() + File.separator + "mindspore" + suffix;
            }
        });
        browseButton.addBrowseFolderListener(new TextBrowseFolderListener(new FileChooserDescriptor(true, false, false, false, false, false)) {
            @Override
            protected @NotNull @NlsSafe String chosenFileToResultingText(@NotNull VirtualFile chosenFile) {
                return super.chosenFileToResultingText(chosenFile);
            }
        });
        downloadMiniCondaButton.addActionListener(actionEvent -> {
            MyDialog myDialog = new MyDialog();
            myDialog.setMyDialogListener(path -> {
                if (path.isEmpty()) {
                    miniCondaService.dialogNotification("Please select the conda download and installation path first.");
                } else {
                    myDialog.dispose();
                    if (miniCondaService.downloadMiniConda(path)) {
                        addDownloadMiniCondaButton(path);
                    } else {
                        Messages.showErrorDialog("Miniconda download installation failed. Please check network.","Miniconda installation failed");
                    }
                }
            });
            myDialog.setVisible(true);
        });
    }

    private void addDownloadMiniCondaButton(String path) {
        // get conda exe path
        if (!path.equals("")) {
            if (OSInfoUtils.isWindows()) {
                String condaExePath = path + File.separator + "Miniconda3" + File.separator + "Scripts" + File.separator + "conda.exe";
                PyCondaPackageService.onCondaEnvCreated(condaExePath);
                condaPath = condaExePath;
                log.info("condaPath:{}", condaPath);
            } else {
                condaPath = PyCondaPackageService.getCondaExecutable(null);
            }
        } else {
            condaPath = PyCondaPackageService.getCondaExecutable(null);
        }
        log.info("condaPath:{}", condaPath);
        boolean isFile = FileUtils.isFile(condaPath);
        if (!StringUtil.isEmptyOrSpaces(condaPath) && isFile) {
            downloadMiniCondaButton.setEnabled(false);
            downloadMiniCondaButton.setVisible(false);
            browseButton.getTextField().setText(condaPath);
            File file = new File(condaPath);
            String condaEnvPath = file.getParent();
            if (condaEnvPath.endsWith("Scripts")) {
                File fileNew = new File(condaEnvPath);
                condaEnvPath = fileNew.getParent();
            }
            String condaEnvPathAll = condaEnvPath + File.separator + "envs" + File.separator + "mindspore";
            if (Files.exists(Path.of(condaEnvPathAll))) {
                int suffix = 0;
                do {
                    suffix++;
                } while (Files.exists(Path.of(condaEnvPathAll + suffix)));
                condaEnvPathAll = condaEnvPathAll + suffix;
            }
            condaEnvBrowserButton.setText(condaEnvPathAll);
        } else {
            downloadMiniCondaButton.setVisible(true);
            downloadMiniCondaButton.setEnabled(true);
        }
    }

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