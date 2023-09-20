package com.mindspore.ide.toolkit.apiscanning;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.mindspore.ide.toolkit.apiscanning.utils.ApiMappingUiUtil;
import com.mindspore.ide.toolkit.apiscanning.utils.VirtualFileTreeUtil;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;

public class ApiMappingProjectUI {
    public JSplitPane splitPane;

    private static final String[] API_COLUMNS =
            {"Original Api", "Original Api Version", "MindSpore Api", "Description", "Platform"};

    private static final String[] API_NULL_COLUMNS =
            {"Original Api", "Original Api Version", "MindSpore Api", "Description"};

    private static final String[] PAPI_COLUMNS =
            {"Original Api", "Original Api Version", "MindSpore Api", "Description"};

    private Project project;

    private Object[][] api;

    private Object[][] papi;

    private Object[][] apiNull;

    private VirtualFile root;

    private VirtualFile chosenFile;

    private JTable apiJTable;

    private JTable apiNullJTable;

    private JLabel emptyJLabel;
    private JPanel apiJPanel;
    private JLabel papiJLabel;
    private JButton export;
    private JLabel apiNullJLabel;
    private JPanel apiNullJPanel;
    private JPanel papiJPanel;
    private JTree tree1;
    private JPanel main;
    private JLabel apiJLabel;

    private JTable papiJTable;
    private ActionListener actionListener;

    public ApiMappingProjectUI(Object[][] api, Object[][] papi, Object[][] apiNull, Project project, VirtualFile root,
                               VirtualFile choseFile) {
        this.project = project;
        this.api = api;
        this.papi = papi;
        this.apiNull = apiNull;
        this.root = root;
        this.chosenFile = choseFile;
        emptyJLabel.setVisible(false);
        addApiPanel();
        addApiNullPanel();
        addPapiPanel();
        setEmptyPanel();
        actionListener = e -> {
            ExportDialog exportListDialog = new ExportDialog(ApiMappingUiUtil.initData(api, apiNull, papi).toString(),
                    root.getName());
            exportListDialog.setVisible(true);
        };
        ApiMappingUiUtil.buttonListener(export, actionListener);
    }

    /**
     * reload ui form
     *
     * @param api api array
     * @param papi papi array
     * @param apiNull null api array
     * @param fileName trigger file name
     */
    public void reload(Object[][] api, Object[][] papi, Object[][] apiNull, String fileName) {
        this.api = api;
        this.papi = papi;
        this.apiNull = apiNull;
        apiJPanel.removeAll();
        apiNullJPanel.removeAll();
        papiJPanel.removeAll();
        emptyJLabel.setVisible(false);
        addApiPanel();
        addApiNullPanel();
        addPapiPanel();
        setEmptyPanel();
        export.removeActionListener(actionListener);
        actionListener = e -> {
            ExportDialog exportListDialog = new ExportDialog(ApiMappingUiUtil.initData(api, apiNull, papi).toString(),
                    fileName);
            exportListDialog.setVisible(true);
        };
        ApiMappingUiUtil.buttonListener(export, actionListener);
    }

    private void setEmptyPanel() {
        if (!apiJLabel.isVisible() && !apiNullJLabel.isVisible() && !papiJLabel.isVisible()) {
            emptyJLabel.setVisible(true);
        }
    }

    private void addApiPanel() {
        if (api == null || api.length == 0) {
            apiJLabel.setVisible(false);
            return;
        }
        apiJTable = new JTable(new MsTableModel(api, API_COLUMNS));
        apiJTable.setDefaultRenderer(Object.class, new MsCellRender());
        apiJTable.addMouseListener(ApiMappingUiUtil.createListener(api, apiJTable, project));
        apiJPanel.add(apiJTable.getTableHeader(), BorderLayout.NORTH);
        apiJPanel.add(apiJTable, BorderLayout.CENTER);
        apiJLabel.setVisible(true);
    }

    private void addApiNullPanel() {
        if (apiNull == null || apiNull.length == 0) {
            apiNullJLabel.setVisible(false);
            return;
        }
        apiNullJTable = new JTable(new MsTableModel(apiNull, API_NULL_COLUMNS));
        apiNullJTable.setDefaultRenderer(Object.class, new MsCellRender());
        apiNullJTable.addMouseListener(ApiMappingUiUtil.createListener(apiNull, apiNullJTable, project));
        apiNullJPanel.add(apiNullJTable.getTableHeader(), BorderLayout.NORTH);
        apiNullJPanel.add(apiNullJTable, BorderLayout.CENTER);
        apiNullJLabel.setVisible(true);
    }

    private void addPapiPanel() {
        if (papi == null || papi.length == 0) {
            papiJLabel.setVisible(false);
            return;
        }
        papiJTable = new JTable(new MsTableModel(papi, PAPI_COLUMNS));
        papiJTable.setDefaultRenderer(Object.class, new MsCellRender());
        papiJTable.addMouseListener(ApiMappingUiUtil.createListener(papi, papiJTable, project));
        papiJPanel.add(papiJTable.getTableHeader(), BorderLayout.NORTH);
        papiJPanel.add(papiJTable, BorderLayout.CENTER);
        papiJLabel.setVisible(true);
    }

    private void createUIComponents() {
        VirtualFileNode root = VirtualFileTreeUtil.initVirtualFileNode(new VirtualFileNode(chosenFile), project);
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
    }
}
