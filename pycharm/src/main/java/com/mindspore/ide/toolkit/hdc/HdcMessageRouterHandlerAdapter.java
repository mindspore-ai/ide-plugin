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
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;

import java.util.Optional;

/**
 * js交互类
 *
 * @since 2022-04-18
 */
public class HdcMessageRouterHandlerAdapter extends CefMessageRouterHandlerAdapter {
    private Project project;
    private HdcBrowserWindowContent hdcBrowserWindowContent;

    public HdcMessageRouterHandlerAdapter(Project project, HdcBrowserWindowContent hdcBrowserWindowContent) {
        this.project = project;
        this.hdcBrowserWindowContent = hdcBrowserWindowContent;
    }

    @Override
    public boolean onQuery(
            CefBrowser browser,
            CefFrame frame,
            long queryId,
            String request,
            boolean isPersistent,
            CefQueryCallback callback) {
        if (hdcBrowserWindowContent.getCommunicator() == null) {
            return false;
        }
        Optional<String> result = hdcBrowserWindowContent.getCommunicator().query(request);
        if (result.isEmpty()) {
            callback.failure(1, "fail");
            return false;
        } else {
            callback.success(result.get());
            return true;
        }
    }
}