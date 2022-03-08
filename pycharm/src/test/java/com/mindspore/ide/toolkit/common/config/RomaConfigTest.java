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
 * RomaConfig Test
 *
 * @since 2022-1-27
 */
public class RomaConfigTest {
    @Test
    public void initTest() {
        RomaConfig romaConfig = new RomaConfig();
        Assert.assertNotNull(romaConfig);
        SearchResource searchResource = new SearchResource();
        Assert.assertNotNull(searchResource);
        searchResource.setLinux("");
        searchResource.setMac("");
        searchResource.setWindows("windows");
        romaConfig.setToken("token");
        romaConfig.setSearchResource(searchResource);
        romaConfig.setUrl("url");
        Assert.assertEquals(romaConfig.getToken(), "token");
        Assert.assertEquals(romaConfig.getSearchResource(), searchResource);
        Assert.assertEquals(romaConfig.getUrl(), "url");
        RomaConfig romaConfig1 = romaConfig.clone();
        Assert.assertNotNull(romaConfig1);

        RomaConfig romaConfig2 = new RomaConfig("token", searchResource, "url");
        Assert.assertNotNull(romaConfig2);
        Assert.assertEquals(romaConfig2.getToken(), "token");
        Assert.assertEquals(romaConfig2.getSearchResource(), searchResource);
        Assert.assertEquals(romaConfig2.getUrl(), "url");

        Assert.assertEquals(romaConfig2.hashCode(), 1620739645);
        Assert.assertNotNull(romaConfig2.toString());
    }
}