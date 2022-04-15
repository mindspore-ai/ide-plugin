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

package com.mindspore.ide.toolkit.search.constant;

import org.junit.Assert;
import org.junit.Test;

/**
 * Constants Test
 *
 * @since 2022-3-4
 */
public class ConstantsTest {
    @Test
    public void initTest() {
        Assert.assertEquals(Constants.HTTP_REQUEST_SEARCHTEXT, "word");
        Assert.assertEquals(Constants.CHARSET_UTF_8, "UTF-8");
    }
}