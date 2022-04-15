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

import com.mindspore.ide.toolkit.common.utils.PathUtils;
import com.mindspore.ide.toolkit.common.utils.YamlUtils;

import java.io.File;
import java.util.Optional;

/**
 * guide config
 *
 * @since 2022-3-10
 */
public class GuideConfig {
    public static final String CONFIG_FILE = "config/guideConfig.yaml";

    private String guideUrl;

    private String guideConfig;

    private String guideConfigFile;

    private String pluginVersion;

    private String guideSettingUrl;

    public String getGuideUrl() {
        return guideUrl;
    }

    public void setGuideUrl(String guideUrl) {
        this.guideUrl = guideUrl;
    }

    public String getGuideConfig() {
        return guideConfig;
    }

    public void setGuideConfig(String guideConfig) {
        this.guideConfig = guideConfig;
    }

    public String getGuideConfigFile() {
        return guideConfigFile;
    }

    public void setGuideConfigFile(String guideConfigFile) {
        this.guideConfigFile = guideConfigFile;
    }

    public String getPluginVersion() {
        return pluginVersion;
    }

    public void setPluginVersion(String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }

    public String getGuideSettingUrl() {
        return guideSettingUrl;
    }

    public void setGuideSettingUrl(String guideSettingUrl) {
        this.guideSettingUrl = guideSettingUrl;
    }

    private static class ConfigBuilder {
        private static final GuideConfig CONFIG;

        static {
            Optional<GuideConfig> instance = YamlUtils.INSTANCE.readResourceFile(CONFIG_FILE, GuideConfig.class);
            CONFIG = instance.orElse(null);
        }
    }

    public static GuideConfig get() {
        return ConfigBuilder.CONFIG;
    }

    public String getConfigFilePath() {
        return String.join(File.separator, PathUtils.getDefaultResourcePath(), getGuideConfig(), getGuideConfigFile());
    }
}