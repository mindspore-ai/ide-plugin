package com.mindspore.ide.toolkit.services.notify;

import com.intellij.notification.NotificationType;
import com.mindspore.ide.toolkit.common.enums.EnumNotifyGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum EventNotifyServiceProxy {
    INSTANCE;

    private static final Logger LOG = LoggerFactory.getLogger(EventNotifyServiceProxy.class);

    private EnventNotifyService eventNotifyService = EnventNotifyService.INSTANCE;

    public void eventNotify(EnumNotifyGroup notifyGroup, String content, NotificationType notificationType) {
        eventNotify(notifyGroup, content, content, null, notificationType);
    }

    public void eventNotify(EnumNotifyGroup notifyGroup, String content, String log, Throwable error, NotificationType notificationType) {
        switch (notificationType) {
            case WARNING:
                LOG.warn(log);
                break;
            case ERROR:
                LOG.error(log);
                break;
            case INFORMATION:
            default:
                LOG.info(log);
        }
        if (error != null) {
            LOG.error(log, error);
        }
        eventNotifyService.eventNotify(notifyGroup, content, notificationType);
    }
}
