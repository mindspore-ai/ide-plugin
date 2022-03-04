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
public class EnumPropertiesTest {
    @Test
    public void initTest() {
        EnumProperties enumProperties = EnumProperties.MIND_SPORE_PROPERTIES;
        Assert.assertNotNull(enumProperties);
        Assert.assertNull(enumProperties.getProperty("project.ward.name"));

        EnumProperties enumProperties1 = EnumProperties.EXCEPTION_SOLUTION_PROPERTIES;
        Assert.assertNotNull(enumProperties1);
        Assert.assertNull(enumProperties1.getProperty("project.ward.name"));

        EnumProperties enumProperties2 = EnumProperties.MY_BUNDLE_PROPERTIES;
        Assert.assertNotNull(enumProperties2);
        Assert.assertNull(enumProperties2.getProperty("project.ward.name"));
    }
}