package com.mindspore.ide.toolkit.apiscanning;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.mindspore.ide.toolkit.apiscanning.handler.ApiMappingHandler;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

public class VirtualFileTreeSelectionListener implements TreeSelectionListener {
    private Project project;
    private static final Logger log = Logger.getInstance(VirtualFileTreeSelectionListener.class);

    public VirtualFileTreeSelectionListener(Project project) {
        this.project = project;
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        log.info("tree node selected");
        TreePath virtualFileNodePath = e.getNewLeadSelectionPath();
        VirtualFileNode virtualFileNode = (VirtualFileNode) virtualFileNodePath.getLastPathComponent();
        try {
            new ApiMappingHandler(project).handleTreeNodeSelection(virtualFileNode);
        } catch (Exception ex) {
            log.info(ex);
        }
    }
}
