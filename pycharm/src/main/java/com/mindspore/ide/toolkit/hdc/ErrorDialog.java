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

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.Rectangle;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 错误弹窗
 *
 * @since 2022-04-18
 */
public class ErrorDialog extends JDialog {
    private ErrorDataInfo errorDataInfo;

    private Project project;

    /**
     * construction method
     *
     * @param errorDataInfo errorDataInfo
     * @param project       project
     */
    public ErrorDialog(ErrorDataInfo errorDataInfo, Project project) {
        this.errorDataInfo = errorDataInfo;
        this.project = project;
        this.setTitle("error");
        this.setModal(false);
        this.setSize(1000, 700);
        setLocationRelativeTo(null);
        init();
        this.setLayout(null);
    }

    private void init() {
        JLabel firstLinePromptJLabel = new JLabel(errorDataInfo.getTitleString(), JLabel.CENTER);
        firstLinePromptJLabel.setBounds(new Rectangle(0, 0, 1000, 25));
        this.add(firstLinePromptJLabel);
        String[][] objects = errorDataInfo.getStrings().toArray(new String[0][]);
        String[] titles = {errorDataInfo.getProjectString(), errorDataInfo.getDescriptionString()};
        DefaultTableModel model = new DefaultTableModel(objects, titles);
        JTable jTable = new JTable(2, errorDataInfo.getStrings().size());
        jTable.setModel(model);
        jTable.setEnabled(false);
        jTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getSource() instanceof JTable) {
                    JTable jTable1 = (JTable) mouseEvent.getSource();
                    int raw = jTable1.rowAtPoint(mouseEvent.getPoint());
                    int col = jTable1.columnAtPoint(mouseEvent.getPoint());
                    String strHttp =
                            HdcRegularUtils.filterSpecialStr(HdcRegularUtils.REGEX_HTTP, objects[raw][col].toString());
                    if (!strHttp.isEmpty()) {
                        ErrorWindowDialog errorWindowDialog = new ErrorWindowDialog(project, strHttp);
                        errorWindowDialog.show();
                    }
                }
            }
        });
        jTable.setBounds(new Rectangle(0, 25, 1000, 700));
        jTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        jTable.getColumnModel().getColumn(1).setPreferredWidth(900);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(JTextField.CENTER);
        jTable.getColumnModel().getColumn(0).setCellRenderer(renderer);
        JScrollPane scrollPane = new JScrollPane(jTable);
        scrollPane.setBounds(new Rectangle(0, 25, 1000, 700));
        this.add(scrollPane, BorderLayout.CENTER);
    }
}