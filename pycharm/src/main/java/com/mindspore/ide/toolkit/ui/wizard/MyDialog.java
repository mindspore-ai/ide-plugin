package com.mindspore.ide.toolkit.ui.wizard;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.SystemProperties;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyDialog extends JDialog {
    JLabel teXtJLabel;
    JLabel teXtJLabel2;
    TextFieldWithBrowseButton browseButton;
    JButton button;
    JLabel teXtJLabelNoPath;
    MyDialogListener myDialogListener;

    public void setMyDialogListener(MyDialogListener myDialogListener) {
        this.myDialogListener = myDialogListener;
    }

    public MyDialog() {
        super((Dialog) null, "Install Miniconda Automatically", true);
        this.setSize(600, 250);
        setLocationRelativeTo(null);
        init();
        this.setLayout(null);
    }

    private void init() {
        teXtJLabel = new JLabel();
        teXtJLabel2 = new JLabel();
        browseButton = new TextFieldWithBrowseButton();
        button = new JButton();
        teXtJLabelNoPath = new JLabel();
        teXtJLabel.setBounds(50, 35, 550, 30);
        teXtJLabel2.setBounds(50, 70, 550, 30);
        browseButton.setBounds(50, 105, 350, 30);
        button.setBounds(450, 105, 100, 35);
        teXtJLabelNoPath.setBounds(50,150,400,20);
        teXtJLabel.setText("Select the CONDA installation path. It is recommended to choose an exist directory.");
        teXtJLabel2.setText("Miniconda will be installed into a new directory called 'Miniconda3'");
        button.setText("Install");
        teXtJLabelNoPath.setText("Please select the conda download and installation path first");
        teXtJLabelNoPath.setForeground(Color.red);
        teXtJLabelNoPath.setVisible(false);
        browseButton.setText(SystemProperties.getUserHome());
        this.add(teXtJLabel);
        this.add(teXtJLabel2);
        this.add(browseButton);
        this.add(button);
        this.add(teXtJLabelNoPath);
        browseButton.addBrowseFolderListener(new TextBrowseFolderListener(new FileChooserDescriptor(false, true, false, false, false, false)) {
            @Override
            protected @NotNull @NlsSafe String chosenFileToResultingText(@NotNull VirtualFile chosenFile) {
                String text= super.chosenFileToResultingText(chosenFile);
                teXtJLabelNoPath.setVisible(text.equals(""));
                return super.chosenFileToResultingText(chosenFile);
            }
        });
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                teXtJLabelNoPath.setVisible(browseButton.getText().equals(""));
                myDialogListener.getTextString(browseButton.getText());
            }
        });
    }
}