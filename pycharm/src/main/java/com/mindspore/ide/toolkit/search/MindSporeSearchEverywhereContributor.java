package com.mindspore.ide.toolkit.search;

import com.intellij.ide.actions.GotoActionAction;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.Processor;
import com.mindspore.ide.toolkit.common.config.GlobalConfig;
import com.mindspore.ide.toolkit.common.events.EventCenter;
import com.mindspore.ide.toolkit.search.entity.DocumentSearch;
import com.mindspore.ide.toolkit.search.entity.DocumentValue;
import com.mindspore.ide.toolkit.search.entity.OpenMindSporeActionModel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.lang.ref.WeakReference;

public class MindSporeSearchEverywhereContributor implements SearchEverywhereContributor<DocumentSearch> {

    private final Project myProject;

    private final WeakReference<Component> myContextComponent;

    public MindSporeSearchEverywhereContributor(Project myProject, Component myContextComponent) {
        this.myProject = myProject;
        this.myContextComponent = new WeakReference<>(myContextComponent);
    }


    @Override
    public @NotNull String getSearchProviderId() {
        return getClass().getSimpleName();
    }

    @Override
    public @NotNull @Nls String getGroupName() {
        return GlobalConfig.get().getToolWindowName();
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
    public void fetchElements(@NotNull String pattern, @NotNull ProgressIndicator progressIndicator, @NotNull Processor<? super DocumentSearch> consumer) {
        if (StringUtil.isEmptyOrSpaces(pattern)) {
            return;
        }
        if (progressIndicator.isCanceled()) {
            return;
        }
        AnAction anAction = ActionManager.getInstance().getAction("ActivateMindSporeToolWindow");
        if (anAction != null) {
            anAction.getTemplatePresentation().setText("Search On MindSpore");
            DocumentSearch searchDoc = new DocumentSearch(anAction, pattern, Integer.MAX_VALUE);
            consumer.process(searchDoc);
        }
        //不调用搜索接口
//        DocumentResultModel model = SearchService.INSTANCE.requestSearchResult(pattern);
//        if (model != null) {
//            for (DocumentValue value : model.getHits()) {
//                DocumentSearch search = new DocumentSearch(value, pattern, value.getId());
//                if (!consumer.process(search)) {
//                    return;
//                }
//            }
//        }
    }

    @Override
    public boolean processSelectedItem(@NotNull DocumentSearch selected, int modifiers, @NotNull String searchText) {
        if (selected.getValue() instanceof AnAction) {
            GotoActionAction.openOptionOrPerformAction(selected.getValue(), searchText, myProject, myContextComponent.get());
        } else if (selected.getValue() instanceof DocumentValue) {
            AnAction anAction = ActionManager.getInstance().getAction("ActivateMindSporeToolWindow");
            GotoActionAction.openOptionOrPerformAction(anAction, searchText, myProject, myContextComponent.get());
            DocumentValue documentValue = (DocumentValue) selected.getValue();
            EventCenter.INSTANCE.publish(new OpenMindSporeActionModel(myProject, documentValue));
        } else {
            return false;
        }

        return true;
    }


    @Override
    public @NotNull ListCellRenderer<? super DocumentSearch> getElementsRenderer() {
        return new DefaultListCellRenderer();
    }

    @Override
    public @Nullable Object getDataForItem(@NotNull DocumentSearch element, @NotNull String dataId) {
        return null;
    }

    @Override
    public boolean showInFindResults() {
        return false;
    }

    public static class Factory implements SearchEverywhereContributorFactory<DocumentSearch> {
        @Override
        public @NotNull SearchEverywhereContributor<DocumentSearch> createContributor(@NotNull AnActionEvent initEvent) {
            return new MindSporeSearchEverywhereContributor(initEvent.getProject(), initEvent.getData(PlatformDataKeys.CONTEXT_COMPONENT));
        }
    }
}
