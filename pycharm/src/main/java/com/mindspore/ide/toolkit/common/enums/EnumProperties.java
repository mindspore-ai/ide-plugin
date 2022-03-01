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
     * @param key String
     * @param args T
     * @param <T> T
     * @return String
     */
    public <T> String getProperty(String key, T... args) {
        return String.format(prop.getProperty(key), args);
    }
}
