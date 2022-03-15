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

import com.mindspore.ide.toolkit.common.beans.NormalInfoConstants;

import java.util.Optional;

public enum EnumHardWarePlatform {
    CPU("CPU", NormalInfoConstants.MINDSPORE_CPU_DESCRIPTION),
    GPU("GPU", NormalInfoConstants.MINDSPORE_GPU_DESCRIPTION),
    ASCEND("ASCEND", NormalInfoConstants.MINDSPORE_ASCEND_DESCRIPTION);

    private String code;

    private String mindsporeMapping;

    EnumHardWarePlatform(String code, String mindsporeMapping) {
        this.code = code;
        this.mindsporeMapping = mindsporeMapping;
    }

    public String getCode() {
        return code;
    }

    public String getMindsporeMapping() {
        return mindsporeMapping;
    }

    public static Optional<EnumHardWarePlatform> findByCode(String code) {
        for (EnumHardWarePlatform val : EnumHardWarePlatform.values()) {
            if (val.getCode().equals(code)) {
                return Optional.of(val);
            }
        }
        return Optional.empty();
    }
}