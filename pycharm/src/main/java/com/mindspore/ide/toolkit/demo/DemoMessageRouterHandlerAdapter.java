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

package com.mindspore.ide.toolkit.demo;

import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;

/**
 * js交互类
 *
 * @since 2022-04-18
 */
public class DemoMessageRouterHandlerAdapter extends CefMessageRouterHandlerAdapter {
    @Override
    public boolean onQuery(
            CefBrowser browser,
            CefFrame frame,
            long queryId,
            String request,
            boolean isPersistent,
            CefQueryCallback callback) {
        String result = WebCommunicator.search(request);
        callback.success(result);
        return super.onQuery(browser, frame, queryId, request, isPersistent, callback);
    }
}