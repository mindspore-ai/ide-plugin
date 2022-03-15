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

package com.mindspore.ide.toolkit.common.exceptions;

import com.mindspore.ide.toolkit.common.enums.EnumError;
import org.jetbrains.annotations.NotNull;

public class MsToolKitException extends Exception {
    private String errMsg;

    private String errCode;

    private String solution;

    public MsToolKitException(String errMsg) {
        super(errMsg);
    }

    public MsToolKitException(@NotNull EnumError enumError) {
        super(enumError.getErrorMessage());
        this.errMsg = enumError.getErrorMessage();
        this.errCode = enumError.getErrorCode();
        this.solution = enumError.getSolution();
    }

    public String getErrMsg() {
        return errMsg;
    }

    public String getErrCode() {
        return errCode;
    }

    public String getSolution() {
        return solution;
    }
}