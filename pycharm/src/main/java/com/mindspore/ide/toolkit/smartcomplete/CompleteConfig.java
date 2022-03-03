package com.mindspore.ide.toolkit.smartcomplete;

import com.google.gson.Gson;

import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.application.ApplicationNamesInfo;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.SystemInfo;

import com.mindspore.ide.toolkit.common.ResourceManager;
import com.mindspore.ide.toolkit.common.config.GlobalConfig;
import com.mindspore.ide.toolkit.common.utils.FileUtils;
import com.mindspore.ide.toolkit.common.utils.PathUtils;
import com.mindspore.ide.toolkit.common.utils.YamlUtils;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Data
public class CompleteConfig {
    /**
     * 下载超时时间（5分钟）
     */
    public static final int DOWNLOAD_TIMEOUT = 5 * 60 * 1000;

    private static final String DEFAULT_RESOURCE_PATH = PathUtils.getDefaultResourcePath();

    private static final String PLUGIN_VERSION = getPluginVersion();

    private static final String PRODUCT_NAME_WITH_EDITION = ApplicationNamesInfo
            .getInstance().getFullProductNameWithEdition();

    private final String defaultPluginVersion = "default_plugin_version";

    private final String lowLine = "_";

    private boolean extranet;

    private String localDir;

    private String modelFolder;

    private Map<String, ModelOfSpecificOs> modelMap;

    private static class ConfigBuilder {
        private static final CompleteConfig CONFIG;
        private static final GlobalConfig.CompleteModelInfo MODEL_INFO =
                GlobalConfig.get().getCompleteModelInfo();
        private static final String OLD_FILE;
        private static final String NEW_FILE;
        private static final String RESOURCE_FILE = "config/complete.yaml";

        static {
            OLD_FILE = String.join(File.separator,
                    DEFAULT_RESOURCE_PATH,
                    MODEL_INFO.getParentDir(),
                    PRODUCT_NAME_WITH_EDITION,
                    MODEL_INFO.getOldConfig());
            NEW_FILE = String.join(File.separator,
                    DEFAULT_RESOURCE_PATH,
                    MODEL_INFO.getParentDir(),
                    PRODUCT_NAME_WITH_EDITION,
                    MODEL_INFO.getNewConfig());

            if (SystemInfo.isWindows || SystemInfo.isLinux) {
                int maxDownloadTimes = 3;
                boolean downloadSucceed = false;
                for (int i = 0; i < maxDownloadTimes; i++) {
                    downloadSucceed = ResourceManager.downloadResource(MODEL_INFO.getDownloadUrl(),
                            NEW_FILE, MODEL_INFO.getToken(), DOWNLOAD_TIMEOUT);
                    if (downloadSucceed) {
                        break;
                    }
                }
                if (!downloadSucceed) {
                    log.warn("Download complete model config failed.");
                }
                CONFIG = getInstance().get();
            } else {
                CONFIG = getResourceInstance(RESOURCE_FILE).get();
            }
        }

        private static Optional<CompleteConfig> getInstance() {
            Optional<CompleteConfig> instance;
            if (Files.exists(Paths.get(OLD_FILE)) && Files.exists(Paths.get(NEW_FILE))) {
                // 下载下来的文件和已有文件是否有差异
                if (FileUtils.isSame(Paths.get(OLD_FILE), Paths.get(NEW_FILE))) {
                    // 无差异
                    instance = getLocalInstance(OLD_FILE);
                } else {
                    // 有差异
                    instance = getLocalInstance(NEW_FILE);
                    // 删掉旧配置文件并把下载的文件命名为配置文件
                    FileUtils.rename(OLD_FILE, NEW_FILE);
                }
            } else if (Files.exists(Paths.get(NEW_FILE))) {
                instance = getLocalInstance(NEW_FILE);
                // 删掉旧配置文件并把下载的文件命名为配置文件
                FileUtils.rename(OLD_FILE, NEW_FILE);
            } else if (Files.exists(Paths.get(OLD_FILE))) {
                instance = getLocalInstance(OLD_FILE);
            } else {
                instance = getResourceInstance(RESOURCE_FILE);
            }
            return instance;
        }

