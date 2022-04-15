/*
 * Copyright 2021-2022 Huawei Technologies Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mindspore.ide.toolkit.wizard;

import com.google.gson.reflect.TypeToken;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.mindspore.ide.toolkit.common.dialoginfo.DialogInfo;
import com.mindspore.ide.toolkit.common.dialoginfo.ExceptionDialogInfo;
import com.mindspore.ide.toolkit.common.enums.EnumHardWarePlatform;
import com.mindspore.ide.toolkit.common.enums.EnumProperties;
import com.mindspore.ide.toolkit.common.exceptions.MsToolKitException;
import com.mindspore.ide.toolkit.common.utils.GsonUtils;
import com.mindspore.ide.toolkit.common.utils.NotificationUtils;
import com.mindspore.ide.toolkit.common.utils.ZipCompressingUtils;
import org.jetbrains.annotations.NotNull;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * for install mindspoe and initialize project
 *
 * @author gaochen
 * @since 2022-1-28
 */
@Slf4j
public class MindSporeService {
    private static final String TEMP_PATH = EnumProperties.MIND_SPORE_PROPERTIES.getProperty("mindspore.template.zip"
            + ".path");
    private static final String TEMP_PATH_ENTRIES = EnumProperties.MIND_SPORE_PROPERTIES.getProperty("mindspore"
            + ".template.zip.path.entries");
    private static final String TEMP_PATH_STRUCTURE = EnumProperties.MIND_SPORE_PROPERTIES.getProperty("mindspore"
            + ".path.structure");

    /**
     * create mindspore template
     *
     * @param targetFilePath save path
     * @param fileName filename
     * @return is success
     */
    public static Boolean createMindSporeTemplate(String targetFilePath, String fileName) {
        if (fileName.isEmpty() || fileName.equals("<empty>")) {
            return true;
        }
        try {
            ZipCompressingUtils.unzipFile(TEMP_PATH + fileName + ".zip", targetFilePath);
            return true;
        } catch (FileNotFoundException exception) {
            NotificationUtils.notify(NotificationUtils.NotifyGroup.NEW_PROJECT,
                    NotificationType.ERROR,
                    "Unable to create MindSpore template.");
            log.warn("zip file not find, unable to create MindSpore template", exception);
            return false;
        } catch (IOException exception) {
            NotificationUtils.notify(NotificationUtils.NotifyGroup.NEW_PROJECT,
                    NotificationType.ERROR,
                    "Unable to create MindSpore template.");
            log.warn("Unable to create MindSpore template.", exception);
            return false;
        }
    }

    /**
     * list templates
     *
     * @return the list
     */
    public static List<String> listTemplates() {
        try (InputStream input = MindSporeService.class.getResourceAsStream(TEMP_PATH_ENTRIES);
            InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            return GsonUtils.INSTANCE.getGson().fromJson(reader,
                    TypeToken.getParameterized(List.class, String.class).getType());
        } catch (IOException exception) {
            return new ArrayList<>();
        }
    }

    /**
     * create project starting files
     *
     * @param baseDir path
     * @return is success
     */
    public static Boolean createStructure(String baseDir) {
        List<String> dirs;
        try (InputStream input = MindSporeService.class.getResourceAsStream(TEMP_PATH_STRUCTURE);
            InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            dirs = GsonUtils.INSTANCE.getGson().fromJson(reader,
                    TypeToken.getParameterized(List.class, String.class).getType());
        } catch (IOException exception) {
            return false;
        }
        for (String dir : dirs) {
            try {
                Files.createDirectories(Path.of(baseDir + File.separator + dir));
            } catch (IOException exception) {
                return false;
            }
        }
        return true;
    }

    /**
     * install mindspore
     *
     * @param hardwarePlatform hardware platform
     * @param sdk sdk
     * @return message
     * @throws MsToolKitException when error
     */
    public static DialogInfo installMindSporeIntoConda(String hardwarePlatform, Sdk sdk) throws MsToolKitException {
        List<String> cmdList;   // conda这里需要完善获取命令的方法
        if (hardwarePlatform.contains(EnumHardWarePlatform.CPU.getCode())) {
            cmdList = Arrays.asList("install", String.format("mindspore-%s",
                            EnumHardWarePlatform.CPU.getCode().toLowerCase(Locale.ROOT)),
                    "-c", "mindspore", "-c", "conda-forge", "-y");
        } else if (hardwarePlatform.contains(EnumHardWarePlatform.GPU.getCode())) {
            String version = hardwarePlatform.split(" ")[2];
            cmdList = Arrays.asList("install", String.format("mindspore-%s",
                            EnumHardWarePlatform.GPU.getCode().toLowerCase(Locale.ROOT)),
                    String.format("cudatoolkit=%s", version), "cudnn", "-c", "mindspore",
                    "-c", "conda-forge", "-y");
        } else if (hardwarePlatform.contains(EnumHardWarePlatform.ASCEND.getCode())) {
            cmdList = Arrays.asList("install", String.format("mindspore-%s",
                            EnumHardWarePlatform.ASCEND.getCode().toLowerCase(Locale.ROOT)),
                    "-c", "mindspore", "-c", "conda-forge", "-y");
        } else {
            throw new MsToolKitException("install MindSpore: hardware platform not supported");
        }
        return CondaCmdProcessor.executeCondaCmd(sdk, null, cmdList);
    }

    /**
     * get a task for running
     *
     * @param project project
     * @param hardware hardware
     * @param sdk sdk
     * @return the task
     */
    public static Task.WithResult<Boolean, Exception> installMindSporeTask(Project project, String hardware, Sdk sdk) {
        return new Task.WithResult(project, "Install MindSpore into Conda", true) {
            @Override
            public Boolean compute(@NotNull ProgressIndicator indicator) {
                try {
                    DialogInfo dialogInfo = MindSporeService.installMindSporeIntoConda(hardware, sdk);
                    dialogInfo.showDialog("Install MindSpore into conda");
                    boolean isMsEnvValidate =
                        MsEnvValidator.validateMindSpore(sdk) == MsEnvValidator.MsEnvStatus.AVAILABLE;
                    if (!isMsEnvValidate && dialogInfo.isSuccessful()) {
                        new ExceptionDialogInfo.Builder()
                                .isSuccessful(false)
                                .description("Failed to run the verification script. There maybe something wrong."
                                        + " If MindSpore is abnormal, you can reinstall it.")
                                .build().showDialog("Install MindSpore into conda");
                    }
                    return isMsEnvValidate && dialogInfo.isSuccessful();
                } catch (MsToolKitException msToolKitException) {
                    ExceptionDialogInfo.parseException(msToolKitException).showDialog("Install MindSpore into conda");
                    return false;
                }
            }
        };
    }
}
