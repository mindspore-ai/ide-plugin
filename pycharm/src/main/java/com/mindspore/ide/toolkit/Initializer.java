package com.mindspore.ide.toolkit;

import com.intellij.ide.AppLifecycleListener;
import com.mindspore.ide.toolkit.common.events.EventCenter;
import com.mindspore.ide.toolkit.common.events.ProjectEvents;
import com.mindspore.ide.toolkit.common.utils.PathUtils;
import com.mindspore.ide.toolkit.guide.GuideUserListener;
import com.mindspore.ide.toolkit.quesionnaire.QuestionnaireListener;
import com.mindspore.ide.toolkit.search.MdDataGet;
import com.mindspore.ide.toolkit.search.MsVersionDataConfig;
import com.mindspore.ide.toolkit.search.OperatorSearchService;
import com.mindspore.ide.toolkit.smartcomplete.SmartCompleteListener;
import com.mindspore.ide.toolkit.ui.search.GlobalSearchListener;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Slf4j
public class Initializer implements AppLifecycleListener {

    @Override
    public void appFrameCreated(@NotNull List<String> commandLineArgs) {
        log.info("appFrameCreated start.");
        AppLifecycleListener.super.appFrameCreated(commandLineArgs);
        initListener();
        PathUtils.initResourceFolder();
        OperatorSearchService.INSTANCE.changeSearchDataHub(MsVersionDataConfig.getInstance().parseVersionJsonFile().get(0).getMdVersion());
        EventCenter.INSTANCE.publish(new ProjectEvents.AppFrameCreated());
    }

    private void initListener() {
        EventCenter.INSTANCE.subscribe(new SmartCompleteListener());
        EventCenter.INSTANCE.subscribe(new GlobalSearchListener());
        EventCenter.INSTANCE.subscribe(new GuideUserListener());
        EventCenter.INSTANCE.subscribe(new QuestionnaireListener());
    }

    @Override
    public void appWillBeClosed(boolean isRestart) {
        AppLifecycleListener.super.appWillBeClosed(isRestart);
        EventCenter.INSTANCE.publish(new ProjectEvents.ProjectClosed());
    }
}
