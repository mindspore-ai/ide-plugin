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

package com.mindspore.ide.toolkit.transplugins;

import org.junit.Assert;
import org.junit.Test;

/**
 * EnvironmentProperty Test
 *
 * @since 2022-3-4
 */
public class EnvironmentPropertyTest {
    @Test
    public void initTest() {
        EnvironmentProperty environmentProperty = new EnvironmentProperty();
        Assert.assertNotNull(environmentProperty);
        environmentProperty.setHardware("hardware");
        environmentProperty.setOs("os");
        Assert.assertEquals(environmentProperty.getHardware(), "hardware");
        Assert.assertEquals(environmentProperty.getOs(), "os");
        Assert.assertTrue(EnvironmentProperty.valid(environmentProperty));
        Assert.assertFalse(EnvironmentProperty.valid(null));

        EnvironmentProperty environmentProperty1 = new EnvironmentProperty();
        environmentProperty1.setOs("os");
        Assert.assertFalse(EnvironmentProperty.valid(environmentProperty1));

        EnvironmentProperty environmentProperty2 = new EnvironmentProperty();
        environmentProperty2.setHardware("hardware");
        Assert.assertFalse(EnvironmentProperty.valid(environmentProperty2));
    }
}