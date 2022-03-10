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

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * EnumProperties
 *
 * @since 2022-1-1
 */
@Slf4j
public enum EnumProperties {
    MIND_SPORE_PROPERTIES("messages/mindspore.properties"),
    EXCEPTION_SOLUTION_PROPERTIES("messages/ExceptionSolution.properties"),
    MY_BUNDLE_PROPERTIES("messages/MyBundle.properties");

    private String resourceFilePath;

    private Properties prop;

    EnumProperties(String resourceFilePath) {
        this.resourceFilePath = resourceFilePath;
        this.prop = new Properties();
        registerProperties();
    }

    private void registerProperties() {
        if (resourceFilePath == null) {
            log.warn("resourceFilePath is null.");
            return;
        }
        try (InputStream in = EnumProperties.class.getClassLoader().getResourceAsStream(resourceFilePath)) {
            prop.load(in);
        } catch (IOException ioException) {
            log.error("Register properties failed.", ioException);
        }
    }

    /**
     * getProperty
     *
     * @param key String
     * @return String
     */
    public String getProperty(String key) {
        return prop.getProperty(key);
    }

    /**
     * getProperty
     *
     * @param key  String
     * @param args T
     * @param <T>  T
     * @return String
     */
    public <T> String getProperty(String key, T... args) {
        return String.format(prop.getProperty(key), args);
    }
}