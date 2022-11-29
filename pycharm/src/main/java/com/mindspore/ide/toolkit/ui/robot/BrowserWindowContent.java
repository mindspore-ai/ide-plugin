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

package com.mindspore.ide.toolkit.ui.robot;

import com.intellij.openapi.Disposable;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandler;
import org.cef.network.CefRequest;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;

/**
 * browser window content
 *
 * @author zhangruojin
 * @since 2.0.0
 */
public class BrowserWindowContent implements Disposable {
    private final JPanel content;

    private JBCefBrowser jbCefBrowser;

    private String url;

    /**
     * BrowserWindowContent类仅包含url参数的构造方法
     *
     * @param url 构造window content对象时的其地址参数
     */
    public BrowserWindowContent(String url) {
        this.url = url;
        content = new JPanel(new BorderLayout());
        if (!JBCefApp.isSupported()) {
            content.add(new JLabel("ide don't support JCEF", SwingConstants.CENTER));
        } else {
            jbCefBrowser = new JBCefBrowser();
            content.add(jbCefBrowser.getComponent(), BorderLayout.CENTER);
            jbCefBrowser.getJBCefClient().getCefClient().addLoadHandler(new CefLoadHandler() {
                @Override
                public void onLoadingStateChange(CefBrowser cefBrowser,
                                                boolean isTrue,
                                                boolean isTrue1,
                                                boolean isTrue2) {
                }

                @Override
                public void onLoadStart(CefBrowser cefBrowser,
                                        CefFrame cefFrame,
                                        CefRequest.TransitionType transitionType) {
                    if (url.contains("mindspore")) {
                        cefBrowser.executeJavaScript(
                                "var idePluginMessage = \"1\";", url, 0);
                    }
                }

                @Override
                public void onLoadEnd(CefBrowser cefBrowser, CefFrame cefFrame, int var3) {
                    if (url.contains("mindspore")) {
                        cefBrowser.executeJavaScript(
                                "setSensorsCustomBuriedData(\"ideplugin\", \"1\");", url, 0);
                    }
                }

                @Override
                public void onLoadError(CefBrowser cefBrowser,
                                        CefFrame cefFrame,
                                        ErrorCode errorCode,
                                        String var1,
                                        String var2) {
                }
            });
            jbCefBrowser.loadURL(url);
        }
    }

    @Override
    public String toString() {
        return "BrowserWindowContent{ content = " + content + "}";
    }

    public JPanel getContent() {
        return content;
    }

    /**
     * 重新加载浏览器的UI组件
     */
    public void refreshBrowser() {
        jbCefBrowser.loadURL(url);
    }

    /**
     * 加载浏览器的UI组件
     *
     * @param url 需要加载的UI组件的url
     */
    public void loadUrl(String url) {
        jbCefBrowser.loadURL(url);
    }

    /**
     * 返回该Browser对象
     *
     * @return CefBrowser的实现类
     */
    public CefBrowser getCefBrowser() {
        return jbCefBrowser.getCefBrowser();
    }

    @Override
    public void dispose() {

    }
}
