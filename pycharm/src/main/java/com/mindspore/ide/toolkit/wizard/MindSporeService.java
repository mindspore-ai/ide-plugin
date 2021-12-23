package com.mindspore.ide.toolkit.wizard;

import com.intellij.openapi.projectRoots.Sdk;

import java.util.List;

public interface MindSporeService {
    void installMindSpore(String downloadUrl, String hpName, String sdkPath, String localFileCacheDir);

    Boolean createJsonFile(String filePath);

    Boolean createMindSporeTemplate(String targetFilePath, String fileName);

    List<String> listTemplates();

    void installAstroid(Sdk sdk, String localFileCacheDir);

    Boolean createStructure(String targetFilePath);
}
