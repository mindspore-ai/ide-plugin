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

package com.mindspore.ide.toolkit.ui.errordialog;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import com.mindspore.ide.toolkit.common.dialog.DialogInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * cmd dialog info
 *
 * @since 1.0
 */
@Getter
@Setter
public class CmdDialogInfo extends DialogInfo {
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

    private CmdDialogInfo(Builder builder) {
        this.setSuccessful(builder.isSuccessful);
        this.setTitle(builder.title);
        this.description = builder.description;
        this.command = builder.command;
        this.output = builder.output;
        this.solution = builder.solution;
    }

    @Override
    public void showDialog() {
        if (!isSuccessful()) {
            ApplicationManager.getApplication().invokeLater(() -> {
                int choiceCode = Messages.showYesNoDialog(getTitle() + " failed. "
                        + solution, getTitle(), Messages.getErrorIcon());
                if (choiceCode == 0) {
                    new CmdExecuteErrorDialog(this).show();
                }
            });
        }
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
        public CmdDialogInfo build() {
            return new CmdDialogInfo(this);
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
}

