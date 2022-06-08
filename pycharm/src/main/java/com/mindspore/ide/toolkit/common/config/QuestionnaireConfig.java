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

import java.util.Optional;

/**
 * questionnaire entity
 *
 * @since 2022-6-6
 */
public class QuestionnaireConfig {
    /** config file path */
    public static final String CONFIG_FILE = "config/questionnaireConfig.yaml";

    private String cacheFileName;

    private String content;

    private String questionnaireUrl;

    private String versionCode;

    public String getCacheFileName() {
        return cacheFileName + versionCode;
    }

    public void setCacheFileName(String cacheFileName) {
        this.cacheFileName = cacheFileName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getQuestionnaireUrl() {
        return questionnaireUrl;
    }

    public void setQuestionnaireUrl(String questionnaireUrl) {
        this.questionnaireUrl = questionnaireUrl;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    private static class ConfigBuilder {
        private static final QuestionnaireConfig CONFIG;

        static {
            Optional<QuestionnaireConfig> instance = YamlUtils.INSTANCE.readResourceFile(CONFIG_FILE,
                    QuestionnaireConfig.class);
            CONFIG = instance.orElse(null);
        }
    }

    public static QuestionnaireConfig get() {
        return QuestionnaireConfig.ConfigBuilder.CONFIG;
    }
}
