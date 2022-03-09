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

import java.util.ArrayList;
import java.util.List;

/**
 * DocumentResultModel Test
 *
 * @since 2022-3-4
 */
public class DocumentResultModelTest {
    @Test
    public void initTest() {
        DocumentResultModel documentResultModel = new DocumentResultModel();
        Assert.assertNotNull(documentResultModel);
        List<DocumentValue> documentValueList = new ArrayList<>();
        documentValueList.add(new DocumentValue());
        Assert.assertEquals(documentValueList.size(), 1);
        documentResultModel.setHits(documentValueList);
        documentResultModel.setOffset(1);
        documentResultModel.setLimit(1);
        documentResultModel.setNbHits(1);
        documentResultModel.setExhaustiveNbHits(true);
        documentResultModel.setProcessingTimeMs(1);
        documentResultModel.setGetHeader("getHeader");
        Assert.assertEquals(documentResultModel.getHits().size(), 1);
        Assert.assertEquals(documentResultModel.getOffset(), 1);
        Assert.assertEquals(documentResultModel.getLimit(), 1);
        Assert.assertEquals(documentResultModel.getNbHits(), 1);
        Assert.assertTrue(documentResultModel.isExhaustiveNbHits());
        Assert.assertEquals(documentResultModel.getProcessingTimeMs(), 1);
        Assert.assertEquals(documentResultModel.getGetHeader(), "getHeader");
        Assert.assertNotNull(documentResultModel.toString());
    }
}