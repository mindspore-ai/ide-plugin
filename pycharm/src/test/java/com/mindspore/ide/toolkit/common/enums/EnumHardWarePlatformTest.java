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
 * EnumHardWarePlatform Test
 *
 * @since 2022-1-27
 */
public class EnumHardWarePlatformTest {
    @Test
    public void initTest() {
        EnumHardWarePlatform enumHardWarePlatform = EnumHardWarePlatform.CPU;
        Assert.assertNotNull(enumHardWarePlatform);
        Assert.assertEquals(enumHardWarePlatform.getCode(), "CPU");
        Assert.assertEquals(enumHardWarePlatform.getMindsporeMapping(), "mindspore");
        EnumHardWarePlatform.findByCode(enumHardWarePlatform.getCode());

        EnumHardWarePlatform enumHardWarePlatform1 = EnumHardWarePlatform.GPU;
        Assert.assertNotNull(enumHardWarePlatform1);
        Assert.assertEquals(enumHardWarePlatform1.getCode(), "GPU");
        Assert.assertEquals(enumHardWarePlatform1.getMindsporeMapping(), "mindspore-gpu");
        EnumHardWarePlatform.findByCode(enumHardWarePlatform1.getCode());

        EnumHardWarePlatform enumHardWarePlatform2 = EnumHardWarePlatform.ASCEND;
        Assert.assertNotNull(enumHardWarePlatform2);
        Assert.assertEquals(enumHardWarePlatform2.getCode(), "ASCEND");
        Assert.assertEquals(enumHardWarePlatform2.getMindsporeMapping(), "mindspore-ascend");
        EnumHardWarePlatform.findByCode(enumHardWarePlatform2.getCode());

        EnumHardWarePlatform.findByCode("");
    }
}