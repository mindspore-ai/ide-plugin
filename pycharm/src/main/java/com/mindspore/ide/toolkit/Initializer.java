package com.mindspore.ide.toolkit;

import com.intellij.ide.AppLifecycleListener;
import com.mindspore.ide.toolkit.common.events.EventCenter;
import com.mindspore.ide.toolkit.common.events.ProjectEvents;
import com.mindspore.ide.toolkit.common.utils.PathUtils;
import com.mindspore.ide.toolkit.guide.GuideUserListener;
import com.mindspore.ide.toolkit.smartcomplete.SmartCompleteListener;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Initializer implements AppLifecycleListener {

    @Override
    public void appFrameCreated(@NotNull List<String> commandLineArgs) {
        AppLifecycleListener.super.appFrameCreated(commandLineArgs);
        initListener();
        PathUtils.initResourceFolder();
        EventCenter.INSTANCE.publish(new ProjectEvents.AppFrameCreated());
    }

    private void initListener() {
        EventCenter.INSTANCE.subscribe(new SmartCompleteListener());
        EventCenter.INSTANCE.subscribe(new GuideUserListener());
        EventCenter.INSTANCE.subscribe(new GuideUserListener());
    }

    @Override
    public void appWillBeClosed(boolean isRestart) {
        AppLifecycleListener.super.appWillBeClosed(isRestart);
        EventCenter.INSTANCE.publish(new ProjectEvents.ProjectClosed());
    }
}