        private static Optional<CompleteConfig> getLocalInstance(String file) {
            return YamlUtils.INSTANCE.readLocalFile(file, CompleteConfig.class);
        }

        private static Optional<CompleteConfig> getResourceInstance(String file) {
            return YamlUtils.INSTANCE.readResourceFile(file, CompleteConfig.class);
        }
    }

    public static CompleteConfig get() {
        return ConfigBuilder.CONFIG;
    }

    /**
     * 模型zip文件全路径
     *
     * @param model 当前模型
     * @return 例如：D:/.mindspore/smartComplete/PyCharm Community Edition/model/1.6.1/1.0/onnx_windows.zip
     */
    public String getModelZipFullPath(Model model) {
        return String.join(File.separator,
                PathUtils.getDefaultResourcePath(),
                localDir,
                PRODUCT_NAME_WITH_EDITION,
                modelFolder,
                model.pluginVersion,
                model.modelVersion,
                model.modelZipName);
    }

    /**
     * 模型zip文件父路径
     *
     * @param model 当前模型
     * @return 例如：D:/.mindspore/smartComplete/PyCharm Community Edition/model/1.6.1/1.0
     */
    public String getModelZipParentPath(Model model) {
        return String.join(File.separator,
                PathUtils.getDefaultResourcePath(),
                localDir,
                PRODUCT_NAME_WITH_EDITION,
                modelFolder,
                model.pluginVersion,
                model.modelVersion);
    }

    /**
     * 模型压缩包解压后的文件夹的全路径
     *
     * @param model 当前模型
     * @return 例如：D:/.mindspore/smartComplete/PyCharm Community Edition/model/1.6.1/1.0/onnx_windows
     */
    public String getModelUnzipFolderFullPath(Model model) {
        return String.join(File.separator,
                PathUtils.getDefaultResourcePath(),
                localDir,
                PRODUCT_NAME_WITH_EDITION,
                modelFolder,
                model.pluginVersion,
                model.modelVersion,
                model.modelUnzipFolderName);
    }

    /**
     * 模型exe全路径
     *
     * @param model 当前模型
     * @return 例如：D:/.mindspore/smartComplete/PyCharm Community Edition/model/1.6.1/1.0/onnx_windows/grpc_server.exe
     */
    public String getModelExeFullPath(Model model) {
        return String.join(File.separator,
                PathUtils.getDefaultResourcePath(),
                localDir,
                PRODUCT_NAME_WITH_EDITION,
                modelFolder,
                model.pluginVersion,
                model.modelVersion,
                model.modelUnzipFolderName,
                model.modelExePath);
    }

    /**
     * model文件夹全路径
     *
     * @return 例如：D:/.mindspore/smartComplete/PyCharm Community Edition/model
     */
    public String getModelFolderPath() {
        return String.join(File.separator,
                PathUtils.getDefaultResourcePath(),
                localDir,
                PRODUCT_NAME_WITH_EDITION,
                modelFolder);
    }

    /**
     * 根据传入的pluginVersion获取该插件版本号对应的全路径
     *
     * @param pluginVersion plugin version
     * @return 例如：D:/.mindspore/smartComplete/PyCharm Community Edition/model/[传入的pluginVersion]
     */
    public String getPluginVersionFolderPath(@NotNull String pluginVersion) {
        return String.join(File.separator,
                PathUtils.getDefaultResourcePath(),
                localDir,
                PRODUCT_NAME_WITH_EDITION,
                modelFolder,
                pluginVersion);
    }

    /**
     * get current model
     *
     * @return current model
     */
    public Model getCurrentModel() {
        ModelOfSpecificOs modelOfSpecificOs = modelMap.get(PLUGIN_VERSION);
        Model model = null;
        if (modelOfSpecificOs != null) {
            model = modelOfSpecificOs.getModelOfCurrentOs();
        }
        if (model == null) {
            model = modelMap.get(defaultPluginVersion).getModelOfCurrentOs();
            model.setPluginVersion(defaultPluginVersion);
        } else {
            model.setPluginVersion(PLUGIN_VERSION);
        }
        return model;
    }

