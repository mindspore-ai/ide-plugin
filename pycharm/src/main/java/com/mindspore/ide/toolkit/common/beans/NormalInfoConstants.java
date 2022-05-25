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

import javax.swing.Icon;
import java.io.File;

/**
 * normal info constants
 *
 * @since 2022-3-10
 */
public class NormalInfoConstants {
    /**
     * MSVersionInfo
     */
    public static final String MS_VERSION_INFO = File.separator + "jsons" + File.separator + "MSVersionInfo.json";

    /**
     * expert1
     */
    public static final String EXPERT_1 = "/" + "jsons" + "/" + "demo" + "/" + "expert1.json";

    /**
     * expert2
     */
    public static final String EXPERT_2 = "/" + "jsons" + "/" + "demo" + "/" + "expert2.json";

    /**
     * expert3
     */
    public static final String EXPERT_3 = "/" + "jsons" + "/" + "demo" + "/" + "expert3.json";

    /**
     * fresh1
     */
    public static final String FRESH_1 = "/" + "jsons" + "/" + "demo" + "/" + "fresh1.json";

    /**
     * fresh2
     */
    public static final String FRESH_2 = "/" + "jsons" + "/" + "demo" + "/" + "fresh2.json";

    /**
     * fresh3
     */
    public static final String FRESH_3 = "/" + "jsons" + "/" + "demo" + "/" + "fresh3.json";

    /**
     * transfer1
     */
    public static final String TRANSFER_1 = "/" + "jsons" + "/" + "demo" + "/" + "transfer1.json";

    /**
     * transfer2
     */
    public static final String TRANSFER_2 = "/" + "jsons" + "/" + "demo" + "/" + "transfer2.json";

    /**
     * icon
     */
    public static final String MS_ICON_PATH = File.separator + "icons" + File.separator + "ms_16px.png";

    /**
     * icon
     */
    public static final Icon MS_ICON_12PX = IconLoader.getIcon("/icons/12px.svg", NormalInfoConstants.class);

    /**
     * icon
     */
    public static final Icon MS_ICON_13PX = IconLoader.getIcon("/icons/13px.svg", NormalInfoConstants.class);

    /**
     * icon
     */
    public static final Icon MS_ICON_16PX = IconLoader.getIcon("/icons/16px.svg", NormalInfoConstants.class);

    /**
     * download path
     */
    public static final String MS_DOWNLOAD_RL_PATH = File.separator + "download" + File.separator;

    /**
     * name
     */
    public static final String MINDSPORE_CPU_DESCRIPTION = "mindspore";

    /**
     * gpu
     */
    public static final String MINDSPORE_GPU_DESCRIPTION = "mindspore-gpu";

    /**
     * ascend
     */
    public static final String MINDSPORE_ASCEND_DESCRIPTION = "mindspore-ascend";
}