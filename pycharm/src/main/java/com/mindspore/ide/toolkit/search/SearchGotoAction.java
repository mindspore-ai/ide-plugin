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

package com.mindspore.ide.toolkit.search;

import com.intellij.ide.DataManager;
import com.intellij.ide.actions.SearchEverywhereBaseAction;
import com.intellij.ide.actions.ShowSettingsUtilImpl;
import com.intellij.ide.ui.search.OptionDescription;
import com.intellij.ide.util.gotoByName.GotoActionModel;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.ex.ActionManagerEx;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.wm.IdeFocusManager;
import org.intellij.lang.annotations.JdkConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.InputEvent;

/**
 * search go to action
 *
 * @since 2022-1-30
 * @author hanguisen
 */
public class SearchGotoAction extends SearchEverywhereBaseAction {

    /**
     * action performed
     *
     * @param actionEvent action event
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent actionEvent) {

    }

    /**
     * open option or perform action
     *
     * @param object object
     * @param myText text info
     * @param mindsporeProject project info
     * @param uiComponent component
     * @param modify modify
     */
    public static void openOptionOrPerformAction(Object object,
        String myText,
        @Nullable Project mindsporeProject,
        @Nullable Component uiComponent,
        @JdkConstants.InputEventMask int modify) {
        // invoke later to let the Goto Action popup close completely before the action is performed
        // and avoid focus issues if the action shows complicated popups itself
        ApplicationManager.getApplication().invokeLater(() -> {
            if (mindsporeProject != null && mindsporeProject.isDisposed())
                return;

            if (object instanceof OptionDescription) {
                OptionDescription optionDescription = (OptionDescription) object;
                if (optionDescription.hasExternalEditor()) {
                    optionDescription.invokeInternalEditor();
                } else {
                    ShowSettingsUtilImpl.showSettingsDialog(mindsporeProject, optionDescription.getConfigurableId(), myText);
                }
            } else {
                IdeFocusManager.getInstance(mindsporeProject).doWhenFocusSettlesDown(
                        () -> performAction(object, uiComponent, null, modify, null));
            }
        });
    }

    /**
     * require projects
     *
     * @return true or false
     */
    @Override
    protected boolean requiresProject() {
        return false;
    }

    /**
     * perform action
     *
     * @param element element
     * @param myComponent component
     * @param anActionEvent action event
     * @param modifiers modifiers
     * @param runnable runnable
     */
    private static void performAction(Object element,
        @Nullable Component myComponent,
        @Nullable AnActionEvent anActionEvent,
        @JdkConstants.InputEventMask int modifiers,
        @Nullable Runnable runnable) {
        // element could be AnAction (SearchEverywhere)
        if (myComponent == null)
            return;
        AnAction action = element instanceof AnAction ? (AnAction) element : ((GotoActionModel.ActionWrapper) element).getAction();
        ApplicationManager.getApplication().invokeLater(() -> {
            DataManager instance = DataManager.getInstance();
            DataContext context = instance != null ? instance.getDataContext(myComponent) : DataContext.EMPTY_CONTEXT;
            InputEvent inputEvent = anActionEvent != null ? anActionEvent.getInputEvent() : null;
            AnActionEvent fromAnAction = AnActionEvent.createFromAnAction(action, inputEvent, ActionPlaces.ACTION_SEARCH, context);
            if (inputEvent == null && modifiers != 0) {
                fromAnAction = new AnActionEvent(null, fromAnAction.getDataContext(), fromAnAction.getPlace(), fromAnAction.getPresentation(), fromAnAction.getActionManager(), modifiers);
            }

            if (ActionUtil.lastUpdateAndCheckDumb(action, fromAnAction, false)) {
                if (action instanceof ActionGroup && !((ActionGroup) action).canBePerformed(context)) {
                    ListPopup actionGroupPopup = JBPopupFactory.getInstance().createActionGroupPopup(
                            fromAnAction.getPresentation().getText(), (ActionGroup) action, context, false, runnable, -1);
                    Window window = SwingUtilities.getWindowAncestor(myComponent);
                    if (window != null) {
                        actionGroupPopup.showInCenterOf(window);
                    } else {
                        actionGroupPopup.showInFocusCenter();
                    }
                } else {
                    ActionManagerEx manager = ActionManagerEx.getInstanceEx();
                    manager.fireBeforeActionPerformed(action, context, fromAnAction);
                    ActionUtil.performActionDumbAware(action, fromAnAction);
                    if (runnable != null)
                        runnable.run();
                    manager.fireAfterActionPerformed(action, context, fromAnAction);
                }
            }
        });
    }


}
