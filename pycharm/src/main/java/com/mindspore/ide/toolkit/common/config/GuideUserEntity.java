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

package com.mindspore.ide.toolkit.common.config;

import com.google.gson.annotations.SerializedName;
import com.mindspore.ide.toolkit.common.utils.YamlUtils;

import java.util.Optional;

/**
 * guide user entity
 *
 * @since 2022-3-10
 */
public class GuideUserEntity {
    @SerializedName("askAgain")
    private boolean isAskAgain;

    private String version;

    @SerializedName("notificationTitle")
    private String title;

    @SerializedName("notificationContent")
    private String content;

    public boolean isAskAgain() {
        return isAskAgain;
    }

    public void setAskAgain(boolean isAskAgain) {
        this.isAskAgain = isAskAgain;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private static class ConfigBuilder {
        private static final GuideUserEntity CONFIG;

        static {
            Optional<GuideUserEntity> instance = YamlUtils.INSTANCE.readConfigFile(GuideConfig.get().getConfigFilePath(), GuideUserEntity.class);
            CONFIG = instance.orElse(null);
        }
    }

    public static GuideUserEntity get() {
        return ConfigBuilder.CONFIG;
    }
}