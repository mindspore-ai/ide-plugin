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

package com.mindspore.ide.toolkit.common.config;

import org.junit.Assert;
import org.junit.Test;

/**
 * PythonCommand Test
 *
 * @since 2022-1-27
 */
public class GlobalConfigTest {
    @Test
    public void initTest() {
        Assert.assertEquals(GlobalConfig.CONFIG_FILE, "config/globalConfig.yaml");

        GlobalConfig globalConfig = GlobalConfig.get();
        Assert.assertNotNull(globalConfig);
        Assert.assertEquals(globalConfig.getResourceFolder(), ".mindspore");
        Assert.assertEquals(globalConfig.getDownloadBaseUrl(), "");
        Assert.assertEquals(globalConfig.getToolWindowName(), "MindSpore");
        Assert.assertEquals(globalConfig.getToolWindowUrl(), "https://www.mindspore.cn/search");
        Assert.assertEquals(globalConfig.getToolWindowSearchContent(), "SearchDoc");
        Assert.assertEquals(globalConfig.getMsEnvValidatorFile(), "MindSporeEnvValidator.py");
        Assert.assertNotNull(globalConfig.getCompleteModelInfo());

        GlobalConfig globalConfig1 = GlobalConfig.get();
        globalConfig1.setResourceFolder("resourceFolder");
        globalConfig1.setDownloadBaseUrl("downloadBaseUrl");
        globalConfig1.setToolWindowName("toolWindowName");
        globalConfig1.setToolWindowUrl("toolWindowUrl");
        globalConfig1.setToolWindowSearchContent("toolWindowSearchContent");
        globalConfig1.setMsEnvValidatorFile("msEnvValidatorFile");
        GlobalConfig.CompleteModelInfo completeModelInfo = new GlobalConfig.CompleteModelInfo();
        globalConfig1.setCompleteModelInfo(completeModelInfo);
        Assert.assertEquals(globalConfig1.getResourceFolder(), "resourceFolder");
        Assert.assertEquals(globalConfig1.getDownloadBaseUrl(), "downloadBaseUrl");
        Assert.assertEquals(globalConfig1.getToolWindowName(), "toolWindowName");
        Assert.assertEquals(globalConfig1.getToolWindowUrl(), "toolWindowUrl");
        Assert.assertEquals(globalConfig1.getToolWindowSearchContent(), "toolWindowSearchContent");
        Assert.assertEquals(globalConfig1.getMsEnvValidatorFile(), "msEnvValidatorFile");
        Assert.assertNotNull(globalConfig1.getCompleteModelInfo());

        Assert.assertNotNull(globalConfig1.toString());

        GlobalConfig.CompleteModelInfo completeModelInfo1 = new GlobalConfig.CompleteModelInfo();
        completeModelInfo1.setOldConfig("oldConfig");
        completeModelInfo1.setNewConfig("newConfig");
        completeModelInfo1.setParentDir("parentDir");
        completeModelInfo1.setDownloadUrl("downloadUrl");
        completeModelInfo1.setToken("token");
        Assert.assertEquals(completeModelInfo1.getOldConfig(), "oldConfig");
        Assert.assertEquals(completeModelInfo1.getNewConfig(), "newConfig");
        Assert.assertEquals(completeModelInfo1.getParentDir(), "parentDir");
        Assert.assertEquals(completeModelInfo1.getDownloadUrl(), "downloadUrl");
        Assert.assertEquals(completeModelInfo1.getToken(), "token");
    }
}