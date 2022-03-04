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

package com.mindspore.ide.toolkit.common.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * PythonCommand Test
 *
 * @since 2022-1-27
 */
public class RegularUtilsTest {
    @Test
    public void initTest() {
        Assert.assertEquals(RegularUtils.removeChinese(null), "");
        Assert.assertEquals(RegularUtils.removeChinese(""), "");
        Assert.assertEquals(RegularUtils.removeChinese("123"), "123");
        Assert.assertEquals(RegularUtils.removeChinese("123."), "123.");
        Assert.assertEquals(RegularUtils.removeChinese("123，。"), "123");

        Assert.assertTrue(RegularUtils.isEmpty(""));
        Assert.assertTrue(RegularUtils.isEmpty(" "));
        Assert.assertFalse(RegularUtils.isEmpty("1111"));
        Assert.assertTrue(RegularUtils.isEmpty("null"));
        Assert.assertTrue(RegularUtils.isEmpty(null));

        Assert.assertEquals(RegularUtils.normalizeFilePath("1111 "), "1111");
        Assert.assertEquals(RegularUtils.normalizeFilePath("1111   "), "1111");
        Assert.assertEquals(RegularUtils.normalizeFilePath("111   111   "), "111   111");
        Assert.assertEquals(RegularUtils.normalizeFilePath(""), "");
        Assert.assertEquals(RegularUtils.normalizeFilePath("   1111"), "   1111");
    }
}