package com.mindspore.ide.toolkit.apiscanning.utils;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.intellij.openapi.vfs.newvfs.impl.VirtualFileImpl;
import com.mindspore.ide.toolkit.apiscanning.VirtualFileNode;
import com.mindspore.ide.toolkit.apiscanning.VirtualFileTreeSelectionListener;
import com.mindspore.ide.toolkit.apiscanning.handler.ApiMappingHandler;

import java.util.Arrays;
import java.util.Iterator;

public class VirtualFileTreeUtil {
    private static final Logger log = Logger.getInstance(VirtualFileTreeUtil.class);

    public static VirtualFileNode initVirtualFileNode(VirtualFileNode root, Project project) {
        VirtualFile[] excludedFiles = ApiMappingHandler.excludedFilesMap.get(project);
        for (VirtualFile child : root.getVirtualFile().getChildren()) {
            if (excludedFiles != null && excludedFiles.length != 0) {
                if (Arrays.asList(excludedFiles).contains(child)) {
                    continue;
                }
                if (ProjectFileIndex.SERVICE.getInstance(project).isExcluded(child)) {
                    continue;
                }
            }
            if (child instanceof VirtualDirectoryImpl && child.getChildren().length == 0) {
                continue;
            }
            if (!(child.getName().endsWith(".py")) && child.getChildren().length == 0) {
                continue;
            }
            VirtualFileNode virtualFileNode = new VirtualFileNode(child);
            root.add(virtualFileNode);
            if (child.getName().endsWith(".py")) {
                setNodeRootPy(virtualFileNode);
            }
            initVirtualFileNode(virtualFileNode, project);
        }

        root.getChildren().sort((o1, o2) ->{
            try {
                if (o1.getVirtualFile() instanceof VirtualDirectoryImpl
                        && o2.getVirtualFile() instanceof VirtualFileImpl) {
                    return -1;
                }

                if (o2.getVirtualFile() instanceof VirtualDirectoryImpl
                        && o1.getVirtualFile() instanceof VirtualFileImpl) {
                    return 1;
                }
                return o1.getVirtualFile().getName().compareToIgnoreCase(o2.getVirtualFile().getName());
            } catch (Exception ex) {
                log.debug("sort error");
            }
            return 0;
        });
        return root;
    }

    public static void setNodeRootPy(VirtualFileNode leaf) {
        leaf.setMarkedPy(true);
        while (leaf.getParent() != null && !leaf.getParent().isMarkedPy()) {
            leaf.setMarkedPy(true);
            leaf = leaf.getParent();
        }
    }

    public static void clearUnmarkedPyDic(VirtualFileNode root) {
        Iterator<VirtualFileNode> iterator = root.getChildren().iterator();
        while (iterator.hasNext()) {
            VirtualFileNode virtualFileNode = iterator.next();
            if (!virtualFileNode.isMarkedPy()) {
                iterator.remove();
            } else  {
                if (virtualFileNode.getChildren() != null) {
                    clearUnmarkedPyDic(virtualFileNode);
                }
            }
        }
    }
}