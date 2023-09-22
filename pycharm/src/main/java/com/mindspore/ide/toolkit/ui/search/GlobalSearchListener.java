package com.mindspore.ide.toolkit.ui.search;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;
import com.mindspore.ide.toolkit.common.config.DocSearchConfig;
import com.mindspore.ide.toolkit.common.utils.GsonUtils;
import com.mindspore.ide.toolkit.search.MsVersionDataConfig;
import com.mindspore.ide.toolkit.search.OperatorSearchService;
import com.mindspore.ide.toolkit.search.entity.OpenMindSporeActionModel;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;
import org.cef.browser.CefBrowser;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

@Listener(references = References.Strong)
public class GlobalSearchListener {

    @Handler
    public void showSearchDetail(OpenMindSporeActionModel event){
        CefBrowser browser = BrowserWindowManager.getBrowserWindow(event.getMyProject()).getCefBrowser();
        browser.executeJavaScript(
                String.format(Locale.ROOT, DocSearchConfig.get().getSearchJs(),
                GsonUtils.INSTANCE.getGson().toJson(event.getDocValue())),
                "",
                0
        );
    }
}
