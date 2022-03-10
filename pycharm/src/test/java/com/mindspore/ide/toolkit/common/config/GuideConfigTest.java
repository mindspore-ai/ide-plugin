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
 * GuideConfig Test
 *
 * @since 2022-1-27
 */
public class GuideConfigTest {
    @Test
    public void initTest() {
        Assert.assertEquals(GuideConfig.CONFIG_FILE, "config/guideConfig.yaml");

        GuideConfig guideConfig = GuideConfig.get();
        Assert.assertNotNull(guideConfig);
        Assert.assertEquals(guideConfig.getGuideUrl(), "");
        Assert.assertEquals(guideConfig.getGuideConfig(), "guide");
        Assert.assertEquals(guideConfig.getGuideConfigFile(), "guideConfig.yaml");
        Assert.assertEquals(guideConfig.getPluginVersion(), "0.0.1");
        Assert.assertEquals(guideConfig.getGuideSettingUrl(), "");
        Assert.assertNotNull(guideConfig.getConfigFilePath());

        GuideConfig guideConfig1 = GuideConfig.get();
        Assert.assertNotNull(guideConfig1);
        guideConfig1.setGuideUrl("guideUrl");
        guideConfig1.setGuideConfig("guideConfig");
        guideConfig1.setGuideConfigFile("guideConfigFile");
        guideConfig1.setPluginVersion("pluginVersion");
        guideConfig1.setGuideSettingUrl("guideSettingUrl");
        Assert.assertEquals(guideConfig1.getGuideUrl(), "guideUrl");
        Assert.assertEquals(guideConfig1.getGuideConfig(), "guideConfig");
        Assert.assertEquals(guideConfig1.getGuideConfigFile(), "guideConfigFile");
        Assert.assertEquals(guideConfig1.getPluginVersion(), "pluginVersion");
        Assert.assertEquals(guideConfig1.getGuideSettingUrl(), "guideSettingUrl");
    }
}