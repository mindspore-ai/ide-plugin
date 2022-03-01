package com.mindspore.ide.toolkit.common.config;

import com.mindspore.ide.toolkit.common.utils.YamlUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Data
public class GlobalConfig {

    public static final String CONFIG_FILE="config/globalConfig.yaml";

    private String resourceFolder;

    private String downloadBaseUrl;

    private String toolWindowName;

    private String toolWindowUrl;

    private String toolWindowSearchContent;

    private String msEnvValidatorFile;

    private CompleteModelInfo completeModelInfo;

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

    /**
     * 补全模型信息
     *
     * @since 2021-10-28
     */
    @Getter
    @Setter
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
    }
}

