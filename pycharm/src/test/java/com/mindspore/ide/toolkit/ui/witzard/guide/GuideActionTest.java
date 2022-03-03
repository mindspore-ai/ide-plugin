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

package com.mindspore.ide.toolkit.ui.witzard.guide;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.mindspore.ide.toolkit.ui.guide.DontAskAgainAction;
import com.mindspore.ide.toolkit.ui.guide.GuideAction;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * guide action test
 *
 * @author hanguisen
 * @since 1.7
 */
public class GuideActionTest {

    @Test(expected = IllegalStateException.class)
    public void testAction() {
        AnActionEvent actionEvent = Mockito.mock(AnActionEvent.class);
        DontAskAgainAction action = new DontAskAgainAction();
        action.actionPerformed(actionEvent);
    }

    @Test(expected = NullPointerException.class)
    public void testGuideAction() {
        AnActionEvent anActionEvent = Mockito.mock(AnActionEvent.class);
        GuideAction guideAction = new GuideAction();
        guideAction.actionPerformed(anActionEvent);
    }
}