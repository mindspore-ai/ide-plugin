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

package com.mindspore.ide.toolkit.common.utils;

import com.mindspore.ide.toolkit.common.enums.EnumPlatform;

/**
 * get os info
 *
 * @since 2022.1.15
 */
public enum OSInfoUtils {
    INSTANCE;

    private String OS;

    private EnumPlatform platform;

    OSInfoUtils() {
        OS = System.getProperty("os.name").toLowerCase();
        if (OS.contains("linux")) {
            platform = EnumPlatform.Linux;
        } else if (OS.contains("mac") && OS.indexOf("os") > 0) {
            platform = EnumPlatform.Mac_OS;
        } else if (OS.contains("windows")) {
            platform = EnumPlatform.Windows;
        } else {
            platform = EnumPlatform.Others;
        }
    }

    public boolean isLinux() {
        return platform == EnumPlatform.Linux;
    }

    public boolean isMacOS() {
        return platform == EnumPlatform.Mac_OS;
    }

    public boolean isWindows() {
        return platform == EnumPlatform.Windows;
    }

    /**
     * 获取操作系统名字
     *
     * @return 操作系统名
     */
    public String getOsName() {
        return platform.toString();
    }

    public EnumPlatform getOs() {
        return platform;
    }
}