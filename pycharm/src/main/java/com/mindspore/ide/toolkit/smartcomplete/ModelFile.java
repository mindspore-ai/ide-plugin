package com.mindspore.ide.toolkit.smartcomplete;

import com.intellij.notification.NotificationType;
import com.mindspore.ide.toolkit.common.ResourceManager;
import com.mindspore.ide.toolkit.common.events.EventCenter;
import com.mindspore.ide.toolkit.common.events.SmartCompleteEvents;
import com.mindspore.ide.toolkit.common.utils.FileUtils;
import com.mindspore.ide.toolkit.common.utils.NotificationUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
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

    private final String modelDownloadUrl = completeConfig.getModelDownloadUrl();

    private final String modelZipFullPath = completeConfig.getModelZipFullPath(completeConfig.getModel());

    private final String modelZipParentPath = completeConfig.getModelZipParentPath(completeConfig.getModel());

    private final String modelExeFullPath = completeConfig.getModelExeFullPath(completeConfig.getModel());

    private final String accessToken = completeConfig.getAccessToken();

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
        return FileUtils.getFile(modelExeFullPath).exists();
    }

    /**
     * download and unzip model zip
     *
     * @return boolean
     */
    public boolean fetchModelFile() {
        int maxDownloadTimes = 3;
        EventCenter.INSTANCE.publish(new SmartCompleteEvents.DownloadCompleteModelStart());
        boolean isDownloadSucceed = false;
        for (int i = 0; i < maxDownloadTimes; i++) {
            isDownloadSucceed = ResourceManager.downloadResource(modelDownloadUrl,
                    modelZipFullPath, accessToken);
            if (isDownloadSucceed) {
                break;
            }
        }
        if (isDownloadSucceed) {
            EventCenter.INSTANCE.publish(new SmartCompleteEvents.DownloadCompleteModelEnd());
            ResourceManager.unzipResource(modelZipFullPath, modelZipParentPath);
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
     *
     * @param completeConfig CompleteConfig
     */
    public void deleteInvalidModelAsync(CompleteConfig completeConfig) {
        modelFileExecutor.execute(() -> {
            String modelFolderPath = completeConfig.getModelFolderPath();
            try {
                Set<String> directoryNameSet = FileUtils.listAllDirectory(Paths.get(modelFolderPath));
                for (String directoryName : directoryNameSet) {
                    deleteInvalidModel(completeConfig, directoryName, modelFolderPath);
                }
            } catch (IOException ioException) {
                log.error("Delete invalid model failed.", ioException);
            }
        });
    }

    /**
     * shut down executor
     */
    public void shutdownExecutor() {
        modelFileExecutor.shutdown();
    }

    private void deleteInvalidModel(CompleteConfig completeConfig,
                                    String directoryName,
                                    String modelFolderPath) {
        if (!Objects.equals(completeConfig.getModel().getModelVersion(), directoryName)) {
            FileUtils.deleteFile(new File(String.join(File.separator, modelFolderPath, directoryName)));
        }
    }
}