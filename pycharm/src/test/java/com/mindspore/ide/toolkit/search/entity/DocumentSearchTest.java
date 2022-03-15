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

package com.mindspore.ide.toolkit.search.entity;

import org.junit.Assert;
import org.junit.Test;

/**
 * DocumentSearch Test
 *
 * @since 2022-3-4
 */
public class DocumentSearchTest {
    @Test
    public void initTest() {
        DocumentSearch documentSearch = new DocumentSearch(this, "pattern");
        Assert.assertNotNull(documentSearch);
        Assert.assertNotNull(documentSearch.getValue());
        Assert.assertEquals(documentSearch.getPattern(), "pattern");
        Assert.assertEquals(documentSearch.getValueText(), "");

        DocumentSearch documentSearch1 = new DocumentSearch(this, "pattern", 10);
        Assert.assertNotNull(documentSearch1);
        Assert.assertEquals(documentSearch1.getMatchingDegree(), 10);

        DocumentValue documentValue = new DocumentValue();
        Assert.assertNotNull(documentValue);
        documentValue.setId(1);
        documentValue.setTitle("title");
        documentValue.setPath("path");
        documentValue.setUrl("url");
        documentValue.setFileType("file_type");
        documentValue.setContent("content");
        DocumentSearch documentSearch2 = new DocumentSearch(documentValue, "pattern", 10);
        Assert.assertEquals(documentSearch2.getValueText(), "title");

        Assert.assertNotNull(documentSearch2.toString());
    }
}