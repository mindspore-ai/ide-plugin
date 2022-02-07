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

import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.projectRoots.Sdk;
import com.jetbrains.python.sdk.flavors.PyCondaRunKt;
import com.mindspore.ide.toolkit.common.config.GlobalConfig;
import com.mindspore.ide.toolkit.common.utils.PathUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * MindSpore开发环境校验
 *
 * @since 2022-01-13
 */
@Slf4j
public class MsEnvValidator {
    private MsEnvValidator() {}

    /**
     * 校验mindspore是否安装成功
     *
     * @param sdk Sdk
     * @return MsEnvStatus
     */
    public static MsEnvStatus validateMindSpore(Sdk sdk) {
        String validatorFileFullPathStr = String.join(File.separator,
                PathUtils.getDefaultResourcePath(),
                GlobalConfig.get().getMsEnvValidatorFile());
        Path validatorFileFullPath = Path.of(validatorFileFullPathStr);
        if (!Files.exists(validatorFileFullPath)) {
            final byte[] fileContent = ("import mindspore"
                    + System.lineSeparator() + "mindspore.run_check()")
                    .getBytes(StandardCharsets.UTF_8);
            try {
                Files.write(validatorFileFullPath, fileContent);
            } catch (IOException ioException) {
                log.error("MsEnvValidator.validateMindSpore-Write python file failed.", ioException);
                return MsEnvStatus.UNKNOWN;
            }
        }

        MsEnvStatus msEnvStatus = MsEnvStatus.UNAVAILABLE;
        try {
            ProcessOutput output = PyCondaRunKt.runCondaPython(sdk.getHomePath(),
                    Arrays.asList(validatorFileFullPathStr));

            String stdout = output.getStdout();
            log.info("MsEnvValidator.validateMindSpore-Execute validator file succeed, stdout is {}", stdout);
            if (stdout != null && stdout.contains("MindSpore has been installed successfully")) {
                msEnvStatus = MsEnvStatus.AVAILABLE;
            }
        } catch (ExecutionException executionException) {
            log.error("MsEnvValidator.validateMindSpore-Execute validator file failed.", executionException);
            msEnvStatus = MsEnvStatus.UNKNOWN;
        }

        return msEnvStatus;
    }

    /**
     * mindspore环境的状态
     */
    public enum MsEnvStatus {
        /**
         * 可用
         */
        AVAILABLE,

        /**
         * 不可用
         */
        UNAVAILABLE,

        /**
         * 未知
         */
        UNKNOWN
    }
}
