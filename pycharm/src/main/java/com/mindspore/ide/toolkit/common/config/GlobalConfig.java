package com.mindspore.ide.toolkit.common.config;

import com.mindspore.ide.toolkit.common.utils.YamlUtils;
import lombok.Data;

import java.util.Optional;

@Data
public class GlobalConfig {

    public static final String CONFIG_FILE="config/globalConfig.yaml";

    private String resourceFolder;

    private String downloadBaseUrl;

    private String toolWindowName;

    private String toolWindowUrl;

    private String toolWindowSearchContent;

    private static class ConfigBuilder{
        private static final GlobalConfig CONFIG;

        static {
            Optional<GlobalConfig> instance = YamlUtils.INSTANCE.readResourceFile(CONFIG_FILE,GlobalConfig.class);
            CONFIG = instance.orElse(null);
        }
    }

    public static GlobalConfig get(){
        return ConfigBuilder.CONFIG;
    }
}

