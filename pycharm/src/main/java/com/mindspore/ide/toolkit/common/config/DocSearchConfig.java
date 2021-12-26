package com.mindspore.ide.toolkit.common.config;

import com.mindspore.ide.toolkit.common.utils.YamlUtils;
import lombok.Data;

import java.util.Optional;

@Data
public class DocSearchConfig {
    public static final String CONFIG_FILE="config/searchConfig.yaml";

    private String searchJs;

    private String searchApi;


    private static class ConfigBuilder{
        private static final DocSearchConfig CONFIG;

        static {
            Optional<DocSearchConfig> instance = YamlUtils.INSTANCE.readResourceFile(CONFIG_FILE,DocSearchConfig.class);
            CONFIG = instance.orElse(null);
        }
    }

    public static DocSearchConfig get(){
        return ConfigBuilder.CONFIG;
    }
}
