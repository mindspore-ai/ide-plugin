package com.mindspore.ide.toolkit.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
    private static final String DEFAULT_PATH = "messages/mindspore.properties";
    private static Properties prop = new Properties();

    static {
        registerProperties(DEFAULT_PATH);
    }

    private PropertiesUtil() {

    }

    public static void registerProperties(final String path) {
        try (InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                return;
            }
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(final String key) {
        return prop.getProperty(key);
    }


}
