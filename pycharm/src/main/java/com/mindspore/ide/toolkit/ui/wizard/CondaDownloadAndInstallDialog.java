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

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.SystemProperties;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * conda download and install dialog
 *
 * @since 1.0
 */
@Slf4j
public class CondaDownloadAndInstallDialog extends JDialog {
    JLabel firstLinePromptJLabel;
    JLabel secondLinePromptJLabel;
    TextFieldWithBrowseButton pathBrowseButton;
    JButton installButton;
    JLabel textJLabelNoPath;
    CondaDownloadAndInstallListener condaDownloadAndInstallListener;

    /**
     * construction method
     */
    public CondaDownloadAndInstallDialog() {
        this.setTitle("Install Miniconda Automatically");
        this.setModal(true);
        this.setSize(600, 250);
        setLocationRelativeTo(null);
        init();
        this.setLayout(null);
    }

    /**
     * set listener
     *
     * @param condaDownloadAndInstallListener listener
     */
    public void setCondaDownloadAndInstallListener(CondaDownloadAndInstallListener condaDownloadAndInstallListener) {
        this.condaDownloadAndInstallListener = condaDownloadAndInstallListener;
    }

    private void init() {
        firstLinePromptJLabel = new JLabel();
        secondLinePromptJLabel = new JLabel();
        pathBrowseButton = new TextFieldWithBrowseButton();
        installButton = new JButton();
        textJLabelNoPath = new JLabel();
        firstLinePromptJLabel.setBounds(50, 35, 550, 30);
        secondLinePromptJLabel.setBounds(50, 70, 550, 30);
        pathBrowseButton.setBounds(50, 105, 350, 30);
        installButton.setBounds(450, 105, 100, 35);
        textJLabelNoPath.setBounds(50, 150, 400, 20);
        firstLinePromptJLabel
                .setText("Select the CONDA installation path. It is recommended to choose an exist directory.");
        secondLinePromptJLabel.setText("Miniconda will be installed into a new directory called 'Miniconda3'");
        installButton.setText("Install");
        textJLabelNoPath.setText("Please select the conda download and installation path first");
        textJLabelNoPath.setForeground(Color.red);
        textJLabelNoPath.setVisible(false);
        pathBrowseButton.setText(SystemProperties.getUserHome());
        this.add(firstLinePromptJLabel);
        this.add(secondLinePromptJLabel);
        this.add(pathBrowseButton);
        this.add(installButton);
        this.add(textJLabelNoPath);
        pathBrowseButton.addBrowseFolderListener(new TextBrowseFolderListener(new FileChooserDescriptor(false,
                true, false, false, false, false)) {
            @Override
            @NlsSafe
            protected String chosenFileToResultingText(@NotNull VirtualFile chosenFile) {
                String textPath = super.chosenFileToResultingText(chosenFile);
                log.info("Select the CONDA download installation address path : {}", textPath);
                textJLabelNoPath.setVisible(textPath.equals(""));
                return super.chosenFileToResultingText(chosenFile);
            }
        });
        installButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                log.info("Click the CONDA download install button");
                textJLabelNoPath.setVisible(pathBrowseButton.getText().equals(""));
                condaDownloadAndInstallListener.getTextString(pathBrowseButton.getText());
            }
        });
    }

    interface CondaDownloadAndInstallListener {
        /**
         * get path
         *
         * @param path path
         */
        void getTextString(String path);
    }
}