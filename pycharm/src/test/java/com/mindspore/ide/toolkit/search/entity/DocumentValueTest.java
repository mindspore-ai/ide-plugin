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
 * DocumentValue Test
 *
 * @since 2022-3-4
 */
public class DocumentValueTest {
    @Test
    public void initTest() {
        DocumentValue documentValue = new DocumentValue();
        Assert.assertNotNull(documentValue);
        documentValue.setId(1);
        documentValue.setTitle("title");
        documentValue.setPath("path");
        documentValue.setUrl("url");
        documentValue.setFile_type("file_type");
        documentValue.setContent("content");
        Assert.assertEquals(documentValue.getId(), 1);
        Assert.assertEquals(documentValue.getTitle(), "title");
        Assert.assertEquals(documentValue.getPath(), "path");
        Assert.assertEquals(documentValue.getUrl(), "url");
        Assert.assertEquals(documentValue.getFile_type(), "file_type");
        Assert.assertEquals(documentValue.getContent(), "content");
        Assert.assertEquals(documentValue.toString(), "title");
    }
}