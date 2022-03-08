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
 * DocSearchConfig Test
 *
 * @since 2022-1-27
 */
public class DocSearchConfigTest {
    @Test
    public void initTest() {
        Assert.assertEquals(DocSearchConfig.CONFIG_FILE, "config/searchConfig.yaml");

        DocSearchConfig docSearchConfig = DocSearchConfig.get();
        Assert.assertNotNull(docSearchConfig);
        Assert.assertEquals(docSearchConfig.getSearchJs(), "showDetailContent(%s)");
        Assert.assertEquals(docSearchConfig.getSearchApi(), "");

        DocSearchConfig docSearchConfig1 = new DocSearchConfig();
        docSearchConfig1.setSearchJs("searchJs");
        docSearchConfig1.setSearchApi("searchApi");
        Assert.assertEquals(docSearchConfig1.getSearchJs(), "searchJs");
        Assert.assertEquals(docSearchConfig1.getSearchApi(), "searchApi");

        Assert.assertEquals(docSearchConfig1.hashCode(), -1626344490);
        Assert.assertNotNull(docSearchConfig1.toString());
    }
}