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
 * search doc config
 *
 * @since 2022-3-10
 */
public class DocSearchConfig {
    public static final String CONFIG_FILE = "config/searchConfig.yaml";

    private String searchJs;

    private String searchApi;

    public String getSearchJs() {
        return searchJs;
    }

    public void setSearchJs(String searchJs) {
        this.searchJs = searchJs;
    }

    public String getSearchApi() {
        return searchApi;
    }

    public void setSearchApi(String searchApi) {
        this.searchApi = searchApi;
    }

    private static class ConfigBuilder {
        private static final DocSearchConfig CONFIG;

        static {
            Optional<DocSearchConfig> instance = YamlUtils.INSTANCE.readResourceFile(CONFIG_FILE,
                    DocSearchConfig.class);
            CONFIG = instance.orElse(null);
        }
    }

    public static DocSearchConfig get() {
        return ConfigBuilder.CONFIG;
    }
}