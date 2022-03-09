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

package com.mindspore.ide.toolkit.witzard;

import com.mindspore.ide.toolkit.wizard.MSVersionInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * ms version info test
 *
 * @author hanguisen
 * @since 1.7
 */
public class MsVersionInfoTest {
    MSVersionInfo info;

    @Before
    public void initMsVersionInfo() {
        info = new MSVersionInfo();
        info.setOsInfoList(new ArrayList<>());
        info.setDes("testDes");
        info.setName("msVersionInfo");
    }

    @Test
    public void test() {
        Assert.assertNotNull(info.getName());
        Assert.assertNotNull(info.getOsInfoList());
        Assert.assertNotNull(info.getDes());
    }
}
