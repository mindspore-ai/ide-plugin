package com.mindspore.ide.toolkit.apiscanning;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.mindspore.ide.toolkit.apiscanning.handler.ApiMappingHandler;
import com.mindspore.ide.toolkit.apiscanning.utils.FileScanAgent;
import com.mindspore.ide.toolkit.apiscanning.utils.VirtualFileTreeUtil;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Dimension;
import java.awt.Insets;

public class ApiMappingProjectUI {
    public JSplitPane splitPane;

    public static Project project;

    public static VirtualFile chosenFile;

    private JTree tree1;

    private VirtualFileNode root;
    private ApiMappingUI apiMappingUI;
    private JPanel main;
    private JPanel contentJPanel;
    private JLabel noResultJLabel;

    public ApiMappingProjectUI(Project project, VirtualFile choseFile) {
        this.project = project;
        this.chosenFile = choseFile;
        this.apiMappingUI.setVisible(false);
        this.noResultJLabel.setVisible(false);

    }

    public void initLoad(){
        try {
            new ApiMappingHandler(this.project).handleTreeNodeSelection(this.root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * reload ui form
     *
     * @param fileScanAgent FileScanAgent
     * @param fileName trigger file name
     */
    public void reload(FileScanAgent fileScanAgent, String fileName) {
        fileScanAgent.assembleResultAndSearch();
        Object[][] api = fileScanAgent.apiArray();
        Object[][] papi = fileScanAgent.papiArray();
        Object[][] apiNull = fileScanAgent.apiNullArray();
        if (api.length > 0 || papi.length > 0 || apiNull.length > 0) {
            this.noResultJLabel.setVisible(false);
            this.apiMappingUI.setVisible(true);
            this.apiMappingUI.reload(api, papi, apiNull, fileName);
        } else {
            this.noResultJLabel.setVisible(true);
            this.apiMappingUI.setVisible(false);
        }
    }

    private void createUIComponents() {
        this.root = VirtualFileTreeUtil.initVirtualFileNode(new VirtualFileNode(chosenFile), project);
        VirtualFileTreeUtil.clearUnmarkedPyDic(root);
        tree1 = new JTree(new VirtualFileTreeModel(root));
        tree1.setVisible(true);
        tree1.addTreeSelectionListener(new VirtualFileTreeSelectionListener(project));
        Icon leafIconFolder = IconLoader.getIcon("/icons/folder.svg", ApiMappingProjectUI.class);
        Icon leafIconPythonFile = IconLoader.getIcon("/icons/pythonFile.svg", ApiMappingProjectUI.class);
        if (leafIconFolder != null && leafIconPythonFile != null) {
            DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
            renderer.setLeafIcon(leafIconPythonFile);
            renderer.setOpenIcon(leafIconFolder);
            renderer.setClosedIcon(leafIconFolder);
            renderer.setDisabledIcon(leafIconFolder);
            tree1.setCellRenderer(renderer);
        }
        this.apiMappingUI = ApiMappingUI.buildSelf(new Object[][]{}, new Object[][]{}, new Object[][]{}, project,
                root.toString());
        System.out.println(1);
    }
}
