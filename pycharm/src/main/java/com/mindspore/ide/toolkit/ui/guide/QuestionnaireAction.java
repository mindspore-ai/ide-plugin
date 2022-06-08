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

package com.mindspore.ide.toolkit.ui.guide;

import com.mindspore.ide.toolkit.common.config.QuestionnaireConfig;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Questionnaire action
 *
 * @since 2022-6-6
 */
@Slf4j
public class QuestionnaireAction extends AnAction {
    private QuestionnaireConfig config = QuestionnaireConfig.get();

    private String url;

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        try {
            Desktop.getDesktop().browse(new URI(getUrl()));
            int num = PropertiesComponent.getInstance().getInt(config.getCacheFileName(), 0);
            PropertiesComponent.getInstance().setValue(config.getCacheFileName(), ++num, 6);
        } catch (IOException | URISyntaxException ex) {
            log.warn("url is invalid,because %s", ex.getMessage());
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
