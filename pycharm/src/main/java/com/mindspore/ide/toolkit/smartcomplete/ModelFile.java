package com.mindspore.ide.toolkit.smartcomplete;

import com.mindspore.ide.toolkit.common.ResourceManager;
import com.mindspore.ide.toolkit.common.events.EventCenter;
import com.mindspore.ide.toolkit.common.events.SmartCompleteEvents;
import com.mindspore.ide.toolkit.common.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModelFile {
    private final CompleteConfig completeConfig = CompleteConfig.get();

    private final String modelDownloadUrl = completeConfig.getModelDownloadUrl();

    private final String modelZipFullPath = completeConfig.getModelZipFullPath();

    private final String modelZipParentPath = completeConfig.getModelZipParentPath();

    private final String modelExeFullPath = completeConfig.getModelExeFullPath();

    private final String accessToken = completeConfig.getAccessToken();

    public boolean modelExeExists() {
        return FileUtils.getFile(modelExeFullPath).exists();
    }

    public void fetchModelFile() {
        EventCenter.INSTANCE.publish(new SmartCompleteEvents.DownloadCompleteModelStart());
        boolean downloadSucceed = ResourceManager.downloadResource(modelDownloadUrl,
                modelZipFullPath, accessToken);
        log.info("Smart complete model download result:{}", downloadSucceed);
        if (downloadSucceed) {
            EventCenter.INSTANCE.publish(new SmartCompleteEvents.DownloadCompleteModelEnd());
            ResourceManager.unzipResource(modelZipFullPath, modelZipParentPath);
        }
    }
}