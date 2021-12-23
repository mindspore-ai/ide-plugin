package com.mindspore.ide.toolkit.ui.guide;

import com.intellij.notification.Notification;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.mindspore.ide.toolkit.common.config.GuideUserEntity;
import com.mindspore.ide.toolkit.common.events.EventCenter;
import com.mindspore.ide.toolkit.common.events.GuideUserEvents;
import org.jetbrains.annotations.NotNull;

public class DontAskAgainAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Notification.get(e).hideBalloon();
        EventCenter.INSTANCE.publish(new GuideUserEvents.DontAskAgain(GuideUserEntity.get()));
    }
}
