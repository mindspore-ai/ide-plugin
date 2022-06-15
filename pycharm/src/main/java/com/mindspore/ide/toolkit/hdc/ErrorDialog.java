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

package com.mindspore.ide.toolkit.hdc;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.ui.UIUtil;
import com.mindspore.ide.toolkit.hdc.msjtable.GridBagTable;
import com.mindspore.ide.toolkit.hdc.msjtable.TableViewRenderer;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Desktop;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 错误弹窗
 *
 * @since 2022-04-18
 */
public class ErrorDialog extends JDialog {
    private ErrorDataInfo errorDataInfo;

    private Project project;

    private JButton copyJLabel;

    private GridBagTable jTable;

    private String[][] objects;

    /**
     * construction method
     *
     * @param errorDataInfo errorDataInfo
     * @param project       project
     */
    public ErrorDialog(ErrorDataInfo errorDataInfo, Project project) {
        this.errorDataInfo = errorDataInfo;
        this.project = project;
        this.setTitle("Trouble shooting");
        this.setModal(true);
        int dialogHeight = 135 + errorDataInfo.getStrings().size() * 30;
        this.setSize(1040, dialogHeight);
        setLocationRelativeTo(null);
        initJLabel();
        int tableHeight = errorDataInfo.getStrings().size() * 30 + 35;
        initJTable(tableHeight);
        // 事件
        buttonListener();
        this.setLayout(null);
    }

    private void initJLabel() {
        // 标题
        JLabel firstLinePromptJLabel = new JLabel(errorDataInfo.getTitleString(), JLabel.CENTER);
        firstLinePromptJLabel.setBounds(new Rectangle(0, 10, 1040, 30));
        firstLinePromptJLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        if (UIUtil.isUnderDarcula()) {
            firstLinePromptJLabel.setForeground(Color.WHITE);
        } else {
            firstLinePromptJLabel.setForeground(Color.BLACK);
        }
        this.add(firstLinePromptJLabel);
        // 复制粘贴按钮
        copyJLabel = new JButton("复制错误报告");
        copyJLabel.setBounds(new Rectangle(800, 5, 150, 40));
        copyJLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        if (UIUtil.isUnderDarcula()) {
            copyJLabel.setForeground(Color.WHITE);
        } else {
            copyJLabel.setForeground(Color.BLACK);
        }
        this.add(copyJLabel);
    }

    private void initJTable(int height) {
        // 表格和表格数据处理
        objects = errorDataInfo.getStrings().toArray(new String[0][]);
        String[] titles = {errorDataInfo.getProjectString(), errorDataInfo.getDescriptionString()};
        DefaultTableModel defaultTableModel = new DefaultTableModel(objects, titles);
        jTable = new GridBagTable(defaultTableModel);
        if (UIUtil.isUnderDarcula()) {
            jTable.getTableHeader().setForeground(Color.WHITE);
        } else {
            jTable.getTableHeader().setForeground(Color.BLACK);
        }
        jTable.getTableHeader().setBounds(new Rectangle(0, 0, 1000, 30));
        jTable.getTableHeader().setPreferredSize(new Dimension(1000, 30));
        jTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 16));
        jTable.setFont(new Font("微软雅黑", 0, 16));
        // 单元格行高
        jTable.setRowHeight(30);
        // 设置文字换行和url变色
        TableViewRenderer tableViewRenderer = new TableViewRenderer();
        jTable.setDefaultRenderer(Object.class, tableViewRenderer);
        // 合并单元格数据
        for (int[] ints : errorDataInfo.getIntMerge()) {
            jTable.mergeCells(ints[0], ints[1], ints[2], ints[3]);
        }
        // 不可编辑
        jTable.setEnabled(false);
        // 大小
        jTable.setBounds(new Rectangle(13, 50, 1000, height));
        // 比列
        jTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        jTable.getColumnModel().getColumn(1).setPreferredWidth(850);
        // 设置居中
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setFont(new Font("微软雅黑", Font.BOLD, 16));
        renderer.setBounds(new Rectangle(0, 0, 1000, 30));
        renderer.setHorizontalAlignment(JTextField.CENTER);
        if (UIUtil.isUnderDarcula()) {
            renderer.setForeground(Color.WHITE);
        } else {
            renderer.setForeground(Color.BLACK);
        }
        jTable.getColumnModel().getColumn(0).setCellRenderer(renderer);
        JScrollPane scrollPane = new JScrollPane(jTable);
        scrollPane.setBounds(new Rectangle(13, 50, 1000, height));
        this.add(scrollPane, BorderLayout.CENTER);
    }

    private void buttonListener() {
        // 复制粘贴事件
        copyJLabel.addActionListener(e -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable trans = new StringSelection(errorDataInfo.getErrorString());
            clipboard.setContents(trans, null);
            Messages.showMessageDialog(project, "已经复制错误报告到剪切板",
                "提示", Messages.getInformationIcon());
        });
        // 表格中url点击事件
        jTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getSource() instanceof JTable) {
                    JTable jTable1 = (JTable) mouseEvent.getSource();
                    int raw = jTable1.rowAtPoint(mouseEvent.getPoint());
                    int col = jTable1.columnAtPoint(mouseEvent.getPoint());
                    String strHttp =
                            HdcRegularUtils.filterSpecialStr(HdcRegularUtils.REGEX_HTTP, objects[raw][col]);
                    if (!strHttp.isEmpty()) {
                        // url跳转
                        try {
                            Desktop.getDesktop().browse(new URI(strHttp));
                        } catch (IOException | URISyntaxException ex) {
                            ex.toString();
                        }
                    }
                }
            }
        });
    }
}