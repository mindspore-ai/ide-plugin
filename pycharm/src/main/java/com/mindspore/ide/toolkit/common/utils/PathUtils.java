package com.mindspore.ide.toolkit.common.utils;

import com.intellij.openapi.application.PathManager;
import com.mindspore.ide.toolkit.common.config.GlobalConfig;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathUtils {
    private static final String MINDSPORE_PATH = "mindspore";

    private static final int FREE_SPACE = 1024 * 1024 * 1024;

    public static String getInstallRootPath() {
        String installPath = System.getProperty("user.home");
        String rootPath = System.getenv("SystemDrive");
        if (RegularUtils.isEmpty(rootPath)) {
            return installPath;
        }
        File[] allRoots = File.listRoots();
        for (File file : allRoots) {
            if (!file.getPath().startsWith(rootPath) && file.getUsableSpace() > FREE_SPACE) {
                installPath = file.getPath();
                break;
            }
        }
        return installPath;
    }

    public static String getDefaultResourcePath() {
        return getInstallRootPath() + File.separator + GlobalConfig.get().getResourceFolder();
    }

    public static void initResourceFolder() {
        File file = FileUtils.getFile(getInstallRootPath());
        if (!file.exists()) {
            if (file.mkdir()) {
                FileUtils.hideFile(file);
            }
        }
    }

    public static Path getUserCommonDataPath() {
        return PathManager.getCommonDataPath();
    }

    public static Path getIdeHomePath() {
        return Paths.get(PathManager.getHomePath());
    }

    public static Path getIdePluginsPath() {
        return Paths.get(PathManager.getPluginsPath());
    }

    public static Path getMindSporePath() {
        return Paths.get(getIdePluginsPath().toString(), MINDSPORE_PATH);
    }
}
