package com.mindspore.ide.toolkit.statusbar;

import com.intellij.ide.lightEdit.LightEditCompatible;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.ui.Messages;
import com.mindspore.ide.toolkit.statusbar.service.MindSporeStatusBarServiceImpl;
import com.mindspore.ide.toolkit.statusbar.utils.InputVersionCheck;
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
        String input = Messages.showInputDialog("please input mindspore version:",
                "Api Mapping", null, null, new InputVersionCheck());
        if (StringUtils.isEmpty(input)) {
            log.debug("cancel input version");
        } else {
            log.debug("input version is {}", input);

            ApplicationManager.getApplication().invokeLater(() -> {
                //TODO -- 下载对应版本mindspore api

                MindSporeStatusBarServiceImpl.addVersion(input);
                MindSporeStatusBarServiceImpl.notifyApp(input);
            });
        }
    }
}
