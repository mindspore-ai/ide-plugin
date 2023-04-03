package com.mindspore.ide.toolkit.apiscanning;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.Strings;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyFromImportStatement;
import com.jetbrains.python.psi.PyImportElement;
import com.jetbrains.python.psi.PyImportStatement;
import com.jetbrains.python.psi.PyImportStatementBase;
import com.jetbrains.python.psi.PyReferenceExpression;
import com.mindspore.ide.toolkit.common.events.CommonEvent;
import com.mindspore.ide.toolkit.common.events.EventCenter;
import com.mindspore.ide.toolkit.common.utils.MSPsiUtils;
import com.mindspore.ide.toolkit.common.utils.NotificationUtils;
import com.mindspore.ide.toolkit.common.utils.RegularUtils;
import com.mindspore.ide.toolkit.search.OperatorSearchService;
import com.mindspore.ide.toolkit.search.entity.LinkInfo;
import com.mindspore.ide.toolkit.search.entity.OperatorRecord;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.swing.JComponent;

/**
 * xxx
 *
 * @since 2022-12-16
 */
@Slf4j
public class TransApiAction extends AnAction {
    private static final String NO_PACKAGE_PLACEHOLDER = "<no package>";

    private final Map<String, Content> contentMap = new HashMap<>();

    private Map<String, String> importMap = new LinkedHashMap<>();

    private Map<String, String> fromMap;

    private Map<String, String> fromAsMap;

    private Set<String> apiNameList;

    private Set<String> apiBlurredNameList;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DataContext dataContext = e.getDataContext();
        PsiFile psiFile = CommonDataKeys.PSI_FILE.getData(dataContext);
        importMap = new HashMap<>();
        fromMap = new HashMap<>();
        fromAsMap = new HashMap<>();
        apiNameList = new LinkedHashSet<>();
        apiBlurredNameList = new LinkedHashSet<>();
        // 保存import或from映射
        translateImport(psiFile, importMap, fromMap, fromAsMap);
        // 获取apiName
        translateCallExpression(psiFile);

        // 过滤无效api，只含有字母数字点和下划线的认为是有效api
        Set<String> apiNameFiltering = filteringApi(apiNameList);
        Set<String> apiBlurredNameFiltering = filteringApi(apiBlurredNameList);

        // 对api和import进行拼接，并搜索内容
        List<Object[]> apiNameNullList = new LinkedList<>();
        List<Object[]> straightApiMappingList = sortList(apiNameFiltering, apiNameNullList);
        List<Object[]> blurredApiMappingList = sortList(apiBlurredNameFiltering, null);

