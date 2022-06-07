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

package com.mindspore.ide.toolkit.quesionnaire;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.mindspore.ide.toolkit.common.config.QuestionnaireConfig;
import com.mindspore.ide.toolkit.common.events.ProjectEvents;
import com.mindspore.ide.toolkit.ui.guide.QuestionnaireAction;
import lombok.extern.slf4j.Slf4j;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;

/**
 * Questionnaire Listener
 *
 * @since 2022-6-6
 */
@Listener(references = References.Strong)
@Slf4j
public class QuestionnaireListener {
    private static final int QUESTION_PROP_VALUE = 5;

    private QuestionnaireConfig config = QuestionnaireConfig.get();

    /**
     * add questionnire notification and action
     *
     * @param projectOpen project
     */
    @Handler
    public void showBalloon(ProjectEvents.ProjectOpen projectOpen) {
        int openPluginNum = PropertiesComponent.getInstance().getInt(config.getCacheFileName(), 0);
        if (openPluginNum == QUESTION_PROP_VALUE) {
            Notification notification = new Notification(Notifications.SYSTEM_MESSAGES_GROUP_ID,
                    "",
                    config.getContent(),
                    NotificationType.INFORMATION,
                    NotificationListener.URL_OPENING_LISTENER);
            AnAction anAction = ActionManager.getInstance()
                    .getAction("mindsporeQuestionnaireAction");
            if (anAction instanceof QuestionnaireAction) {
                QuestionnaireAction questionnaireAction = (QuestionnaireAction) anAction;
                questionnaireAction.setUrl(config.getQuestionnaireUrl());
                notification.addAction(questionnaireAction);
            }
            Notifications.Bus.notify(notification);
        } else {
            if (openPluginNum < QUESTION_PROP_VALUE) {
                PropertiesComponent.getInstance()
                        .setValue(config.getCacheFileName(), ++openPluginNum, 0);
            }
        }
    }
}
