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
 * SearchResource Test
 *
 * @since 2022-1-27
 */
public class SearchResourceTest {
    @Test
    public void initTest() {
        SearchResource searchResource = new SearchResource();
        Assert.assertNotNull(searchResource);
        searchResource.setWindows("windows");
        searchResource.setLinux("");
        searchResource.setMac("");
        Assert.assertEquals(searchResource.getWindows(), "windows");
        Assert.assertEquals(searchResource.getLinux(), "");
        Assert.assertEquals(searchResource.getMac(), "");

        SearchResource searchResource1 = new SearchResource("", "linux", "");
        Assert.assertNotNull(searchResource1);
        Assert.assertEquals(searchResource1.getWindows(), "");
        Assert.assertEquals(searchResource1.getLinux(), "linux");
        Assert.assertEquals(searchResource1.getMac(), "");

        Assert.assertEquals(searchResource1.hashCode(), 1780927103);
        Assert.assertNotNull(searchResource1.toString());
    }
}