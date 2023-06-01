package com.mindspore.ide.toolkit.apiscanning;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.SystemProperties;
import com.mindspore.ide.toolkit.common.utils.FileUtils;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * export dialog
 *
 * @since 2022-12-07
 */
public class ExportDialog extends JDialog {
    TextFieldWithBrowseButton pathBrowseButton;

    JButton exportButton;

    private String apiString;

    private String fileName;

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    /**
     * construction method
     *
     * @param apiString apiString
     * @param fileName trigger file name
     */
    public ExportDialog(String apiString, String fileName) {
        this.apiString = apiString;
        this.setTitle("导出表格内容");
        this.setModal(true);
        this.setSize(600, 100);
        this.fileName = fileName;
        setLocationRelativeTo(null);
        initDialog();
        this.setLayout(null);
    }

    private void initDialog() {
        pathBrowseButton = new TextFieldWithBrowseButton();
        pathBrowseButton.setBounds(50, 10, 350, 30);
        pathBrowseButton.setText(SystemProperties.getUserHome() + File.separator + fileName + "-mapping-"
                + dateFormat.format(new Date()) + ".csv");
        this.add(pathBrowseButton);
        pathBrowseButton.addBrowseFolderListener(
                new TextBrowseFolderListener(new FileChooserDescriptor(false, true, false, false, false,false)) {
                    @Override
                    @NlsSafe
                    protected String chosenFileToResultingText(@NotNull VirtualFile chosenFile) {
                        String dir = chosenFile.getPresentableUrl();
                        String text = pathBrowseButton.getText();
                        String fileNameTemp = text.substring(text.lastIndexOf(File.separator) + 1);
                        return dir + File.separator + fileNameTemp;
                    }
                });

        exportButton = new JButton();
        exportButton.setBounds(450, 10, 100, 35);
        exportButton.setText("导出");
        this.add(exportButton);
        exportButton.addActionListener(actionEvent -> {
            String filePath = pathBrowseButton.getText();
            try (InputStream is = new ByteArrayInputStream(apiString.getBytes("GB2312"))) {
                FileUtils.writeFile(filePath, is);
                JOptionPane.showMessageDialog(null, "导出成功，文件路径：" + filePath);
                this.setVisible(false);
            } catch (IOException ioException) {
                JOptionPane.showMessageDialog(null, "导出失败");
            }
        });
    }
}