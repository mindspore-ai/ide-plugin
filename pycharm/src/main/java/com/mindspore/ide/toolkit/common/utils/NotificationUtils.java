package com.mindspore.ide.toolkit.common.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import lombok.Getter;

public class NotificationUtils {
    private NotificationUtils() {
    }

    public static void notify(NotifyGroup group, NotificationType type, String content) {
        Notifications.Bus.notify(new Notification(group.groupId, group.title, content, type));
    }

    @Getter
    public enum NotifyGroup {
        SMART_COMPLETE("SmartComplete", "MindSpore smart complete"),
        NEW_PROJECT("NewProject", "New MindSpore project");

        private String groupId;

        private String title;

        NotifyGroup(String groupId, String title) {
            this.groupId = groupId;
            this.title = title;
        }
    }
}
