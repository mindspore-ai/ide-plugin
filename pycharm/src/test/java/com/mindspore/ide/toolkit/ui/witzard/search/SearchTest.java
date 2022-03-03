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

package com.mindspore.ide.toolkit.ui.witzard.search;

import com.intellij.openapi.project.Project;
import com.mindspore.ide.toolkit.ui.search.BrowserWindowContent;
import com.mindspore.ide.toolkit.ui.search.BrowserWindowManager;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * search test
 *
 * @author hanguisen
 * @since 1.7
 */
public class SearchTest {

    @Test(expected = NullPointerException.class)
    public void testBrowserWindowContent() {
        BrowserWindowContent content = new BrowserWindowContent("www.mindspore.com");
        Assert.assertNotNull(content.getContent());
        Assert.assertNotNull(content.getCefBrowser());
        Assert.assertNotNull(content.toString());
        content.refreshBrowser();
        content.dispose();
    }

    @Test(expected = NullPointerException.class)
    public void testBrowserWindowManager() {
        BrowserWindowManager.getBrowserWindow(Mockito.mock(Project.class));
    }
}
