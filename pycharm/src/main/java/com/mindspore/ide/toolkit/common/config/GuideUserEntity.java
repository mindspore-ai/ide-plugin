package com.mindspore.ide.toolkit.common.config;

import com.mindspore.ide.toolkit.common.utils.YamlUtils;
import lombok.Data;

import java.util.Optional;

@Data
public class GuideUserEntity {
    private boolean askAgain;

    private String version;

    private String notificationTitle;

    private String notificationContent;

    private static class ConfigBuilder {
        private static final GuideUserEntity CONFIG;

        static {
            Optional<GuideUserEntity> instance = YamlUtils.INSTANCE.readConfigFile(GuideConfig.get().getConfigFilePath(), GuideUserEntity.class);
            CONFIG = instance.orElse(null);
        }
    }

    public static GuideUserEntity get() {
        return ConfigBuilder.CONFIG;
    }
}
