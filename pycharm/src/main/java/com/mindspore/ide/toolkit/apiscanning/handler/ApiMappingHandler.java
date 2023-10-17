package com.mindspore.ide.toolkit.apiscanning.handler;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
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
import com.mindspore.ide.toolkit.apiscanning.ApiMappingProjectUI;
import com.mindspore.ide.toolkit.apiscanning.VirtualFileNode;

import com.mindspore.ide.toolkit.apiscanning.utils.FileScanAgent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ApiMappingHandler {
    private static final Logger log = Logger.getInstance(ApiMappingHandler.class);

    private static Map<Content, ApiMappingProjectUI> projectContentMap = new HashMap<>();
    private Project myProject;
    public static Map<Project, VirtualFile[]> excludedFilesMap = new HashMap<>();
    private VirtualFile root;
    private ToolWindow toolWindow;
    private ContentManager contentManager;
    private final Set<VirtualFile> virtualFileSet = new LinkedHashSet<>();

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
        ApiMappingProjectUI.project = myProject;
        ApiMappingProjectUI.chosenFile = root;
        ApiMappingProjectUI projectMap = new ApiMappingProjectUI(myProject, root);
        JBScrollPane jbScrollPane = new JBScrollPane(projectMap.splitPane);
        Content content = ContentFactory.SERVICE.getInstance()
                .createContent(jbScrollPane, myProject.getName(), true);
        contentManager.addContent(content);
        projectContentMap.put(content, projectMap);
        contentManager.setSelectedContent(content, false, false);
        toolWindow.show();
        projectMap.initLoad();
    }

    public void handleTreeNodeSelection(@NotNull VirtualFileNode virtualFileNode) throws Exception {
        VirtualFile chosenFile = virtualFileNode.getVirtualFile();
        long startTime = System.currentTimeMillis();

        FileScanAgent fileScanAgent = ProgressManager.getInstance().run(new Task.WithResult<FileScanAgent, Exception>(myProject,
                "Scanning",
                false) {
            @Override
            public FileScanAgent compute(@NotNull ProgressIndicator indicator) {
                FileScanAgent fileScanAgentInner= new FileScanAgent();
                ApplicationManager.getApplication()
                        .runReadAction(() -> {
                            VfsUtilCore.iterateChildrenRecursively(chosenFile, virtualFileFilter, contentIterator);
                            List<PsiFile> psiFiles = PsiUtilCore.toPsiFiles(
                                    PsiManager.getInstance(myProject), virtualFileSet);

                            psiFiles.stream()
                                    .filter(psiFile -> psiFile.getName().contains(".py"))
                                    .forEach(fileScanAgentInner::scan);
                        });
                return fileScanAgentInner;
            }
        });
        if (fileScanAgent == null) {
            return;
        }
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(myProject);
        ToolWindow toolWindow = toolWindowManager.getToolWindow("MindSporeApiMapping");
        ApiMappingProjectUI projectMap = projectContentMap.get(contentManager.getSelectedContent());
        String name = chosenFile.getName();
        projectMap.reload(fileScanAgent,name, myProject);
        ContentManager contentManagerLocal = toolWindow.getContentManager();
        contentManagerLocal.removeContent(contentManagerLocal.getSelectedContent(), true);
        Content content = ContentFactory.SERVICE.getInstance()
                .createContent(new JBScrollPane(projectMap.splitPane), name, true);
        contentManagerLocal.addContent(content);
        projectContentMap.put(content, projectMap);
        contentManagerLocal.setSelectedContent(content, true, true);
        toolWindow.show();
        log.info("api mapping for project const" + (System.currentTimeMillis() - startTime) + " ms");
    }


    public Set<VirtualFile> getVirtualFileSet(){
        return virtualFileSet;
    }



}
