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
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.mindspore.ide.toolkit.common.beans.PythonCommand;
import com.mindspore.ide.toolkit.common.beans.PythonPackageInfo;
import com.mindspore.ide.toolkit.common.enums.EnumError;
import com.mindspore.ide.toolkit.common.enums.EnumHardWarePlatform;
import com.mindspore.ide.toolkit.common.enums.EnumNotifyGroup;
import com.mindspore.ide.toolkit.common.exceptions.BizException;
import com.mindspore.ide.toolkit.common.exceptions.CommonException;
import com.mindspore.ide.toolkit.common.utils.*;
import com.mindspore.ide.toolkit.services.notify.EventNotifyServiceProxy;
import org.apache.commons.lang.StringUtils;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MindSporeServiceImpl implements MindSporeService {
    private static final Logger LOG = LoggerFactory.getLogger(MindSporeServiceImpl.class);
    private static final String PIP_UNINSTALL = " -m pip uninstall -y ";
    private static final String TEMP_PATH = PropertiesUtil.getProperty("mindspore.template.zip.path");
    private static final String TEMP_PATH_ENTRIES = PropertiesUtil.getProperty("mindspore.template.zip.path.entries");
    private static final String TEMP_PATH_STRUCTURE = PropertiesUtil.getProperty("mindspore.path.structure");
    private static final String ASTROID = "astroid";

    private EventNotifyServiceProxy eventNotifyServiceProxy = EventNotifyServiceProxy.INSTANCE;

    private MindSporeServiceImpl() {
    }

    @Override
    public void installMindSpore(String downloadUrl, String hpName, String sdkPath, String localFileCacheDir) {
        try {
            String mindsporeFilePath = MindSporeUtils.createDownloadDirPath(downloadUrl, localFileCacheDir);
            LOG.info("MindSporeFilePath: {}", mindsporeFilePath);
            LOG.info("sdkHomePath: {}", sdkPath);
            if (isSpecialMindSporeExist(sdkPath, hpName)) {
                LOG.info("MindSpore installed before.");
                eventNotifyServiceProxy.eventNotify(EnumNotifyGroup.MIND_SPORE, "MindSpore installed before.", NotificationType.INFORMATION);
                return;
            }
            HttpUtils.download(downloadUrl, mindsporeFilePath);

            uninstallMindSpore(sdkPath);
            String cmdReturnInfo = installLocalWhlPackage(sdkPath, mindsporeFilePath);

            if (isSpecialMindSporeExist(sdkPath, hpName)) {
                eventNotifyServiceProxy.eventNotify(EnumNotifyGroup.MIND_SPORE,
                        "MindSpore installed successfully.", NotificationType.INFORMATION);
            } else {
                eventNotifyServiceProxy.eventNotify(EnumNotifyGroup.MIND_SPORE,
                        "MindSpore install failed." + System.lineSeparator() + cmdReturnInfo,
                        NotificationType.INFORMATION);
            }
        } catch (SocketTimeoutException | HttpHostConnectException exception) {
            LOG.error(exception.getMessage(), exception);
            eventNotifyServiceProxy.eventNotify(EnumNotifyGroup.MIND_SPORE,
                    "setup failed. Please add your intellij http proxy", NotificationType.WARNING);
        } catch (IOException exception) {
            LOG.error(exception.getMessage(), exception);
            installMindSporeWarning();
        }
    }

    @Override
    public Boolean createJsonFile(String filePath) {
        File file = new File(filePath + File.separator + PropertiesUtil.getProperty("project.identify.name"));
        boolean createFlag = false;
        try {
            createFlag = file.createNewFile();
        } catch (IOException exception) {
            LOG.error(exception.getMessage(), exception);
        }
        return createFlag;
    }

    @Override
    public Boolean createMindSporeTemplate(String targetFilePath, String fileName) {
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

    @Override
    public List<String> listTemplates() {
        try (InputStream input = MindSporeServiceImpl.class.getResourceAsStream(TEMP_PATH_ENTRIES);
             InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)){
            return GsonUtils.INSTANCE.getGson().fromJson(reader,
                    TypeToken.getParameterized(List.class, String.class).getType());
        } catch (IOException exception) {
            return new ArrayList<>();
        }
    }

    @Override
    public void installAstroid(Sdk sdk, String localFileCacheDir) {
        try {
            if (MindSporeUtils.isPackageExist(ASTROID, sdk)) {
                eventNotifyServiceProxy.eventNotify(EnumNotifyGroup.MIND_SPORE,
                        "astroid installed before", NotificationType.INFORMATION);
                return;
            }
            String astroidDestPath = astroidWhlPkgFileCopyToProject(localFileCacheDir);
            String result = installLocalWhlPackage(sdk.getHomePath(), astroidDestPath);
            if (MindSporeUtils.isPackageExist(ASTROID, sdk)) {
                eventNotifyServiceProxy.eventNotify(EnumNotifyGroup.MIND_SPORE,
                        "astroid installed successfully.", NotificationType.INFORMATION);
            } else {
                eventNotifyServiceProxy.eventNotify(EnumNotifyGroup.MIND_SPORE,
                        "astroid install failed." + System.lineSeparator() + result, NotificationType.WARNING);

            }
        } catch (BizException | IOException exception) {
            LOG.error(exception.getMessage(), exception);
            installAstroidWarning();
        }
    }

    @Override
    public Boolean createStructure(String baseDir) {
        List<String> dirs;
        try (InputStream input = MindSporeServiceImpl.class.getResourceAsStream(TEMP_PATH_STRUCTURE);
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

    private String astroidWhlPkgFileCopyToProject(String localFileCacheDir) throws IOException, BizException {
        File astroidDestFile = new File(localFileCacheDir + File.separator + "astroid-2.5.0-py3-none-any.whl");
        if (!astroidDestFile.exists()) {
            boolean success = astroidDestFile.createNewFile();
            if (!success) {
                throw new BizException(EnumError.FILE_CREATE_FAIL.getErrCode(),
                        EnumError.FILE_CREATE_FAIL.getErrMsg() + astroidDestFile.getCanonicalPath());
            }
        } else {
            LOG.warn("unexpected file exist");
        }
        try (InputStream inputStream = MindSporeServiceImpl.class.getResourceAsStream(
                "/staticAnalyzerastroid-2.5.0-py3-none-any.whl");
             OutputStream outputStream = new FileOutputStream(astroidDestFile)) {
            byte[] buff = new byte[1024];
            int length;
            while ((length = inputStream.read(buff)) > 0) {
                outputStream.write(buff, 0, length);
            }
        }
        return astroidDestFile.getCanonicalPath();
    }

    private boolean isSpecialMindSporeExist(String sdkPath, String hpName) throws IOException {
        Optional<PythonPackageInfo> packageInfo = MindSporeUtils.queryMindSporePackage(sdkPath);
        if (!packageInfo.isPresent()) {
            return false;
        }
        EnumHardWarePlatform hardWarePlatform = EnumHardWarePlatform.findByCode(hpName).orElse(null);
        if (hardWarePlatform == null || hardWarePlatform.getMindsporeMapping().isEmpty()) {
            return false;
        }

        return hardWarePlatform.getMindsporeMapping().equals(packageInfo.get().getName()) &&
                PropertiesUtil.getProperty("mindspore.version.identify").equals(packageInfo.get().getVersion());
    }

    private void uninstallMindSpore(String sdkPath) throws IOException {
        Optional<String> mindsporeVersionOp = getMindSporePackageNameInPython(sdkPath);
        if (mindsporeVersionOp.isPresent()) {
            LOG.info("Uninstall MindSpore package: {}", mindsporeVersionOp.get());
            Runtime.getRuntime().exec(sdkPath + PIP_UNINSTALL + mindsporeVersionOp.get());
        }
    }

    private String installLocalWhlPackage(String sdkPath, String localFilePath) throws IOException {
        String cmdReturnInfo = RunExecUtils.runExec(Arrays.asList(sdkPath,
                PythonCommand.CHAR_MODULE, PythonCommand.PIP,
                PythonCommand.INSTALL, localFilePath));
        return cmdReturnInfo;
    }

    private Optional<String> getMindSporePackageNameInPython(String sdkPath) throws IOException {
        if (sdkPath == null) {
            return Optional.empty();
        }
        for (EnumHardWarePlatform hardWarePlatform : EnumHardWarePlatform.values()) {
            String result = RunExecUtils.runExec(Arrays.asList(sdkPath,
                    PythonCommand.CHAR_MODULE, PythonCommand.PIP,
                    PythonCommand.SHOW, hardWarePlatform.getMindsporeMapping()));
            if (StringUtils.isNotBlank(result) && result.contains("Home-page")) {
                return Optional.of(hardWarePlatform.getMindsporeMapping());
            }
        }
        return Optional.empty();
    }

    private void installMindSporeWarning() {
        eventNotifyServiceProxy.eventNotify(EnumNotifyGroup.MIND_SPORE,
                "MindSpore has not installed. Please check it",
                NotificationType.WARNING);
    }

    private void installAstroidWarning() {
        eventNotifyServiceProxy.eventNotify(EnumNotifyGroup.MIND_SPORE,
                "Astroid install failed. Please check it",
                NotificationType.INFORMATION);
    }

    private static class Singleton {
        private static final MindSporeService INSTANCE = new MindSporeServiceImpl();

        private Singleton() {
        }
    }

    /**
     * get mindspore service instance
     *
     * @return mindspore service
     */
    public static MindSporeService getInstance() {
        return Singleton.INSTANCE;
    }
}
