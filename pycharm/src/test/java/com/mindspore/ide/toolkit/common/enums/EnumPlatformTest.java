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

package com.mindspore.ide.toolkit.common.enums;

import org.junit.Assert;
import org.junit.Test;

/**
 * PythonCommand Test
 *
 * @since 2022-1-27
 */
public class EnumPlatformTest {
    @Test
    public void initTest() {
        EnumPlatform enumPlatform = EnumPlatform.Linux;
        Assert.assertNotNull(enumPlatform);
        Assert.assertEquals(enumPlatform.toString(), "Linux");

        EnumPlatform enumPlatform1 = EnumPlatform.MacOs_x86;
        Assert.assertNotNull(enumPlatform1);
        Assert.assertEquals(enumPlatform1.toString(), "MacOs_x86");

        EnumPlatform enumPlatform2 = EnumPlatform.MacOs_arm;
        Assert.assertNotNull(enumPlatform2);
        Assert.assertEquals(enumPlatform2.toString(), "MacOs_arm");

        EnumPlatform enumPlatform3 = EnumPlatform.Windows;
        Assert.assertNotNull(enumPlatform3);
        Assert.assertEquals(enumPlatform3.toString(), "Windows");

        EnumPlatform enumPlatform4 = EnumPlatform.Others;
        Assert.assertNotNull(enumPlatform4);
        Assert.assertEquals(enumPlatform4.toString(), "Others");
    }
}