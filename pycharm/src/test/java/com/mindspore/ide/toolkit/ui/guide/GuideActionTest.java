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

import com.intellij.notification.Notification;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * guide action test
 *
 * @author hanguisen
 * @since 1.7
 */
public class GuideActionTest {
    @Test
    public void testGuideAction() {
        AnActionEvent anActionEvent = Mockito.mock(AnActionEvent.class);
        Mockito.when(anActionEvent.getData(DataKey.create("Notification")))
                .thenReturn(Mockito.mock(Notification.class));
        DontAskAgainAction action = new DontAskAgainAction();
        action.actionPerformed(anActionEvent);
    }

    @Test(expected = NullPointerException.class)
    public void testDoNotAskAction() {
        AnActionEvent anActionEvent = Mockito.mock(AnActionEvent.class);
        Project project = Mockito.mock(Project.class);
        Mockito.when(project.getService(ToolWindowManager.class))
                .thenReturn(Mockito.mock(ToolWindowManager.class));
        Mockito.when(anActionEvent.getProject()).thenReturn(project);
        GuideAction action = new GuideAction();
        action.actionPerformed(anActionEvent);
    }
}