    /**
     * get old model in disk
     *
     * @return Optional<Model>
     */
    public Optional<Model> getOldModelInDisk() {
        Set<String> pluginVersionSet = Collections.emptySet();
        try {
            pluginVersionSet = FileUtils.listAllDirectory(Paths.get(getModelFolderPath()));
        } catch (IOException ioException) {
            log.error("List model file failed, cannot list plugin version.", ioException);
        }

        for (String pluginVersion : pluginVersionSet) {
            Set<String> modelVersionSet = Collections.emptySet();
            try {
                modelVersionSet = FileUtils.listAllDirectory(Paths.get(getPluginVersionFolderPath(pluginVersion)));
            } catch (IOException ioException) {
                log.error("List model file failed, cannot list model version.", ioException);
            }

            Model currentModel = getCurrentModel();
            for (String modelVersion : modelVersionSet) {
                if (!Objects.equals(pluginVersion, currentModel.pluginVersion)
                    || !Objects.equals(modelVersion, currentModel.modelVersion)) {
                    // 这里获得old Model
                    Model oldModel = cloneModel(currentModel);
                    oldModel.setModelVersion(modelVersion);
                    oldModel.setPluginVersion(pluginVersion);
                    return Optional.of(oldModel);
                }
            }
        }

        return Optional.empty();
    }

    private Model cloneModel(Model model) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(model), Model.class);
    }

    private static String getPluginVersion() {
        // pluginIdStr为build.gradle里面的group（'com.mindspore'）
        String pluginIdStr = "com.mindspore";
        return PluginManagerCore.getPlugin(PluginId.getId(pluginIdStr)).getVersion();
    }

    /**
     * ModelOfSpecificOs
     *
     * @since 2022-2-11
     */
    @Getter
    @Setter
    public static class ModelOfSpecificOs {
        private Model windows;
        private Model linux;

        /**
         * 获取当前操作系统对应的最新的模型
         *
         * @return 模型实例
         */
        public Model getModelOfCurrentOs() {
            if (SystemInfo.isWindows) {
                return windows;
            } else {
                return linux;
            }
        }
    }

    /**
     * Model
     *
     * @since 2022-1-19
     */
    @Getter
    @Setter
    public static class Model {
        private String modelDownloadUrl;

        private String accessToken;

        private String modelZipName;

        private String modelUnzipFolderName;

        private String modelExePath;

        private String modelVersion;

        private String pluginVersion;

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof Model)) {
                return false;
            }

            Model other = (Model) obj;
            if (!Objects.equals(modelDownloadUrl, other.modelDownloadUrl)) {
                return false;
            } else if (!Objects.equals(accessToken, other.accessToken)) {
                return false;
            } else if (!Objects.equals(modelZipName, other.modelZipName)) {
                return false;
            } else if (!Objects.equals(modelUnzipFolderName, other.modelUnzipFolderName)) {
                return false;
            } else if (!Objects.equals(modelExePath, other.modelExePath)) {
                return false;
            } else if (!Objects.equals(modelVersion, other.modelVersion)) {
                return false;
            } else if (!Objects.equals(pluginVersion, other.pluginVersion)) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + (modelDownloadUrl == null ? 0 : modelDownloadUrl.hashCode());
            result = 31 * result + (accessToken == null ? 0 : accessToken.hashCode());
            result = 31 * result + (modelZipName == null ? 0 : modelZipName.hashCode());
            result = 31 * result + (modelUnzipFolderName == null ? 0 : modelUnzipFolderName.hashCode());
            result = 31 * result + (modelExePath == null ? 0 : modelExePath.hashCode());
            result = 31 * result + (modelVersion == null ? 0 : modelVersion.hashCode());
            return 31 * result + (pluginVersion == null ? 0 : pluginVersion.hashCode());
        }
    }
}