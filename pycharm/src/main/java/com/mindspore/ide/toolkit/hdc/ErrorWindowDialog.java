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
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Dimension;
import java.awt.BorderLayout;

/**
 * 含有jcef弹窗
 *
 * @since 2022-04-18
 */
public class ErrorWindowDialog extends DialogWrapper {
    private Project project;
    private String url;

    /**
     * construction method
     *
     * @param project project
     * @param url     url
     */
    protected ErrorWindowDialog(@Nullable Project project, String url) {
        super(project);
        this.url = url;
        this.project = project;
        this.setModal(false);
        init();
        setTitle("error url");
    }

    @Override
    @Nullable
    protected JComponent createCenterPanel() {
        JPanel jPanel = new JPanel();
        if (!JBCefApp.isSupported()) {
            jPanel.add(new JLabel());
        } else {
            JBCefBrowser jbCefBrowser = new JBCefBrowser();
            jbCefBrowser.getComponent().setPreferredSize(new Dimension(1000, 700));
            jPanel.add(jbCefBrowser.getComponent(), BorderLayout.CENTER);
            jbCefBrowser.loadURL(url);
        }
        return jPanel;
    }
}