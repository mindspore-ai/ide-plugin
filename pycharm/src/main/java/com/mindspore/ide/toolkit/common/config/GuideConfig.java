package com.mindspore.ide.toolkit.common.config;

import com.mindspore.ide.toolkit.common.utils.PathUtils;
import com.mindspore.ide.toolkit.common.utils.YamlUtils;
import lombok.Data;

import java.io.File;
import java.util.Optional;

@Data
public class GuideConfig {
    public static final String CONFIG_FILE = "config/guideConfig.yaml";

    public String guideUrl;
    private String guideConfig;
    private String guideConfigFile;
    private String pluginVersion;
    private String guideSettingUrl;

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

    public String getConfigFilePath(){
        return String.join(File.separator, PathUtils.getDefaultResourcePath(),getGuideConfig(),getGuideConfigFile());
    }
}