        if (straightApiMappingList.size() > 0) {
            Project project1 = e.getProject();
            ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project1);
            ToolWindow toolWindow = toolWindowManager.getToolWindow("MindSporeApiMapping");
            ContentManager contentManager = toolWindow.getContentManager();
            String name = psiFile.getName();
            JComponent jComponent = ApiMappingUI.build(trans(straightApiMappingList),
                    trans(blurredApiMappingList),
                    trans(apiNameNullList),
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
                    NotificationUtils.NotifyGroup.API_SCANNING, NotificationType.INFORMATION, "无相关API");
        }
        EventCenter.INSTANCE.publish(new CommonEvent());
    }

    public static Set<String> filteringApi(Set<String> apiNameList) {
        Set<String> stringSet = new LinkedHashSet<>();
        apiNameList.forEach(s -> {
            if (RegularUtils.isApi(s)) {
                stringSet.add(s);
            }
        });
        return stringSet;
    }

    public static Object[][] trans(List<Object[]> data) {
        if (CollectionUtils.isEmpty(data)) {
            return new Object[][] {};
        }
        Object[][] objects = new Object[data.size()][data.get(0).length];
        for (int i = 0; i < data.size(); i++) {
            objects[i] = data.get(i);
        }
        return objects;
    }

    private List<Object[]> sortList(Set<String> apiNameList, List<Object[]> apiNameNullList) {
        Set<String> apiString = filteringData(apiNameList);
        importMapDataSort(apiString, apiNameList);
        fromMapDataSort(apiString, apiNameList);
        fromAsMapDataSort(apiString, apiNameList);
        return searchData(filteringData(apiString), apiNameNullList);
    }

    private void importMapDataSort(Set<String> apiString, Set<String> apiNameList) {
        importMap.forEach((key, value) -> {
            if (value == null) {
                importMapDataSortIf(apiString, apiNameList, key);
            } else {
                importMapDataSortElse(apiString, apiNameList, key, value);
            }
        });
    }

    public static void importMapDataSortIf(Set<String> apiString, Set<String> apiNameList, String key) {
        for (String str : apiNameList) {
            String[] split = str.split("\\.");
            if (split.length > 0) {
                if (split[0].equals(key)) {
                    apiString.add(str);
                }
            } else {
                if (str.equals(key)) {
                    apiString.add(str);
                }
            }
        }
    }

    public static void importMapDataSortElse(Set<String> apiString, Set<String> apiNameList, String key, String value) {
        for (String str : apiNameList) {
            String[] split = str.split("\\.");
            if (split.length > 0) {
                if (split[0].equals(value)) {
                    apiString.add(str.replaceFirst(value, key));
                }
            } else {
                if (str.equals(value)) {
                    apiString.add(str.replaceFirst(value, key));
                }
            }
        }
    }

    private void fromMapDataSort(Set<String> apiString, Set<String> apiNameList) {
        fromMap.forEach((key, value) -> {
            if (value == null) {
                for (String str : apiNameList) {
                    apiString.add(key + "." + str);
                }
            } else {
                fromMapDataSortElse(apiString, apiNameList, key, value);
            }
        });
    }

    public static void fromMapDataSortElse(Set<String> apiString, Set<String> apiNameList, String key, String value) {
        for (String str : apiNameList) {
            String[] split = str.split("\\.");
            if (split.length > 0) {
                if (split[0].equals(value)) {
                    apiString.add(key + str.replaceFirst(split[0], ""));
                }
            } else {
                if (str.equals(value)) {
                    apiString.add(key);
                }
            }
        }
    }

    private void fromAsMapDataSort(Set<String> apiString, Set<String> apiNameList) {
        fromAsMap.forEach((key, value) -> {
            for (String str : apiNameList) {
                String[] split = str.split("\\.");
                if (split.length > 0) {
                    if (split[0].equals(value)) {
                        apiString.add(key + str.replaceFirst(split[0], ""));
                    }
                } else {
                    if (str.equals(value)) {
                        apiString.add(key);
                    }
                }
            }
        });
    }

    public static Set<String> filteringData(Set<String> apiNameList) {
        Set<String> apiString = new LinkedHashSet<>();
        Iterator<String> it = apiNameList.iterator();

        while (it.hasNext()) {
            String str = it.next();
            String[] api = str.split("\\.");
            if (api.length > 0 && "tensorflow".equals(api[0])) {
                str = str.replaceFirst("tensorflow", "tf");
            }
            String[] apiNew = str.split("\\.");
            if ("tf".equals(apiNew[0]) || "torch".equals(apiNew[0]) || "torchtext".equals(apiNew[0]) || "torchvision".equals(apiNew[0])) {
                apiString.add(str);
                it.remove();
            }
        }
        return apiString;
    }

    public static List<Object[]> searchData(Set<String> apiStringSet, List<Object[]> apiNameNullList) {
        List<Object[]> apiList = new LinkedList<>();
        List<Object[]> nonMatchApiList = new LinkedList<>();
        for (String str : apiStringSet) {
            String[] api = str.split("\\.");
            if (api.length > 0 && "tensorflow".equals(api[0])) {
                str = str.replaceFirst("tensorflow", "tf");
            }
            List<OperatorRecord> records = OperatorSearchService.INSTANCE.searchFullMatch(str);
            for (OperatorRecord record : records) {
                Object[] cells = new Object[4];
                if (Strings.isEmpty(record.getOriginalLink())) {
                    cells[0] = record.getOriginalLink();
                } else {
                    cells[0] = new LinkInfo(record.getOriginalOperator(), record.getOriginalLink());
                }
                cells[1] = record.getVersionText();
                if (Strings.isEmpty(record.getMindSporeLink())) {
                    cells[2] = record.getMindSporeOperator();
                } else {
                    cells[2] = new LinkInfo(record.getMindSporeOperator(), record.getMindSporeLink());
                }
                if (Strings.isEmpty(record.getDescriptionLink())) { // 注释
                    cells[3] =
                            record.getDescription() + (record.isInWhiteList() ? "" : "（仅支持2.0及以上版本MindSpore）");
                } else {
                    cells[3] =
                            new LinkInfo(record.getDescription(), record.getDescriptionLink(), !record.isInWhiteList());
                }
                apiList.add(cells);
            }
            if (records.isEmpty()) {
                nonMatchApiList.add(new Object[]{str, "", "", new LinkInfo("缺失api处理策略",
                        "https://www.mindspore.cn/docs/zh-CN/master/migration_guide/analysis_and_preparation.html" +
                                "#%E7%BC%BA%E5%A4%B1api%E5%A4%84%E7%90%86%E7%AD%96%E7%95%A5")});
            }
        }
        Objects.requireNonNullElse(apiNameNullList, apiList).addAll(nonMatchApiList);
        return apiList;
    }

    private void translateCallExpressionInPyCallExpression(PsiElement psiElement, List<String[]> formerFunction) {
        for (PsiElement element : psiElement.getChildren()) {
            if (element instanceof PyCallExpression) {
                PyCallExpression pyCallExpression = (PyCallExpression) element;
                String apiString = pyCallExpression.getCallee().getText();
                int lastDot = apiString.lastIndexOf('.');
                String key;
                String value;
                if (lastDot > 0 && apiString.length() > lastDot) {
                    key = apiString.substring(0, lastDot);
                    value = apiString.substring(lastDot + 1);
                } else {
                    key = apiString;
                    value = NO_PACKAGE_PLACEHOLDER;
                }
                formerFunction.add(new String[] {key, value});
                translateCallExpressionInPyCallExpression(element, formerFunction);
            } else if (element instanceof PyReferenceExpression && element.getChildren().length > 0 && !element.getText().toLowerCase().equals(element.getText())) {
                apiNameList.add(element.getText());
                translateCallExpressionInPyCallExpression(element, formerFunction);
            } else {
                translateCallExpressionInPyCallExpression(element, formerFunction);
            }
        }

        if (formerFunction.size() <= 0) {
            log.info("The length of the formerFunction is 0");
        } else {
            int last = formerFunction.size() - 1;
            String lastKey = formerFunction.get(last)[0];
            formerFunction.forEach(record -> {
                if (record[0].equals(lastKey)) {
                    if (NO_PACKAGE_PLACEHOLDER.equals(record[1])) {
                        apiNameList.add(lastKey);
                    } else {
                        apiNameList.add(lastKey + "." + record[1]);
                    }
                } else {
                    if (NO_PACKAGE_PLACEHOLDER.equals(record[1])) {
                        apiBlurredNameList.add(lastKey);
                    } else {
                        apiBlurredNameList.add(lastKey + "." + record[1]);
                    }
                }
            });
        }
        formerFunction.clear();
    }

    private void translateCallExpression(PsiElement psiElement) {
        List<String[]> formerFunction = new ArrayList<>();
        for (PsiElement element : psiElement.getChildren()) {
            if (element instanceof PyCallExpression) {
                PyCallExpression pyCallExpression = (PyCallExpression) element;
                String apiString = pyCallExpression.getCallee().getText();
                int lastDot = apiString.lastIndexOf('.');
                String key;
                String value;
                if (lastDot > 0 && apiString.length() > lastDot) {
                    key = apiString.substring(0, lastDot);
                    value = apiString.substring(lastDot + 1);
                } else {
                    key = apiString;
                    value = NO_PACKAGE_PLACEHOLDER;
                }
                formerFunction.add(new String[] {key, value});
                translateCallExpressionInPyCallExpression(element, formerFunction);
            } else if (element instanceof PyReferenceExpression && !MSPsiUtils.isPsiImport(element) && element.getChildren().length > 0 && !element.getText().toLowerCase().equals(element.getText())) {
                apiNameList.add(element.getText());
            } else {
                translateCallExpression(element);
            }
        }
    }

    /**
     * 获取文件中的import
     *
     * @param psiElement psiElement
     * @param importMap import xxx    or    import xxx as yyy
     * @param fromMap from xxx import yyy     or     from xxx import *
     * @param fromAsMap from xxx import yyy as zzz
     */
    public static void translateImport(PsiElement psiElement, Map<String, String> importMap, Map<String, String> fromMap, Map<String, String> fromAsMap) {
        PyImportElement importElement;
        for (PsiElement element : psiElement.getChildren()) {
            try {
                if (element instanceof PyImportStatementBase) {
                    PyImportStatementBase importStatementBase = (PyImportStatementBase) element;
                    if (element instanceof PyImportStatement) {
                        // import mindspore.numpy as mnp; key是mindspore， value是mnp
                        // import mindspore; key是mindspore, value是null
                        importElement = importStatementBase.getImportElements()[0];
                        importMap.put(importElement.getImportedQName().toString(), importElement.getAsName());
                    } else if (element instanceof PyFromImportStatement) {
                        PyFromImportStatement pyFromImportStatement = (PyFromImportStatement) element;
                        if (pyFromImportStatement.getImportElements().length >= 1) {
                            if (pyFromImportStatement.getImportElements()[0].getAsName() == null) {
                                // from mindspore.numpy import nn; key是mindspore.numpy.nn, value是nn
                                for (PyImportElement pyImportElement : importStatementBase.getImportElements()) {
                                    fromMap.put(pyFromImportStatement.getImportSourceQName().toString() + "." + pyImportElement.getText(), pyImportElement.getText());
                                }
                            } else {
                                // from d2l import mindspore as dl; key是mindspore.d2l, value是dl
                                fromAsMap.put(importStatementBase.getFullyQualifiedObjectNames().get(0), importStatementBase.getImportElements()[0].getAsName());
                            }
                        } else {
                            // from mindspore import *; key是mindspore, value是null
                            //fromMap.put(pyFromImportStatement.getImportSourceQName().toString(), null);
                        }
                    } else {
                        log.info("psiFile exceptions");
                    }
                } else {
                    translateImport(element, importMap, fromMap, fromAsMap);
                }
            } catch (Throwable ex) {
                log.debug("import analyse error", ex);
            }
        }
    }
}
