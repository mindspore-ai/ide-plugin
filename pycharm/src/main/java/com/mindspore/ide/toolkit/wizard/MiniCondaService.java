package com.mindspore.ide.toolkit.wizard;

import com.intellij.notification.*;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.projectRoots.Sdk;
import com.mindspore.ide.toolkit.common.utils.HttpUtils;
import com.mindspore.ide.toolkit.common.utils.NotificationUtils;
import com.mindspore.ide.toolkit.common.utils.RunExecUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class MiniCondaService {
    public boolean downloadMiniConda(String path) {
        final boolean[] isSuccess = {false};
        Task.WithResult withResult = new Task.WithResult(null, "download and install miniconda", false) {
            @Override
            protected Sdk compute(@NotNull ProgressIndicator indicator) {
                if (OSInfoUtils.isWindows()) {
                    isSuccess[0] = downloadMiniCondaWindows(path);
                } else if (OSInfoUtils.isLinux()) {
                    isSuccess[0] = downloadMiniCondaLinux(path);
                } else if (OSInfoUtils.isMacOS()) {
                    isSuccess[0] = downloadMiniCondaMacOS(path);
                }
                return null;
            }
        };
        ProgressManager.getInstance().run(withResult);
        return isSuccess[0];
    }

    private boolean downloadMiniCondaWindows(String path) {
        try {
            String exePath = path + File.separator + "Miniconda3-latest-Windows-x86_64.exe";
            HttpUtils.download("https://repo.anaconda.com/miniconda/Miniconda3-py39_4.10.3-Windows-x86_64.exe", exePath, 30000);
            NotificationUtils.notify(NotificationUtils.NotifyGroup.NEW_PROJECT,
                    NotificationType.INFORMATION,
                    "download MiniConda success");
            installWindowPackage(exePath, path);
            NotificationUtils.notify(NotificationUtils.NotifyGroup.NEW_PROJECT,
                    NotificationType.INFORMATION,
                    "install MiniConda success");
            return true;
        } catch (IOException e) {
            NotificationUtils.notify(NotificationUtils.NotifyGroup.NEW_PROJECT,
                    NotificationType.ERROR,
                    "Miniconda download installation failed");
            log.info(e.getMessage());
            return false;
        }
    }

    private String installWindowPackage(String sdkPath, String localFilePath) throws IOException {
        String installPath = "/D=" + localFilePath + File.separator + "Miniconda3";
        log.info("installPath:{}", installPath);
        String cmdReturnInfo = RunExecUtils.
                runExec(Arrays.asList(sdkPath, "/InstallationType=JustMe", "/AddToPath=1", "/RegisterPython=0", "/S", installPath));
        return cmdReturnInfo;
    }

    private boolean downloadMiniCondaLinux(String path) {
        try {
            String shPath = path + File.separator + "Miniconda3-latest-Linux-x86_64.sh";
            HttpUtils.download("https://repo.anaconda.com/miniconda/Miniconda3-latest-Linux-x86_64.sh", shPath, 30000);
            NotificationUtils.notify(NotificationUtils.NotifyGroup.NEW_PROJECT,
                    NotificationType.INFORMATION,
                    "download MiniConda success");
            installLinuxPackage(shPath, path);
            NotificationUtils.notify(NotificationUtils.NotifyGroup.NEW_PROJECT,
                    NotificationType.INFORMATION,
                    "install MiniConda success");
            return true;
        } catch (IOException e) {
            NotificationUtils.notify(NotificationUtils.NotifyGroup.NEW_PROJECT,
                    NotificationType.ERROR,
                    "Miniconda download installation failed");
            log.info(e.getMessage());
            return false;
        }
    }

    private String installLinuxPackage(String shPath, String localFilePath) throws IOException {
        String installPath = File.separator + localFilePath + File.separator + "Miniconda3";
        log.info("installPath:{}", installPath);
        log.info("shPath:{}", shPath);
        String cmdReturnInfo = RunExecUtils.
                runExec(Arrays.asList("bash", shPath, "-b", "-p", installPath));
        return cmdReturnInfo;
    }

    private boolean downloadMiniCondaMacOS(String path) {
        try {
            String shPath = path + File.separator + "Miniconda3-latest-MacOSX-x86_64.sh";
            HttpUtils.download("https://repo.anaconda.com/miniconda/Miniconda3-latest-MacOSX-x86_64.sh", shPath, 30000);
            NotificationUtils.notify(NotificationUtils.NotifyGroup.NEW_PROJECT,
                    NotificationType.INFORMATION,
                    "download MiniConda success");
            installMacOSPackage(shPath, path);
            NotificationUtils.notify(NotificationUtils.NotifyGroup.NEW_PROJECT,
                    NotificationType.INFORMATION,
                    "install MiniConda success");
            return true;
        } catch (IOException e) {
            NotificationUtils.notify(NotificationUtils.NotifyGroup.NEW_PROJECT,
                    NotificationType.ERROR,
                    "Miniconda download installation failed");
            log.info(e.getMessage());
            return false;
        }
    }

    private String installMacOSPackage(String shPath, String localFilePath) throws IOException {
        String installPath = File.separator + localFilePath + File.separator + "Miniconda3";
        log.info("installPath:{}", installPath);
        log.info("shPath:{}", shPath);
        String cmdReturnInfo = RunExecUtils.
                runExec(Arrays.asList("bash", shPath, "-b", "-p", installPath));
        return cmdReturnInfo;
    }
}