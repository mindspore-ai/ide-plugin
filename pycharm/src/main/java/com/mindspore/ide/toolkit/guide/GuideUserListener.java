package com.mindspore.ide.toolkit.guide;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.ActionManager;
import com.mindspore.ide.toolkit.common.config.GuideConfig;
import com.mindspore.ide.toolkit.common.config.GuideUserEntity;
import com.mindspore.ide.toolkit.common.events.GuideUserEvents;
import com.mindspore.ide.toolkit.common.events.ProjectEvents;
import com.mindspore.ide.toolkit.common.utils.FileUtils;
import com.mindspore.ide.toolkit.common.utils.HttpUtils;
import com.mindspore.ide.toolkit.common.utils.YamlUtils;
import lombok.extern.slf4j.Slf4j;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Listener(references = References.Strong)
@Slf4j
public class GuideUserListener {
    private final GuideConfig config = GuideConfig.get();

    @Handler
    public void initGuideConfigFile(ProjectEvents.AppFrameCreated appFrameCreated) throws IOException {
        if (!FileUtils.fileExist(config.getConfigFilePath())) {
            GuideUserEntity entity = new GuideUserEntity();
            entity.setAskAgain(false);
            entity.setVersion(config.getPluginVersion());
            FileUtils.writeDataToFile(Paths.get(config.getConfigFilePath()), new Yaml().dumpAsMap(entity));
        }
    }

    @Handler
    public void showBalloon(ProjectEvents.ProjectOpen projectOpen) throws IOException {
        GuideUserEntity entity = GuideUserEntity.get();
        if(config.getGuideSettingUrl().isEmpty()){
            return;
        }
        GuideUserEntity remoteEntity = YamlUtils.INSTANCE.parse(HttpUtils.doGet(config.getGuideSettingUrl(),
                buildDownloadHeader()).getEntity().getContent(), GuideUserEntity.class);
        if(remoteEntity == null){
            return;
        }
        if (!entity.isAskAgain() && entity.getVersion().equals(remoteEntity.getVersion())) {
            NotificationGroup notificationGroup = new NotificationGroup(remoteEntity.getTitle(),
                    NotificationDisplayType.BALLOON, false);
            Notification notification = notificationGroup.createNotification(remoteEntity.getContent(),
                    remoteEntity.getContent(), NotificationType.INFORMATION, null);
            notification.addAction(ActionManager.getInstance().getAction("mindSporeGuideAction"));
            notification.addAction(ActionManager.getInstance().getAction("mindSporeDontAskAgain"));
            Notifications.Bus.notify(notification);
            NotificationGroup.balloonGroup(remoteEntity.getTitle()).createNotification();
            FileUtils.writeDataToFile(Paths.get(config.getConfigFilePath()), new Yaml().dumpAsMap(remoteEntity));
        }
    }

    @Handler
    public void dontAskAgain(GuideUserEvents.DontAskAgain events) throws IOException {
        GuideUserEntity entity = events.getEntity();
        entity.setAskAgain(true);
        FileUtils.writeDataToFile(Paths.get(config.getConfigFilePath()), new Yaml().dumpAsMap(entity));
    }

    private static Map<String, String> buildDownloadHeader() {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
        return headerMap;
    }
}
