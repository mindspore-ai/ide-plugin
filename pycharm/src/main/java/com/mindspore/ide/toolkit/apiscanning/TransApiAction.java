package com.mindspore.ide.toolkit.apiscanning;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiFile;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.mindspore.ide.toolkit.apiscanning.utils.FileScanAgent;
import com.mindspore.ide.toolkit.common.events.CommonEvent;
import com.mindspore.ide.toolkit.common.events.EventCenter;
import com.mindspore.ide.toolkit.common.utils.NotificationUtils;
import com.mindspore.ide.toolkit.search.OperatorSearchService;

import lombok.extern.slf4j.Slf4j;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

/**
 * xxx
 *
 * @since 2022-12-16
 */
@Slf4j
public class TransApiAction extends AnAction {

    private final Map<String, Content> contentMap = new HashMap<>();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (!OperatorSearchService.INSTANCE.isInit()) {
            NotificationUtils.notify(NotificationUtils.NotifyGroup.SEARCH, NotificationType.WARNING,
                    "API Mapping data is still loading, please try later!");
            return;
        }
        DataContext dataContext = e.getDataContext();
        PsiFile psiFile = CommonDataKeys.PSI_FILE.getData(dataContext);
        FileScanAgent fileScanAgent = new FileScanAgent(psiFile);
        Object[][] api = fileScanAgent.apiArray();
        Object[][] papi = fileScanAgent.papiArray();
        Object[][] apiNull = fileScanAgent.apiNullArray();

        if (api.length > 0 || papi.length > 0 || apiNull.length > 0) {
            Project project1 = e.getProject();
            ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project1);
            ToolWindow toolWindow = toolWindowManager.getToolWindow("MindSporeApiMapping");
            ContentManager contentManager = toolWindow.getContentManager();
            String name = psiFile.getName();
            JComponent jComponent = ApiMappingUI.build(api,
                    papi,
                    apiNull,
                    project1,
                    name);
            Content content = ContentFactory.SERVICE.getInstance().createContent(jComponent, name, true);
            Content oldContent = contentMap.put(name, content);
            if (oldContent != null) {
                contentManager.removeContent(oldContent, true);
            }
            contentManager.addContent(content);
            contentManager.setSelectedContent(content, true, true);
            toolWindow.show();
        } else {
            NotificationUtils.notify(
                    NotificationUtils.NotifyGroup.SEARCH, NotificationType.INFORMATION, "没有相关的API");
        }
        EventCenter.INSTANCE.publish(new CommonEvent());
    }









}

