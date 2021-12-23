package com.mindspore.ide.toolkit.services.notify;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.mindspore.ide.toolkit.common.enums.EnumNotifyGroup;

public enum EnventNotifyService {
    INSTANCE;

    public void eventNotify(EnumNotifyGroup notifyGroup, String content, NotificationType notificationType) {
        Notification notification = new Notification(notifyGroup.getDisplayId(), notifyGroup.getTitle(), content, notificationType);
        Notifications.Bus.notify(notification);
    }
}
