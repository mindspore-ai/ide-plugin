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
import com.intellij.openapi.projectRoots.Sdk;
import com.mindspore.ide.toolkit.common.dialog.DialogInfo;
import com.mindspore.ide.toolkit.common.enums.EnumHardWarePlatform;
import com.mindspore.ide.toolkit.common.enums.EnumNotifyGroup;
import com.mindspore.ide.toolkit.common.utils.*;
import com.mindspore.ide.toolkit.services.notify.EventNotifyServiceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class MindSporeService {
    private static final Logger LOG = LoggerFactory.getLogger(MindSporeService.class);
    private static final String TEMP_PATH = PropertiesUtil.getProperty("mindspore.template.zip.path");
    private static final String TEMP_PATH_ENTRIES = PropertiesUtil.getProperty("mindspore.template.zip.path.entries");
    private static final String TEMP_PATH_STRUCTURE = PropertiesUtil.getProperty("mindspore.path.structure");

    private static EventNotifyServiceProxy eventNotifyServiceProxy = EventNotifyServiceProxy.INSTANCE;

    public static Boolean createMindSporeTemplate(String targetFilePath, String fileName) {
        if (fileName.isEmpty() || fileName.equals("<empty>")) {
            return true;
        }
        try {
            ZipCompressingUtils.unzipFile(TEMP_PATH + fileName + ".zip", targetFilePath);
            return true;
        } catch (FileNotFoundException exception) {
            eventNotifyServiceProxy.eventNotify(EnumNotifyGroup.MIND_SPORE,
                    "Unable to create MindSpore template",
                    "zip file not find, unable to create MindSpore template",
                    exception, NotificationType.ERROR);
            return false;
        } catch (IOException exception) {
            eventNotifyServiceProxy.eventNotify(EnumNotifyGroup.MIND_SPORE,
                    "Unable to create MindSpore template",
                    "IOException", exception, NotificationType.ERROR);
            return false;
        }
    }

    public static List<String> listTemplates() {
        try (InputStream input = MindSporeService.class.getResourceAsStream(TEMP_PATH_ENTRIES);
             InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)){
            return GsonUtils.INSTANCE.getGson().fromJson(reader,
                    TypeToken.getParameterized(List.class, String.class).getType());
        } catch (IOException exception) {
            return new ArrayList<>();
        }
    }

    public static Boolean createStructure(String baseDir) {
        List<String> dirs;
        try (InputStream input = MindSporeService.class.getResourceAsStream(TEMP_PATH_STRUCTURE);
             InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)){
            dirs = GsonUtils.INSTANCE.getGson().fromJson(reader,
                    TypeToken.getParameterized(List.class, String.class).getType());
        } catch (IOException exception) {
            return false;
        }
        for (String dir : dirs){
            try {
                Files.createDirectories(Path.of(baseDir + File.separator + dir));
            } catch (IOException exception){
                return false;
            }
        }
        return true;
    }

    public static DialogInfo installMindSporeIntoConda(String hardwarePlatform, Sdk sdk) {
        List<String> cmdList;
        //conda这里需要完善获取命令的方法
        if (hardwarePlatform.contains(EnumHardWarePlatform.CPU.getCode())) {
            cmdList = Arrays.asList("install", String.format("mindspore-%s=1.5.0",
                            EnumHardWarePlatform.CPU.getCode().toLowerCase(Locale.ROOT)),
                    "-c", "mindspore", "-c", "conda-forge", "-y");
        } else if (hardwarePlatform.contains(EnumHardWarePlatform.GPU.getCode())) {
            String version = hardwarePlatform.split(" ")[2];
            cmdList = Arrays.asList("install", String.format("mindspore-%s=1.5.0",
                            EnumHardWarePlatform.GPU.getCode().toLowerCase(Locale.ROOT)),
                    String.format("cudatoolkit=%s", version), "cudnn", "-c", "mindspore",
                    "-c", "conda-forge", "-y");
        } else {
            cmdList = Arrays.asList("install", String.format("mindspore-%s=1.5.0",
                            EnumHardWarePlatform.ASCEND.getCode().toLowerCase(Locale.ROOT)),
                    "-c", "mindspore", "-c", "conda-forge", "-y");
        }
        return CondaCmdProcessor.executeCondaCmd(sdk, null, cmdList);
    }
}
