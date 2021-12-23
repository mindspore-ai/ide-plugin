package com.mindspore.ide.toolkit.smartcomplete;

import com.mindspore.ide.toolkit.common.utils.PathUtils;
import com.mindspore.ide.toolkit.common.utils.YamlUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Optional;

@Slf4j
@Data
public class CompleteConfig {
    private static final String CONFIG_FILE = "config/complete.yaml";

    private String modelDownloadUrl;

    private String accessToken;

    private String localDir;

    private String modelFolder;

    private String modelZipName;

    private String modelUnzipFolderName;

    private String modelExePath;

    private static class ConfigBuilder {
        private static final CompleteConfig CONFIG;

        static {
            Optional<CompleteConfig> instance = YamlUtils.INSTANCE.readResourceFile(CONFIG_FILE, CompleteConfig.class);
            CONFIG = instance.orElse(null);
        }
    }

    public static CompleteConfig get() {
        return ConfigBuilder.CONFIG;
    }

    public String getModelZipFullPath() {
        return String.join(File.separator, PathUtils.getDefaultResourcePath(), localDir, modelFolder, modelZipName);
    }

    public String getModelZipParentPath() {
        return String.join(File.separator, PathUtils.getDefaultResourcePath(), localDir, modelFolder);
    }

    public String getModelUnzipFolderFullPath() {
        return String.join(File.separator, PathUtils.getDefaultResourcePath(), localDir, modelFolder, modelUnzipFolderName);
    }

    public String getModelExeFullPath() {
        return String.join(File.separator, PathUtils.getDefaultResourcePath(), localDir, modelFolder, modelUnzipFolderName, modelExePath);
    }
}