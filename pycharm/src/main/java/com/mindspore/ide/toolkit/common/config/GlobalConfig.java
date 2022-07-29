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

import com.mindspore.ide.toolkit.common.utils.YamlUtils;

import java.util.List;
import java.util.Optional;

/**
 * global config
 *
 * @since 2022-3-10
 */
public class GlobalConfig {
    public static final String CONFIG_FILE = "config/globalConfig.yaml";

    private String resourceFolder;

    private String downloadBaseUrl;

    private String toolWindowName;

    private String toolWindowUrl;

    private String toolWindowSearchContent;

    private String msEnvValidatorFile;

    private CompleteModelInfo completeModelInfo;

    private List<String> pythonVersions;

    public String getResourceFolder() {
        return resourceFolder;
    }

    public void setResourceFolder(String resourceFolder) {
        this.resourceFolder = resourceFolder;
    }

    public String getDownloadBaseUrl() {
        return downloadBaseUrl;
    }

    public void setDownloadBaseUrl(String downloadBaseUrl) {
        this.downloadBaseUrl = downloadBaseUrl;
    }

    public String getToolWindowName() {
        return toolWindowName;
    }

    public void setToolWindowName(String toolWindowName) {
        this.toolWindowName = toolWindowName;
    }

    public String getToolWindowUrl() {
        return toolWindowUrl;
    }

    public void setToolWindowUrl(String toolWindowUrl) {
        this.toolWindowUrl = toolWindowUrl;
    }

    public String getToolWindowSearchContent() {
        return toolWindowSearchContent;
    }

    public void setToolWindowSearchContent(String toolWindowSearchContent) {
        this.toolWindowSearchContent = toolWindowSearchContent;
    }

    public String getMsEnvValidatorFile() {
        return msEnvValidatorFile;
    }

    public void setMsEnvValidatorFile(String msEnvValidatorFile) {
        this.msEnvValidatorFile = msEnvValidatorFile;
    }

    public CompleteModelInfo getCompleteModelInfo() {
        return completeModelInfo;
    }

    public void setCompleteModelInfo(CompleteModelInfo completeModelInfo) {
        this.completeModelInfo = completeModelInfo;
    }

    public List<String> getPythonVersions() {
        return pythonVersions;
    }

    public void setPythonVersions(List<String> pythonVersions) {
        this.pythonVersions = pythonVersions;
    }

    private static class ConfigBuilder {
        private static final GlobalConfig CONFIG;

        static {
            Optional<GlobalConfig> instance = YamlUtils.INSTANCE.readResourceFile(CONFIG_FILE, GlobalConfig.class);
            CONFIG = instance.orElse(null);
        }
    }

    public static GlobalConfig get() {
        return ConfigBuilder.CONFIG;
    }

    /**
     * 补全模型信息
     *
     * @since 2021-10-28
     */
    public static class CompleteModelInfo {
        /**
         * 旧配置文件名
         */
        private String oldConfig;

        /**
         * 新配置文件名
         */
        private String newConfig;

        /**
         * 配置文件父文件夹
         */
        private String parentDir;

        /**
         * 配置文件下载路径
         */
        private String downloadUrl;

        /**
         * 配置文件下载token
         */
        private String token;

        public String getOldConfig() {
            return oldConfig;
        }

        public void setOldConfig(String oldConfig) {
            this.oldConfig = oldConfig;
        }

        public String getNewConfig() {
            return newConfig;
        }

        public void setNewConfig(String newConfig) {
            this.newConfig = newConfig;
        }

        public String getParentDir() {
            return parentDir;
        }

        public void setParentDir(String parentDir) {
            this.parentDir = parentDir;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}