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

package com.mindspore.ide.toolkit.common.beans;

import org.junit.Assert;
import org.junit.Test;

/**
 * PythonPackageInfo Test
 *
 * @since 2022-1-27
 */
public class PythonPackageInfoTest {
    @Test
    public void initTest() {
        PythonPackageInfo pythonPackageInfo = new PythonPackageInfo("name", "version");
        Assert.assertNotNull(pythonPackageInfo);
        Assert.assertEquals(pythonPackageInfo.getName(), "name");
        Assert.assertEquals(pythonPackageInfo.getVersion(), "version");

        PythonPackageInfo pythonPackageInfo1 = new PythonPackageInfo("", "");
        Assert.assertNotNull(pythonPackageInfo1);
        Assert.assertEquals(pythonPackageInfo1.getName(), "");
        Assert.assertEquals(pythonPackageInfo1.getVersion(), "");

        PythonPackageInfo pythonPackageInfo2 = new PythonPackageInfo(null, null);
        Assert.assertNotNull(pythonPackageInfo2);
        Assert.assertNull(pythonPackageInfo2.getName());
        Assert.assertNull(pythonPackageInfo2.getVersion());
    }
}