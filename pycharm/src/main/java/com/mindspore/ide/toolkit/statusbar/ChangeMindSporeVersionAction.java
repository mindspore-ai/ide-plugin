package com.mindspore.ide.toolkit.statusbar;

import com.intellij.ide.lightEdit.LightEditCompatible;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.ui.Messages;
import com.mindspore.ide.toolkit.search.OperatorSearchService;
import com.mindspore.ide.toolkit.statusbar.service.MindSporeStatusBarServiceImpl;
import com.mindspore.ide.toolkit.statusbar.utils.InputVersionCheck;
import com.mindspore.ide.toolkit.statusbar.utils.MindSporeVersionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class ChangeMindSporeVersionAction extends AnAction implements DumbAware, LightEditCompatible {
    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setEnabledAndVisible(true);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        String input = Messages.showInputDialog(event.getProject(), "Please input mindSpore version:",
                "Api Mapping", null, null, new InputVersionCheck(),null, "input should be like 2.1 or 2.1.0");
        if (StringUtils.isEmpty(input)) {
            log.debug("cancel input version");
        } else {
            log.debug("input version is {}", input);
            String versionString = MindSporeVersionUtils.getBigVersion(input);
            Task.Backgroundable task = new Task.Backgroundable(event.getProject(), "Changing to mindspore " + versionString, false) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    indicator.isRunning();
                    boolean checkResult = true;
                    if (!versionString.equals(MindSporeStatusBarServiceImpl.getCurrentSelectedVersion())) {
                        checkResult = OperatorSearchService.INSTANCE.changeSearchDataHub(versionString);
                    }

                    if (checkResult) {
                        MindSporeVersionUtils.addVersion(versionString);
                        MindSporeStatusBarServiceImpl.notifyApp(versionString);
                    }

                    indicator.stop();
                }
            };

            ProgressManager.getInstance().run(task);
        }
    }
}
