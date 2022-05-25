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

import com.intellij.openapi.Disposable;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import org.cef.browser.CefBrowser;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 浏览器
 *
 * @since 2022-04-18
 */
public class HdcBrowserWindowContent implements Disposable {
    private final JPanel content;

    private JBCefBrowser jbCefBrowser;

    private String url = "/web/hdcDemo.html";

    private String html;

    private Communicator communicator;

    /**
     * construction method
     */
    public HdcBrowserWindowContent() {
        content = new JPanel(new BorderLayout());
        if (!JBCefApp.isSupported()) {
            content.add(new JLabel("ide don't support JCEF", SwingConstants.CENTER));
        } else {
            jbCefBrowser = new JBCefBrowser();
            content.add(jbCefBrowser.getComponent(), BorderLayout.CENTER);
            try (InputStream inputStream = HdcBrowserWindowContent.class.getResourceAsStream(url);
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                for (int length; (length = inputStream.read(buffer)) != -1;) {
                    output.write(buffer, 0, length);
                }
                html = output.toString(StandardCharsets.UTF_8);
            } catch (IOException exceptionIo) {
                html = "";
            }
            jbCefBrowser.loadHTML(html);
        }
    }

    @Override
    public String toString() {
        return "BrowserWindowContent{ content = " + content + "}";
    }

    /**
     * 刷新
     */
    public void refreshBrowser() {
        jbCefBrowser.loadHTML(html);
    }

    /**
     * 获取 CefBrowser
     *
     * @return CefBrowser
     */
    public CefBrowser getCefBrowser() {
        return jbCefBrowser.getCefBrowser();
    }

    public JPanel getContent() {
        return content;
    }

    public JBCefBrowser getJbCefBrowser() {
        return jbCefBrowser;
    }

    public Communicator getCommunicator() {
        return communicator;
    }

    public void setCommunicator(Communicator communicator) {
        this.communicator = communicator;
    }

    @Override
    public void dispose() {
    }
}