package com.mindspore.ide.toolkit.wizard;

import com.intellij.notification.*;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.jetbrains.python.packaging.PyCondaPackageService;
import com.mindspore.ide.toolkit.common.utils.HttpUtils;
import com.mindspore.ide.toolkit.common.utils.NotificationUtils;
import com.mindspore.ide.toolkit.common.utils.OSInfoUtils;
import com.mindspore.ide.toolkit.common.utils.RunExecUtils;
import com.mindspore.ide.toolkit.common.utils.RegularUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class MiniCondaService {
    private static final String WINDOWS_CONDA_PARENT_PATH_NAME = "Scripts";

    private static final String LINUX_CONDA_PARENT_PATH_NAME = "bin";

    private static final String WINDOWS_CONDA_NAME = "conda.exe";

    private static final String LINUX_CONDA_NAME = "conda";

    private static final String CONDA_ENVS_PATH = "envs";

    /**
     * get a task for running to download MiniConda
     *
     * @param path download path
     * @return task
     */
    public static boolean downloadMiniCondaTask(String path) {
        Task.WithResult withResult = new Task.WithResult<Boolean, Exception>(
                null,
                "Download and Install Miniconda",
                false) {
            @Override
            protected Boolean compute(@NotNull ProgressIndicator indicator) {
                return downloadMiniConda(path);
            }
        };
        return (Boolean) ProgressManager.getInstance().run(withResult);
    }

    /**
     * set conda executable path to ide cache
     *
     * @param path path to be set
     * @return full path of executable
     */
    public static String setCondaExePathToIde(String path) {
        // get conda exe path
        String condaPath;
        if (!path.equals("")) {
            if (OSInfoUtils.INSTANCE.isWindows()) {
                String condaExePath = path + File.separator + "Miniconda3" + File.separator
                        + WINDOWS_CONDA_PARENT_PATH_NAME + File.separator + WINDOWS_CONDA_NAME;
                PyCondaPackageService.onCondaEnvCreated(condaExePath);
                condaPath = condaExePath;
                log.info("windows condaExePath:{}", condaExePath);
            } else {
                String condaExePath = path + File.separator + "Miniconda3" + File.separator
                        + LINUX_CONDA_PARENT_PATH_NAME + File.separator + LINUX_CONDA_NAME;
                PyCondaPackageService.onCondaEnvCreated(condaExePath);
                condaPath = condaExePath;
                log.info("other condaExePath:{}", condaExePath);
            }
        } else {
            condaPath = PyCondaPackageService.getCondaExecutable(null);
            log.info("First entry acquisition path : {}", condaPath);
        }
        return condaPath;
    }

    /**
     * get conda env path
     *
     * @param condaPath conda path
     * @return full conda path
     */
    public static String getCondaEnvsPath(String condaPath) {
        if (RegularUtils.isEmpty(condaPath)) {
            return "";
        }
        File file = new File(condaPath);
        String condaEnvPath = file.getParent();
        if (condaEnvPath.endsWith(WINDOWS_CONDA_PARENT_PATH_NAME)
                || condaEnvPath.endsWith(LINUX_CONDA_PARENT_PATH_NAME)) {
            File fileNew = new File(condaEnvPath);
            condaEnvPath = fileNew.getParent();
        }
        return condaEnvPath + File.separator + CONDA_ENVS_PATH + File.separator;
    }

    private static boolean downloadMiniConda(String path) {
        try {
            switch (OSInfoUtils.INSTANCE.getOs()) {
                case Linux: {
                    String shPath = path + File.separator + "Miniconda3-latest-Linux-x86_64.sh";
                    HttpUtils.download("https://repo.anaconda.com/miniconda/Miniconda3-latest-Linux-x86_64.sh",
                            shPath, 30000);
                    NotificationUtils.notify(NotificationUtils.NotifyGroup.NEW_PROJECT,
                            NotificationType.INFORMATION, "Download MiniConda success");
                    installLinuxPackage(shPath, path);
                    NotificationUtils.notify(NotificationUtils.NotifyGroup.NEW_PROJECT,
                            NotificationType.INFORMATION, "Install MiniConda success");
                    break;
                }
                case Windows: {
                    String exePath = path + File.separator + "Miniconda3-latest-Windows-x86_64.exe";
                    HttpUtils.download(
                            "https://repo.anaconda.com/miniconda/Miniconda3-py39_4.10.3-Windows-x86_64.exe",
                            exePath, 30000);
                    NotificationUtils.notify(NotificationUtils.NotifyGroup.NEW_PROJECT,
                            NotificationType.INFORMATION, "Download MiniConda success");
                    installWindowPackage(exePath, path);
                    NotificationUtils.notify(NotificationUtils.NotifyGroup.NEW_PROJECT,
                            NotificationType.INFORMATION, "Install MiniConda success");
                    break;
                }
                case Mac_OS: {
                    String shPath = path + File.separator + "Miniconda3-latest-MacOSX-x86_64.sh";
                    HttpUtils.download("https://repo.anaconda.com/miniconda/Miniconda3-latest-MacOSX-x86_64.sh",
                            shPath, 30000);
                    NotificationUtils.notify(NotificationUtils.NotifyGroup.NEW_PROJECT,
                            NotificationType.INFORMATION, "Download MiniConda success");
                    installMacOSPackage(shPath, path);
                    NotificationUtils.notify(NotificationUtils.NotifyGroup.NEW_PROJECT,
                            NotificationType.INFORMATION, "Install MiniConda success");
                    break;
                }
                default: {
                    NotificationUtils.notify(NotificationUtils.NotifyGroup.NEW_PROJECT,
                            NotificationType.INFORMATION, "OS not supported!");
                }
            }
            return true;
        } catch (IOException e) {
            NotificationUtils.notify(NotificationUtils.NotifyGroup.NEW_PROJECT,
                    NotificationType.ERROR, "Miniconda download installation failed");
            return false;
        }
    }

    private static String installWindowPackage(String sdkPath, String localFilePath) throws IOException {
        String installPath = "/D=" + localFilePath + File.separator + "Miniconda3";
        log.info("installPath:{}", installPath);
        log.info("shPath:{}", sdkPath);
        return RunExecUtils.runExec(Arrays.asList(sdkPath,
                "/InstallationType=JustMe", "/AddToPath=1", "/RegisterPython=0", "/S", installPath));
    }

    private static String installLinuxPackage(String shPath, String localFilePath) throws IOException {
        String installPath = File.separator + localFilePath + File.separator + "Miniconda3";
        log.info("installPath:{}", installPath);
        log.info("shPath:{}", shPath);
        return RunExecUtils.runExec(Arrays.asList("bash", shPath, "-b", "-p", installPath));
    }

    private static String installMacOSPackage(String shPath, String localFilePath) throws IOException {
        String installPath = File.separator + localFilePath + File.separator + "Miniconda3";
        log.info("installPath:{}", installPath);
        log.info("shPath:{}", shPath);
        return RunExecUtils.runExec(Arrays.asList("bash", shPath, "-b", "-p", installPath));
    }
}