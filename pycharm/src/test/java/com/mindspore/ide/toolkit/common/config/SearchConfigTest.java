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
 * SearchConfig Test
 *
 * @since 2022-1-27
 */
public class SearchConfigTest {
    @Test
    public void initTest() {
        SearchConfig searchConfig = new SearchConfig();
        Assert.assertNotNull(searchConfig);
        searchConfig.setIpAddress("ipAddress");
        searchConfig.setPort(10);
        searchConfig.setUrlBasePath("urlBasePath");
        searchConfig.setMdbSize("mdbSize");
        searchConfig.setUdbSize("udbSize");
        Assert.assertEquals(searchConfig.getIpAddress(), "ipAddress");
        Assert.assertEquals(searchConfig.getPort(), 10);
        Assert.assertEquals(searchConfig.getUrlBasePath(), "urlBasePath");
        Assert.assertEquals(searchConfig.getMdbSize(), "mdbSize");
        Assert.assertEquals(searchConfig.getUdbSize(), "udbSize");
        SearchConfig searchConfig1 = searchConfig.clone();
        Assert.assertNotNull(searchConfig1);

        SearchConfig searchConfig2 = new SearchConfig("ipAddress",
                10, "urlBasePath", "mdbSize", "udbSize");
        Assert.assertNotNull(searchConfig2);
        Assert.assertEquals(searchConfig2.getIpAddress(), "ipAddress");
        Assert.assertEquals(searchConfig2.getPort(), 10);
        Assert.assertEquals(searchConfig2.getUrlBasePath(), "urlBasePath");
        Assert.assertEquals(searchConfig2.getMdbSize(), "mdbSize");
        Assert.assertEquals(searchConfig2.getUdbSize(), "udbSize");

        Assert.assertEquals(searchConfig2.hashCode(), -742722789);
        Assert.assertNotNull(searchConfig2.toString());
    }
}