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

package com.mindspore.ide.toolkit.common.dialoginfo;

import com.mindspore.ide.toolkit.common.enums.EnumProperties;
import com.mindspore.ide.toolkit.common.exceptions.MsToolKitException;
import com.mindspore.ide.toolkit.ui.errordialog.ExceptionDialog;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.jetbrains.python.packaging.PyExecutionException;
import com.jetbrains.python.sdk.PythonSdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 异常弹窗中需展示的信息。
 * 通过调用{@link ExceptionDialogInfo#parseException}即可生成Exception对应的弹窗内容。
 *
 * @since 1.0
 */
public class ExceptionDialogInfo extends DialogInfo {
    private static final Pattern ERROR_PATTERN = Pattern.compile(".*error:.*", Pattern.CASE_INSENSITIVE);

    /**
     * 描述
     */
    private String description;

    /**
     * 执行的命令
     */
    private String command;

    /**
     * 执行命令后的输出信息
     */
    private String output;

    /**
     * 针对出现的错误的解决方式
     */
    private String solution;

    private ExceptionDialogInfo(Builder builder) {
        this.setSuccessful(builder.isSuccessful);
        this.setTitle(builder.title);
        this.description = builder.description;
        this.command = builder.command;
        this.output = builder.output;
        this.solution = builder.solution;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    @Override
    public void showDialog() {
        if (isSuccessful()) {
            return;
        }
        ApplicationManager.getApplication().invokeLater(() -> {
            showDialogSync();
        });
    }

    /**
     * builder for cmd dialog
     */
    public static class Builder {
        private boolean isSuccessful;

        private String title;

        private String description;

        private String command;

        private String output;

        private String solution;

        /**
         * build cmd dialog
         *
         * @return cmd dialog
         */
        public ExceptionDialogInfo build() {
            return new ExceptionDialogInfo(this);
        }

        /**
         * build successful info
         *
         * @param isSuccessful is success
         * @return Builder
         */
        public Builder isSuccessful(boolean isSuccessful) {
            this.isSuccessful = isSuccessful;
            return this;
        }

        /**
         * build dialog title
         *
         * @param title title
         * @return builder
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * build description
         *
         * @param description des
         * @return builder
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * build command
         *
         * @param command command
         * @return Builder
         */
        public Builder command(String command) {
            this.command = command;
            return this;
        }

        /**
         * build output
         *
         * @param output output info
         * @return builder
         */
        public Builder output(String output) {
            this.output = output;
            return this;
        }

        /**
         * build solution
         *
         * @param solution str
         * @return builder
         */
        public Builder solution(String solution) {
            this.solution = solution;
            return this;
        }
    }

    /**
     * 解析异常，将异常转换成弹窗中需展示的信息
     *
     * @param exception 异常
     * @param extraArgs 额外参数，一般不传入。执行conda命令时，如果用的是sdk的方式，需要传入对应sdk实例。
     * @return ExceptionDialogInfo
     */
    public static ExceptionDialogInfo parseException(@NotNull Exception exception,
                                                     Object... extraArgs) {
        if (exception instanceof PyExecutionException) {
            return parsePyExecutionException(exception, extraArgs);
        } else if (exception instanceof MsToolKitException) {
            MsToolKitException msToolKitException = (MsToolKitException) exception;
            String errMsg = msToolKitException.getErrMsg();
            if (errMsg != null) {
                return new Builder()
                        .description(errMsg)
                        .solution(msToolKitException.getSolution())
                        .build();
            }
        } else {
            String errMessage = exception.getMessage();
            if (errMessage != null) {
                return new Builder()
                        .description(errMessage)
                        .build();
            }
        }

        return new ExceptionDialogInfo.Builder()
                .description("Unknown exception")
                .build();
    }

    private static ExceptionDialogInfo parsePyExecutionException(Exception exception, Object... extraArgs) {
        if (!(exception instanceof PyExecutionException)) {
            return new ExceptionDialogInfo.Builder()
                    .description("Unknown exception")
                    .build();
        }

        Sdk sdk = null;
        if (extraArgs != null && extraArgs.length == 1) {
            Object obj = extraArgs[0];
            if (obj instanceof Sdk) {
                sdk = (Sdk) obj;
            }
        }
        PyExecutionException pyExecutionException = (PyExecutionException) exception;

        final String stdoutReason = findErrorReason(pyExecutionException.getStdout());
        final String stderrReason = findErrorReason(pyExecutionException.getStderr());
        final String reason = stdoutReason == null ? stderrReason : stdoutReason;

        final String message = reason == null ? pyExecutionException.getMessage() : reason;
        final String command = pyExecutionException.getCommand() + " "
                + StringUtil.join(pyExecutionException.getArgs(), " ");
        final String solution = getPyExecutionErrorSolution(pyExecutionException, reason, sdk);
        final String output = pyExecutionException.getStdout()
                + System.lineSeparator()
                + pyExecutionException.getStderr();

        return new ExceptionDialogInfo.Builder()
                .description(message)
                .command(command)
                .output(output)
                .solution(solution)
                .build();
    }

    @Nullable
    private static String findErrorReason(@NotNull String errorStr) {
        final Matcher matcher = ERROR_PATTERN.matcher(errorStr);
        if (matcher.find()) {
            String result = matcher.group();
            return result == null? null : result.trim();
        }
        return null;
    }

    @Nullable
    private static String getPyExecutionErrorSolution(@NotNull PyExecutionException pyExecutionException,
                                                      @Nullable String reason,
                                                      @Nullable Sdk sdk) {
        if (reason != null) {
            return getPyExecutionErrorSolutionFromReason(reason, sdk);
        }

        if (SystemInfo.isLinux && (containsInOutOrErr(pyExecutionException, "pyconfig.h")
                || containsInOutOrErr(pyExecutionException, "Python.h"))) {
            return EnumProperties.EXCEPTION_SOLUTION_PROPERTIES.getProperty("install.python.into.computer");
        }

        if ("pip".equals(pyExecutionException.getCommand())) {
            if (sdk == null) {
                return EnumProperties.EXCEPTION_SOLUTION_PROPERTIES.getProperty("has.pip.in.python.interpreter");
            } else {
                return EnumProperties.EXCEPTION_SOLUTION_PROPERTIES
                        .getProperty("has.pip.in.python.interpreter.locate", sdk.getHomePath());
            }
        }

        return null;
    }

    private static boolean containsInOutOrErr(@NotNull PyExecutionException pyExecutionException, @NotNull String what) {
        return StringUtil.containsIgnoreCase(pyExecutionException.getStdout(), what)
                || StringUtil.containsIgnoreCase(pyExecutionException.getStderr(), what);
    }

    @Nullable
    private static String getPyExecutionErrorSolutionFromReason(String reason, Sdk sdk) {
        if (StringUtil.containsIgnoreCase(reason, "CondaHTTPError")) {
            return EnumProperties.EXCEPTION_SOLUTION_PROPERTIES.getProperty("check.network");
        } else if (StringUtil.containsIgnoreCase(reason, "proxy")) {
            return EnumProperties.EXCEPTION_SOLUTION_PROPERTIES.getProperty("check.conda.proxy");
        } else if (StringUtil.containsIgnoreCase(reason, "SyntaxError")) {
            if (sdk == null) {
                return EnumProperties.EXCEPTION_SOLUTION_PROPERTIES.getProperty("use.right.python");
            } else {
                return EnumProperties.EXCEPTION_SOLUTION_PROPERTIES
                        .getProperty("use.right.python.version", PythonSdkType.getLanguageLevelForSdk(sdk));
            }
        } else {
            return null;
        }
    }

    private void showDialogSync() {
        String defaultTitle = "Something wrong";
        String title = getTitle();
        StringBuilder messageSb = new StringBuilder();

        if (title == null || title.equals("")) {
            messageSb.append(defaultTitle);
            title = defaultTitle;
        } else {
            messageSb.append(title).append(" failed.");
        }
        if (solution != null && !solution.equals("")) {
            messageSb.append(System.lineSeparator()).append(solution);
        }
        messageSb.append(System.lineSeparator()).append(System.lineSeparator())
                .append("Do you want to check error message?");

        int choiceCode = Messages.showYesNoDialog(messageSb.toString(), title, Messages.getErrorIcon());
        if (choiceCode == Messages.YES) {
            new ExceptionDialog(this).show();
        }
    }
}