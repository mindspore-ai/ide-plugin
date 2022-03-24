package com.mindspore.ide.toolkit.smartcomplete;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.util.SystemInfo;

import com.mindspore.ide.toolkit.common.ResourceManager;
import com.mindspore.ide.toolkit.common.events.EventCenter;
import com.mindspore.ide.toolkit.common.events.SmartCompleteEvents;
import com.mindspore.ide.toolkit.common.utils.FileUtils;
import com.mindspore.ide.toolkit.common.utils.NotificationUtils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 模型文件类
 * 负责下载模型文件；检查模型文件是否存在
 *
 * @since 2022-1-19
 */
@Slf4j
public class ModelFile {
    private final CompleteConfig completeConfig = CompleteConfig.get();

    private final CompleteConfig.Model currentModel = completeConfig.getCurrentModel();

    private final int queueCapacity = 100;

    private final ThreadPoolExecutor modelFileExecutor = new ThreadPoolExecutor(
            2 * Runtime.getRuntime().availableProcessors(),
            4 * Runtime.getRuntime().availableProcessors(),
            0L,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(queueCapacity),
            new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * is model exe exists
     *
     * @return boolean
     */
    public boolean modelExeExists() {
        String modelExeFullPath = completeConfig.getModelExeFullPath(currentModel);
        return FileUtils.getFile(modelExeFullPath).exists();
    }

    /**
     * download and unzip model zip
     *
     * @return boolean
     */
    public boolean fetchModelFile() {
        final String modelDownloadUrl = currentModel.getModelDownloadUrl();
        final String modelZipFullPath = completeConfig.getModelZipFullPath(currentModel);
        final String accessToken = currentModel.getAccessToken();

        int maxDownloadTimes = 3;
        EventCenter.INSTANCE.publish(new SmartCompleteEvents.DownloadCompleteModelStart());
        boolean isDownloadSucceed = false;
        for (int i = 0; i < maxDownloadTimes; i++) {
            isDownloadSucceed = ResourceManager.downloadResource(modelDownloadUrl,
                    modelZipFullPath, accessToken, CompleteConfig.DOWNLOAD_TIMEOUT);
            if (isDownloadSucceed) {
                break;
            }
        }
        if (isDownloadSucceed) {
            final String modelZipParentPath = completeConfig.getModelZipParentPath(currentModel);
            String unzipFileLocation = modelZipParentPath;
            if (completeConfig.isExtranet()) {
                unzipFileLocation = completeConfig.getModelUnzipFolderFullPath(currentModel);
            }
            EventCenter.INSTANCE.publish(new SmartCompleteEvents.DownloadCompleteModelEnd());
            ResourceManager.unzipResource(modelZipFullPath, unzipFileLocation);

            setFolderPermission(modelZipParentPath);
            deleteNoUseZipFile(modelZipParentPath);
        } else {
            NotificationUtils.notify(NotificationUtils.NotifyGroup.SMART_COMPLETE,
                    NotificationType.ERROR,
                    "Download complete model failed.");
        }
        log.info("Smart complete model download result:{}", isDownloadSucceed);
        return isDownloadSucceed;
    }

    /**
     * delete invalid model
     */
    public void deleteInvalidModelAsync() {
        modelFileExecutor.execute(() -> {
            String modelFolderPath = completeConfig.getModelFolderPath();
            try {
                Set<String> pluginVersionSet = FileUtils.listAllDirectory(Paths.get(modelFolderPath));
                for (String pluginVersion : pluginVersionSet) {
                    deleteInvalidModel(pluginVersion, modelFolderPath);
                }
            } catch (IOException ioException) {
                log.warn("Delete invalid model failed.", ioException);
            }
        });
    }

    /**
     * shut down executor
     */
    public void shutdownExecutor() {
        modelFileExecutor.shutdown();
    }

    private void deleteInvalidModel(String pluginVersion,
                                    String modelFolderPath) throws IOException {
        if (Objects.equals(currentModel.getPluginVersion(), pluginVersion)) {
            deleteInvalidModelWhenPluginVersionIsSame(pluginVersion);
        } else {
            FileUtils.deleteFile(new File(String.join(File.separator, modelFolderPath, pluginVersion)));
        }
    }

    private void deleteInvalidModelWhenPluginVersionIsSame(String pluginVersion) throws IOException {
        String pluginVersionPath = completeConfig.getPluginVersionFolderPath(pluginVersion);
        Set<String> modelVersionSet = FileUtils.listAllDirectory(Paths.get(pluginVersionPath));
        for (String modelVersion : modelVersionSet) {
            if (!Objects.equals(currentModel.getModelVersion(), modelVersion)) {
                FileUtils.deleteFile(new File(String.join(File.separator, pluginVersionPath, modelVersion)));
            }
        }
    }

    private void setFolderPermission(String modelZipParentPath) {
        if (SystemInfo.isLinux) {
            ProcessBuilder builder = new ProcessBuilder("/bin/chmod", "-R", "777", modelZipParentPath);
            try {
                builder.start().waitFor();
            } catch (IOException | InterruptedException exception) {
                log.warn("Change folder \"{}\" permission failed.", modelZipParentPath, exception);
            }
        }
    }

    private void deleteNoUseZipFile(String folderPath) {
        Set<String> fileNameSet = Collections.emptySet();
        try {
            fileNameSet = FileUtils.listAllFile(Paths.get(folderPath));
        } catch (IOException ioException) {
            log.warn("List model zip file failed.", ioException);
        }
        for (String fileName : fileNameSet) {
            if (fileName.endsWith(".zip")) {
                FileUtils.deleteFile(new File(String.join(File.separator, folderPath, fileName)));
            }
        }
    }
}