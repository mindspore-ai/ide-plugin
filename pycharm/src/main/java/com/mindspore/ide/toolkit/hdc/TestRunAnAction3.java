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

package com.mindspore.ide.toolkit.hdc;

import com.intellij.codeInsight.daemon.GutterMark;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.Constraints;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorGutterComponentEx;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 点击事件
 *
 * @since 2022-04-18
 */
public class TestRunAnAction3 extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        addAction(editor);
    }

    private void addAction(Editor editor) {
        if (editor.getGutter() instanceof EditorGutterComponentEx) {
            EditorGutterComponentEx gutter = (EditorGutterComponentEx) editor.getGutter();
            for (int i = 0; i < 2000; i++) {
                List<GutterMark> gutterMarks = gutter.getGutterRenderers(i);
                for (GutterMark gutterMark : gutterMarks) {
                    getGutterMark(gutterMark);
                }
            }
        }
    }

    private void getGutterMark(GutterMark gutterMark) {
        if (gutterMark instanceof GutterIconRenderer) {
            GutterIconRenderer gutterIconRenderer = (GutterIconRenderer) gutterMark;
            if (gutterIconRenderer.getIcon().equals(AllIcons.RunConfigurations.TestState.Run)
                    && gutterIconRenderer.getPopupMenuActions() instanceof DefaultActionGroup) {
                DefaultActionGroup actionGroup = (DefaultActionGroup) gutterIconRenderer
                        .getPopupMenuActions();
                AnAction[] anActions = actionGroup.getChildActionsOrStubs();
                groupAddAction(anActions, actionGroup);
            }
        }
    }

    private void groupAddAction(AnAction[] anActions, DefaultActionGroup actionGroup) {
        boolean isB = false;
        for (AnAction anAction : anActions) {
            if (anAction instanceof TestRunAnAction2) {
                isB = true;
                break;
            }
        }
        if (!isB) {
            actionGroup.addAction(ActionManager
                            .getInstance()
                            .getAction("TestRunAnAction2"),
                    Constraints.LAST,
                    ActionManager.getInstance());
        }
    }
}