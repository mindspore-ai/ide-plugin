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
public class EnumErrorTest {
    @Test
    public void initTest() {
        EnumError enumError = EnumError.READ_CONFIGURATION_ERROR;
        Assert.assertNotNull(enumError);
        Assert.assertEquals(enumError.getErrorCode(), "ERROR_000001");
        Assert.assertEquals(enumError.getErrorMessage(), "read configuration error, file path: {}");
        Assert.assertNull(enumError.getSolution());

        EnumError enumError1 = EnumError.IO_EXCEPTION;
        Assert.assertNotNull(enumError1);
        Assert.assertEquals(enumError1.getErrorCode(), "ERROR_000002");
        Assert.assertEquals(enumError1.getErrorMessage(), "io Exception!");
        Assert.assertNull(enumError1.getSolution());

        EnumError enumError2 = EnumError.CREATE_CACHE_DIR_FAIL;
        Assert.assertNotNull(enumError2);
        Assert.assertEquals(enumError2.getErrorCode(), "ERROR_000003");
        Assert.assertEquals(enumError2.getErrorMessage(), "create cache dir fail.");
        Assert.assertNull(enumError2.getSolution());

        EnumError enumError3 = EnumError.NULL_PROJECT;
        Assert.assertNotNull(enumError3);
        Assert.assertEquals(enumError3.getErrorCode(), "ERROR_000004");
        Assert.assertEquals(enumError3.getErrorMessage(), "project is null");
        Assert.assertNull(enumError3.getSolution());

        EnumError enumError4 = EnumError.FILE_CREATE_FAIL;
        Assert.assertNotNull(enumError4);
        Assert.assertEquals(enumError4.getErrorCode(), "ERROR_000005");
        Assert.assertEquals(enumError4.getErrorMessage(), "file create fail, expected file path:");
        Assert.assertNull(enumError4.getSolution());

        EnumError enumError5 = EnumError.CONDA_EXECUTABLE_NOT_SPECIFIED;
        Assert.assertNotNull(enumError5);
        Assert.assertEquals(enumError5.getErrorCode(), "ERROR_000006");
        Assert.assertEquals(enumError5.getErrorMessage(), "Conda executable is not specified.");
        Assert.assertEquals(enumError5.getSolution(), "Please specify conda executable.");
    }
}