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
public class CondaDownloadAndInstallDialog extends JDialog {
    JLabel textJLabel;
    JLabel textJLabel2;
    TextFieldWithBrowseButton browseButton;
    JButton button;
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
        textJLabel = new JLabel();
        textJLabel2 = new JLabel();
        browseButton = new TextFieldWithBrowseButton();
        button = new JButton();
        textJLabelNoPath = new JLabel();
        textJLabel.setBounds(50, 35, 550, 30);
        textJLabel2.setBounds(50, 70, 550, 30);
        browseButton.setBounds(50, 105, 350, 30);
        button.setBounds(450, 105, 100, 35);
        textJLabelNoPath.setBounds(50, 150, 400, 20);
        textJLabel.setText("Select the CONDA installation path. It is recommended to choose an exist directory.");
        textJLabel2.setText("Miniconda will be installed into a new directory called 'Miniconda3'");
        button.setText("Install");
        textJLabelNoPath.setText("Please select the conda download and installation path first");
        textJLabelNoPath.setForeground(Color.red);
        textJLabelNoPath.setVisible(false);
        browseButton.setText(SystemProperties.getUserHome());
        this.add(textJLabel);
        this.add(textJLabel2);
        this.add(browseButton);
        this.add(button);
        this.add(textJLabelNoPath);
        browseButton.addBrowseFolderListener(new TextBrowseFolderListener(new FileChooserDescriptor(false,
                true, false, false, false, false)) {
            @Override
            @NlsSafe
            protected String chosenFileToResultingText(@NotNull VirtualFile chosenFile) {
                String text = super.chosenFileToResultingText(chosenFile);
                textJLabelNoPath.setVisible(text.equals(""));
                return super.chosenFileToResultingText(chosenFile);
            }
        });
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                textJLabelNoPath.setVisible(browseButton.getText().equals(""));
                condaDownloadAndInstallListener.getTextString(browseButton.getText());
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