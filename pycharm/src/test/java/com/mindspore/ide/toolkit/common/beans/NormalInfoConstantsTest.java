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

import java.io.File;

/**
 * NormalInfoConstants Test
 *
 * @since 2022-1-27
 */
public class NormalInfoConstantsTest {
    @Test
    public void initTest() {
        Assert.assertEquals(NormalInfoConstants.MS_VERSION_INFO,
                File.separator + "jsons" + File.separator + "MSVersionInfo.json");
        Assert.assertEquals(NormalInfoConstants.MS_ICON_PATH,
                File.separator + "icons" + File.separator + "ms_16px.png");
        Assert.assertNotNull(NormalInfoConstants.MS_ICON_12PX);
        Assert.assertNotNull(NormalInfoConstants.MS_ICON_13PX);
        Assert.assertNotNull(NormalInfoConstants.MS_ICON_16PX);
        Assert.assertEquals(NormalInfoConstants.MS_DOWNLOAD_RL_PATH,
                File.separator + "download" + File.separator);
        Assert.assertEquals(NormalInfoConstants.MINDSPORE_CPU_DESCRIPTION, "mindspore");
        Assert.assertEquals(NormalInfoConstants.MINDSPORE_GPU_DESCRIPTION, "mindspore-gpu");
        Assert.assertEquals(NormalInfoConstants.MINDSPORE_ASCEND_DESCRIPTION, "mindspore-ascend");
    }
}