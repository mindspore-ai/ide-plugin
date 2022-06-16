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

package com.mindspore.ide.toolkit.common.events;

import com.mindspore.ide.toolkit.common.config.GuideUserEntity;
import org.junit.Assert;
import org.junit.Test;

/**
 * GuideUserEvents Test
 *
 * @since 2022-1-27
 */
public class GuideUserEventsTest {
    @Test
    public void initTest() {
        GuideUserEvents guideUserEvents = new GuideUserEvents();
        Assert.assertNotNull(guideUserEvents);

        GuideUserEntity guideUserEntity = new GuideUserEntity();
        GuideUserEvents.DontAskAgain dontAskAgain = new GuideUserEvents.DontAskAgain(guideUserEntity);
        dontAskAgain.setEntity(guideUserEntity);
        Assert.assertNotNull(dontAskAgain.getEntity());

        Assert.assertNotNull(guideUserEvents.toString());
    }
}