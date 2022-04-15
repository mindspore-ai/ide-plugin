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

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;
import java.io.File;

/**
 * normal info constants
 *
 * @since 2022-3-10
 */
public class NormalInfoConstants {
    public static final String MS_VERSION_INFO = File.separator + "jsons" + File.separator + "MSVersionInfo.json";

    public static final String MS_ICON_PATH = File.separator + "icons" + File.separator + "ms_16px.png";

    public static final Icon MS_ICON_12PX = IconLoader.getIcon("/icons/12px.svg", NormalInfoConstants.class);

    public static final Icon MS_ICON_13PX = IconLoader.getIcon("/icons/13px.svg", NormalInfoConstants.class);

    public static final Icon MS_ICON_16PX = IconLoader.getIcon("/icons/16px.svg", NormalInfoConstants.class);

    public static final String MS_DOWNLOAD_RL_PATH = File.separator + "download" + File.separator;

    public static final String MINDSPORE_CPU_DESCRIPTION = "mindspore";

    public static final String MINDSPORE_GPU_DESCRIPTION = "mindspore-gpu";

    public static final String MINDSPORE_ASCEND_DESCRIPTION = "mindspore-ascend";
}