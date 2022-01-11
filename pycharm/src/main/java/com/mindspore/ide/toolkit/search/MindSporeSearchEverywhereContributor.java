package com.mindspore.ide.toolkit.search;

import com.mindspore.ide.toolkit.common.config.GlobalConfig;
import com.mindspore.ide.toolkit.common.events.EventCenter;
import com.mindspore.ide.toolkit.search.entity.DocumentSearch;
import com.mindspore.ide.toolkit.search.entity.DocumentValue;
import com.mindspore.ide.toolkit.search.entity.OpenMindSporeActionModel;
import com.mindspore.ide.toolkit.ui.search.BrowserWindowManager;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory;
import com.intellij.ide.actions.GotoActionAction;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.util.Processor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Nls;

import java.lang.ref.WeakReference;
import java.util.Map;
import javax.swing.ListCellRenderer;
import javax.swing.DefaultListCellRenderer;
import java.awt.Component;

public class MindSporeSearchEverywhereContributor implements SearchEverywhereContributor<DocumentSearch> {

    private final Project mindSporeProject;

    private final WeakReference<Component> mindSporeContextComponent;

    public MindSporeSearchEverywhereContributor(Project mindSporeProject, Component mindSporeContextComponent) {
        this.mindSporeProject = mindSporeProject;
        this.mindSporeContextComponent = new WeakReference<>(mindSporeContextComponent);
    }

    @Override
    public int getSortWeight() {
        return 600;
    }

    @Override
    public boolean isShownInSeparateTab() {
        return true;
    }

    @Override
    @NotNull
    public String getSearchProviderId() {
        return getClass().getSimpleName();
    }

    @Override
    @NotNull
    @Nls
    public String getGroupName() {
        return GlobalConfig.get().getToolWindowName();
    }

    @Override
    @NotNull
    public ListCellRenderer<? super DocumentSearch> getElementsRenderer() {
        return new DefaultListCellRenderer();
    }

    @Override
    @Nullable
    public Object getDataForItem(@NotNull DocumentSearch element, @NotNull String dataId) {
        return null;
    }

    @Override
    public boolean showInFindResults() {
        return false;
    }

    @Override
    public void fetchElements(@NotNull String pattern, @NotNull ProgressIndicator progressIndicator, @NotNull Processor<? super DocumentSearch> consumer) {
        if (StringUtil.isEmptyOrSpaces(pattern)) {
            return;
        }
        if (progressIndicator.isCanceled()) {
            return;
        }

        //不调用搜索接口
        Map<String, String> model = OperatorSearch.INSTANCE.search(pattern, 10);
        if (model != null) {
            for (Map.Entry<String, String> entry : model.entrySet()) {
                DocumentSearch search = new DocumentSearch(entry, pattern);
                if (!consumer.process(search)) {
                    return;
                }
            }
        }
        AnAction anAction = ActionManager.getInstance().getAction("ActivateMindSporeToolWindow");
        if (anAction != null) {
            anAction.getTemplatePresentation().setText("Search On MindSpore");
            DocumentSearch searchDoc = new DocumentSearch(anAction, pattern, 1);
            consumer.process(searchDoc);
        }
    }

    @Override
    public boolean processSelectedItem(@NotNull DocumentSearch selected, int modifiers, @NotNull String searchText) {
        if (selected.getValue() instanceof AnAction) {
            GotoActionAction.openOptionOrPerformAction(selected.getValue(), searchText, mindSporeProject,
                    mindSporeContextComponent.get());
        } else if (selected.getValue() instanceof Map.Entry) {
            Map.Entry<String, String> map = (Map.Entry<String, String>) selected.getValue();
            BrowserWindowManager.getBrowserWindow(mindSporeProject).loadUrl(map.getValue());
        } else if (selected.getValue() instanceof DocumentValue) {
            AnAction anAction = ActionManager.getInstance().getAction("ActivateMindSporeToolWindow");
            GotoActionAction.openOptionOrPerformAction(anAction, searchText, mindSporeProject,
                    mindSporeContextComponent.get());
            DocumentValue documentValue = (DocumentValue) selected.getValue();
            EventCenter.INSTANCE.publish(new OpenMindSporeActionModel(mindSporeProject, documentValue));
        } else {
            return false;
        }
        return true;
    }

    public static class Factory implements SearchEverywhereContributorFactory<DocumentSearch> {
        @NotNull
        @Override
        public SearchEverywhereContributor<DocumentSearch> createContributor(@NotNull AnActionEvent initEvent) {
            return mindSporeCreate(initEvent);
        }
    }

    @NotNull
    private static SearchEverywhereContributor<DocumentSearch> mindSporeCreate(@NotNull AnActionEvent initEvent) {
        return new MindSporeSearchEverywhereContributor(initEvent.getProject(),
                initEvent.getData(PlatformDataKeys.CONTEXT_COMPONENT));
    }
}