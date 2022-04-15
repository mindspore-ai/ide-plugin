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

package com.mindspore.ide.toolkit.common.config;

import org.junit.Assert;
import org.junit.Test;

/**
 * GuideConfig Test
 *
 * @since 2022-3-10
 */
public class GuideUserEntityTest {
    @Test
    public void initTest() {
        GuideUserEntity guideUserEntity = new GuideUserEntity();
        Assert.assertNotNull(guideUserEntity);
        guideUserEntity.setAskAgain(true);
        guideUserEntity.setVersion("version");
        guideUserEntity.setTitle("notificationTitle");
        guideUserEntity.setContent("notificationContent");
        Assert.assertTrue(guideUserEntity.isAskAgain());
        Assert.assertEquals(guideUserEntity.getVersion(), "version");
        Assert.assertEquals(guideUserEntity.getTitle(), "notificationTitle");
        Assert.assertEquals(guideUserEntity.getContent(), "notificationContent");
    }
}