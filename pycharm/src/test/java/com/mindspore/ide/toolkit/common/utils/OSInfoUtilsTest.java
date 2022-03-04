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
public class OSInfoUtilsTest {
    @Test
    public void initTest() {
        OSInfoUtils osInfoUtils = OSInfoUtils.INSTANCE;
        Assert.assertNotNull(osInfoUtils);
        Assert.assertNotNull(osInfoUtils.getOs());
        Assert.assertNotNull(osInfoUtils.getOsName());
        osInfoUtils.isLinux();
        osInfoUtils.isWindows();
        osInfoUtils.isMacOsX86();
    }
}