package com.mindspore.ide.toolkit.apiscanning;

import com.intellij.ide.projectView.actions.MarkRootActionBase;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.StandardFileSystems;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiUtilCore;
import com.mindspore.ide.toolkit.apiscanning.handler.ApiMappingHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class TransProjectApiAction extends AnAction {
    private static final Logger log = Logger.getInstance(TransProjectApiAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        long startTime = System.currentTimeMillis();


        Optional<Project> projectOp = Optional.ofNullable(e.getData(PlatformDataKeys.PROJECT));
        if (projectOp.isPresent()) {
            ProgressManager.getInstance().run(new Task.Backgroundable(projectOp.get(), "operator scan " +
                    "project-level") {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    Optional<String> projectUrlOp = Optional.ofNullable(projectOp.get().getPresentableUrl());
                    ApiMappingHandler apiMappingHandler = new ApiMappingHandler(projectOp.get());
                    Optional<VirtualFile> virtualFileOP =
                            Optional.ofNullable(StandardFileSystems.local().findFileByPath(projectUrlOp.get()));
                    virtualFileOP.ifPresent(
                            virtualFileRoot -> {
                                Editor editor = e.getData(CommonDataKeys.EDITOR);
                                if (editor != null) {
                                    ModifiableRootModel modifiableModel = ModuleRootManager
                                            .getInstance(ProjectRootManager.getInstance(projectOp.get()).getFileIndex()
                                                    .getModuleForFile(PsiDocumentManager.getInstance(projectOp.get())
                                                            .getPsiFile(editor.getDocument()).getVirtualFile()))
                                            .getModifiableModel();
                                    ContentEntry contentEntry = MarkRootActionBase.findContentEntry(modifiableModel,
                                            virtualFileRoot);
                                    if (contentEntry != null) {
                                        ApiMappingHandler.excludedFilesMap.put(projectOp.get(),
                                                contentEntry.getExcludeFolderFiles());
                                    }
                                }
                            }
                    );
                    virtualFileOP.ifPresent(apiMappingHandler::iterateVfsTreeNode);
                    List<PsiFile> psiFiles = PsiUtilCore.toPsiFiles(PsiManager.getInstance(projectOp.get()),
                            apiMappingHandler.getVirtualFileSet());
                    ApplicationManager.getApplication().invokeLater(() -> ApplicationManager.getApplication()
                            .runReadAction(() -> apiMappingHandler.handleProjectApiMapping(psiFiles)));
                }
            });
        } else {
            log.warn("get project failed, can't execute api mapping");
        }
        log.info("api mapping for project cost " + (System.currentTimeMillis() - startTime) + " ms");
    }
}
