package com.mindspore.ide.toolkit.apiscanning.handler;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileFilter;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.content.ContentManagerEvent;
import com.intellij.ui.content.ContentManagerListener;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyReferenceExpression;
import com.mindspore.ide.toolkit.apiscanning.ApiMappingProjectUI;
import com.mindspore.ide.toolkit.apiscanning.TransApiAction;
import com.mindspore.ide.toolkit.apiscanning.VirtualFileNode;
import com.mindspore.ide.toolkit.common.utils.MSPsiUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ApiMappingHandler {
    private static final Logger log = Logger.getInstance(ApiMappingHandler.class);
    private static final String NO_PACKAGE_PLACEHOLDER = "<no package>";

    private final Map<String, Content> contentMap = new HashMap<>();
    private Map<String, String> importMap = new LinkedHashMap<>();
    private Map<String, String> fromMap;
    private Map<String, String> fromAsMap;
    private static Map<Content, ApiMappingProjectUI> projectContentMap = new HashMap<>();
    private Set<String> apiNameList;
    private Project myProject;
    private Set<String> apiBlurredNameList;
    public static Map<Project, VirtualFile[]> excludedFilesMap = new HashMap<>();
    private VirtualFile root;
    private ToolWindow toolWindow;
    private ContentManager contentManager;
    private final Set<VirtualFile> virtualFileSet = new LinkedHashSet<>();
    private Set<String> apiNameFiltering = new LinkedHashSet<>();
    private Set<String> apiBlurredNameFiltering = new LinkedHashSet<>();

    private final VirtualFileFilter virtualFileFilter = new VirtualFileFilter() {
        @Override
        public boolean accept(@NotNull VirtualFile file) {
            VirtualFile[] excludedFiles = excludedFilesMap.get(myProject);
            if (excludedFiles != null && excludedFiles.length != 0) {
                return !(Arrays.asList(excludedFiles).contains(file));
            }
            return true;
        }
    };

    private final ContentIterator contentIterator = fileOrDir -> {
        try {
            virtualFileSet.add(fileOrDir);
        } catch (Exception e) {
            return false;
        }
        return true;
    };

    public ApiMappingHandler(Project project) {
        myProject = project;
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        toolWindow = toolWindowManager.getToolWindow("MindSporeApiMapping");
        contentManager = toolWindow.getContentManager();
        contentManager.addContentManagerListener(new ContentManagerListener() {
            @Override
            public void contentRemoved(@NotNull ContentManagerEvent event) {
                projectContentMap.remove(event.getContent());
            }
        });
    }

    public void iterateVfsTreeNode(VirtualFile virtualFileRoot) {
        root = virtualFileRoot;
        VfsUtilCore.iterateChildrenRecursively(root, virtualFileFilter, contentIterator);
    }

    public void handleProjectApiMapping(List<PsiFile> psiFiles) {
        psiFiles.stream()
                .filter(psiFile -> psiFile.getName().contains(".py"))
                .forEach(this::getProjectApiMappingInfoByPsi);
        List<Object[]> apiNameNullList = new LinkedList<>();
        List<Object[]> straightApiMappingList = sortList(apiNameFiltering, apiNameNullList);
        List<Object[]> blurredApiMappingList = sortList(apiBlurredNameFiltering, null);
        if (straightApiMappingList.size() > 0) {
            ApiMappingProjectUI projectMap = new ApiMappingProjectUI(trans(straightApiMappingList),
                    trans(blurredApiMappingList),
                    trans(apiNameNullList), myProject, root, root);
            JBScrollPane jbScrollPane = new JBScrollPane(projectMap.splitPane);
            Content content = ContentFactory.SERVICE.getInstance()
                    .createContent(jbScrollPane, myProject.getName(), true);
            contentManager.addContent(content);
            projectContentMap.put(content, projectMap);
            contentManager.setSelectedContent(content, false, false);
        }
        toolWindow.show();
    }

    public void handleTreeNodeSelection(@NotNull VirtualFileNode virtualFileNode) {
        VirtualFile chosenFile = virtualFileNode.getVirtualFile();
        VfsUtilCore.iterateChildrenRecursively(chosenFile, virtualFileFilter, contentIterator);
        long startTime = System.currentTimeMillis();
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(myProject);
        ToolWindow toolWindow = toolWindowManager.getToolWindow("MindSporeApiMapping");
        ContentManager contentManager = toolWindow.getContentManager();
        List<PsiFile> psiFiles = PsiUtilCore.toPsiFiles(PsiManager.getInstance(myProject), virtualFileSet);
        psiFiles.stream()
                .filter(psiFile -> psiFile.getName().contains(".py"))
                .forEach(this::getProjectApiMappingInfoByPsi);
        List<Object[]> apiNameNullList = new LinkedList<>();
        List<Object[]> straightApiMappingList = sortList(apiNameFiltering, apiNameNullList);
        List<Object[]> blurredApiMappingList = sortList(apiBlurredNameFiltering, null);
        ApiMappingProjectUI projectMap = projectContentMap.get(contentManager.getSelectedContent());
        projectMap.reload(trans(straightApiMappingList),
                trans(blurredApiMappingList),
                trans(apiNameNullList));
        String name = chosenFile.getName();
        contentManager.removeContent(contentManager.getSelectedContent(), true);
        Content content = ContentFactory.SERVICE.getInstance()
                .createContent(new JBScrollPane(projectMap.splitPane), name , true);
        contentManager.addContent(content);
        projectContentMap.put(content, projectMap);
        contentManager.setSelectedContent(content, true, true);
        toolWindow.show();
        log.info("api mapping for project const" + (System.currentTimeMillis() - startTime) + " ms");
    }
    public void getProjectApiMappingInfoByPsi(@NotNull PsiFile psiFile) {
        importMap = new HashMap<>();
        fromMap = new HashMap<>();
        fromAsMap = new HashMap<>();
        apiNameList = new LinkedHashSet<>();
        apiBlurredNameList = new LinkedHashSet<>();
        translateImport(psiFile, importMap, fromMap, fromAsMap);
        translateCallExpression(psiFile);
        Set<String> apiNameFiltering1 = filteringApi(apiNameList);
        Set<String> apiBlurredNameFiltering1 = filteringApi(apiBlurredNameList);
        apiNameFiltering.addAll(apiNameFiltering1);
        apiBlurredNameFiltering.addAll(apiBlurredNameFiltering1);
    }

    public Set<VirtualFile> getVirtualFileSet(){
        return virtualFileSet;
    }

    private Set<String> filteringApi(Set<String> apiNameList) {
        return TransApiAction.filteringApi(apiNameList);
    }

    private Object[][] trans(List<Object[]> data) {
        return TransApiAction.trans((data));
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

    private void fromMapDataSort(Set<String> apiString, Set<String> apiNameList) {
        fromMap.forEach((key, value) -> {
            if (value == null) {
                for (String str: apiNameList) {
                    apiString.add(key + "." + str);
                }
            } else {
                fromMapDataSortElse(apiString, apiNameList, key, value);
            }
        });
    }

    private void fromMapDataSortElse(Set<String> apiString, Set<String> apiNameList, String key, String value) {
        TransApiAction.fromMapDataSortElse(apiString,apiNameList,key,value);
    }

    private void fromAsMapDataSort(Set<String> apiString, Set<String> apiNameList) {
        fromAsMap.forEach((key, value)->{
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

    private void importMapDataSortIf(Set<String> apiString, Set<String> apiNameList, String key) {
        TransApiAction.importMapDataSortIf(apiString, apiNameList, key);
    }

    private void importMapDataSortElse(Set<String> apiString, Set<String> apiNameList, String key, String value) {
        TransApiAction.importMapDataSortElse(apiString, apiNameList, key, value);
    }

    private Set<String> filteringData(Set<String> apiNameList) {
        return TransApiAction.filteringData(apiNameList);
    }

    private List<Object[]> searchData(Set<String> apiStringSet, List<Object[]> apiNameNullList) {
        return TransApiAction.searchData(apiStringSet, apiNameNullList);
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
            } else if (element instanceof PyReferenceExpression && element.getChildren().length > 0
                    && !element.getText().toLowerCase().equals(element.getText())) {
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
            } else if (element instanceof PyReferenceExpression && !MSPsiUtils.isPsiImport(element)
                    && element.getChildren().length > 0 && !element.getText().toLowerCase().equals(element.getText())) {
                apiNameList.add(element.getText());
            } else {
                translateCallExpression(element);
            }
        }
    }

    private void translateImport(PsiElement psiElement, Map<String, String> importMap, Map<String,String> fromMap,
                                 Map<String, String> fromAsMap) {
        TransApiAction.translateImport(psiElement, importMap, fromMap, fromAsMap);
    }
}
