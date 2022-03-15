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

public enum EnumError {
    READ_CONFIGURATION_ERROR("ERROR_000001", "read configuration error, file path: {}", null),
    IO_EXCEPTION("ERROR_000002", "io Exception!", null),
    CREATE_CACHE_DIR_FAIL("ERROR_000003", "create cache dir fail.", null),
    NULL_PROJECT("ERROR_000004", "project is null", null),
    FILE_CREATE_FAIL("ERROR_000005", "file create fail, expected file path:", null),
    CONDA_EXECUTABLE_NOT_SPECIFIED("ERROR_000006",
            "Conda executable is not specified.",
            "Please specify conda executable.");

    private String errorCode;

    private String errorMessage;

    private String solution;

    EnumError(String errorCode, String errorMessage, String solution) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.solution = solution;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getSolution() {
        return solution;
    }
}