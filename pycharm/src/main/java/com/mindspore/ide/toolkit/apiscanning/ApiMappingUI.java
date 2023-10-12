package com.mindspore.ide.toolkit.apiscanning;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.mindspore.ide.toolkit.apiscanning.utils.ApiMappingUiUtil;
import com.mindspore.ide.toolkit.search.entity.LinkInfo;
import com.mindspore.ide.toolkit.ui.search.BrowserWindowManager;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * 功能描述
 *
 * @since 2022-12-16
 */
public class ApiMappingUI {
    private static final String[] API_COLUMNS =
            {"Original Api", "Original Api Version", "MindSpore Api", "Platform", "Description"};

    private static final String[] API_NULL_COLUMNS =
            {"Original Api", "Description"};

    private static final String[] PAPI_COLUMNS =
            {"Original Api", "Original Api Version", "MindSpore Api", "Platform", "Description"};

    private static final String NEW_LINE = "\n";

    private static final String API = "可以转换为MindSpore API的PyTorch/TensorFlow API";

    private static final String API_NULL = "暂未提供直接映射关系的PyTorch API";

    private static final String PAPI = "可能是torch.Tensor API的结果";

    private Project project;

    private Object[][] api;

    private Object[][] papi;

    private Object[][] apiNull;

    private JPanel main;

    private JButton export;

    private JPanel apiJPanel;

    private JTable apiJTable;

    private JTable apiNullJTable;

    private JLabel apiNullJLabel;

    private JPanel apiNullJPanel;

    private JPanel papiJPanel;

    private JLabel papiJLabel;
    private JLabel apiJLabel;

    private JTable papiJTable;

    private String fileName;
    private ActionListener actionListener;

    /**
     * Construction method
     */
    public ApiMappingUI() {

    }

    private void init(Object[][] api, Object[][] papi, Object[][] apiNull, Project project, String name) {
        this.project = project;
        this.api = api;
        this.papi = papi;
        this.apiNull = apiNull;
        this.fileName = name;
        addApiPanel();
        addApiNullPanel();
        addPapiPanel();
        buttonListener();
    }

    /**
     * build
     *
     * @param api     api
     * @param papi    papi
     * @param apiNull apiNull
     * @param project project
     * @param name    trigger file name
     * @return JComponent
     */
    public static JComponent build(Object[][] api, Object[][] papi, Object[][] apiNull, Project project, String name) {

        return new JScrollPane(buildSelf(api, papi, apiNull, project, name).main);
    }

    public static ApiMappingUI buildSelf(Object[][] api, Object[][] papi, Object[][] apiNull, Project project,
                                         String name) {
        ApiMappingUI apiMappingUI = new ApiMappingUI();
        apiMappingUI.init(api, papi, apiNull, project, name);
        return apiMappingUI;
    }

    public void reload(Object[][] api, Object[][] papi, Object[][] apiNull, String fileName) {
        this.api = api;
        this.papi = papi;
        this.apiNull = apiNull;
        apiJPanel.removeAll();
        apiNullJPanel.removeAll();
        papiJPanel.removeAll();
        addApiPanel();
        addApiNullPanel();
        addPapiPanel();
        export.removeActionListener(actionListener);
        actionListener = e -> {
            ExportDialog exportListDialog = new ExportDialog(ApiMappingUiUtil.initData(api, apiNull, papi).toString(),
                    fileName);
            exportListDialog.setVisible(true);
        };
        ApiMappingUiUtil.buttonListener(export, actionListener);
    }

    public void addApiPanel() {
        apiJTable = new JTable(new MsTableModel(api, API_COLUMNS));
        apiJTable.setDefaultRenderer(Object.class, new MsCellRender());
        apiJTable.addMouseListener(createListener(api, apiJTable));
        if (api.length > 0) {
            apiJPanel.add(apiJTable.getTableHeader(), BorderLayout.NORTH);
            apiJPanel.add(apiJTable, BorderLayout.SOUTH);
            apiJLabel.setVisible(true);
        } else {
            apiJLabel.setVisible(false);
        }
    }

    public void addApiNullPanel() {
        apiNullJTable = new JTable(new MsTableModel(apiNull, API_NULL_COLUMNS));
        apiNullJTable.setDefaultRenderer(Object.class, new MsCellRender());
        apiNullJTable.addMouseListener(createListener(apiNull, apiNullJTable));
        if (apiNull.length > 0) {
            apiNullJPanel.add(apiNullJTable.getTableHeader(), BorderLayout.NORTH);
            apiNullJPanel.add(apiNullJTable, BorderLayout.SOUTH);
            apiNullJLabel.setVisible(true);
        } else {
            apiNullJLabel.setVisible(false);
        }
    }

    public void addPapiPanel() {
        papiJTable = new JTable(new MsTableModel(papi, PAPI_COLUMNS));
        papiJTable.setDefaultRenderer(Object.class, new MsCellRender());
        papiJTable.addMouseListener(createListener(papi, papiJTable));
        if (papi.length > 0) {
            papiJPanel.add(papiJTable.getTableHeader(), BorderLayout.NORTH);
            papiJPanel.add(papiJTable, BorderLayout.SOUTH);
            papiJLabel.setVisible(true);
        } else {
            papiJLabel.setVisible(false);
        }
    }

    public void buttonListener() {
        // 导出事件
        this.actionListener = e -> {
            ExportDialog exportListDialog = new ExportDialog(initData().toString(), fileName);
            exportListDialog.setVisible(true);
        };
        export.addActionListener(this.actionListener);
    }


    public StringBuilder initData() {
        StringBuilder str = new StringBuilder();
        str.append(API).append(",").append(NEW_LINE);
        append(str, api);
        if (apiNull.length > 0) {
            str.append(",").append(NEW_LINE).append(API_NULL).append(",").append(NEW_LINE);
            append(str, apiNull);
        }
        if (papi.length > 0) {
            str.append(",").append(NEW_LINE).append(PAPI).append(",").append(NEW_LINE);
            append(str, papi);
        }
        return str;
    }

    public void append(StringBuilder builder, Object[][] table) {
        if (table == null) {
            return;
        }
        for (Object[] objects : table) {
            for (Object object : objects) {
                if (object instanceof LinkInfo) {
                    LinkInfo linkInfo = (LinkInfo) object;
                    String text =
                            String.format("\"=HYPERLINK(\"\"%s\"\",\"\"%s\"\")\"", linkInfo.getUrl(), linkInfo.getText() + linkInfo.getVersionString());
                    builder.append(text.trim()).append(",");
                } else {
                    builder.append(object == null ? "" : object.toString().trim()).append(",");
                }
            }
            builder.setLength(builder.length() - 1);
            builder.append(NEW_LINE);
        }
    }

    public MouseAdapter createListener(Object[][] data, JTable table) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != 1 || e.getClickCount() != 1) {
                    return;
                }
                int row = table.rowAtPoint(e.getPoint());
                int column = table.columnAtPoint(e.getPoint());
                Object o = data[row][column];
                if (o instanceof LinkInfo) {
                    LinkInfo linkModel = (LinkInfo) o;
                    BrowserWindowManager.getBrowserWindow(project).loadUrl(linkModel.getUrl());
                    ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
                    ToolWindow toolWindow = toolWindowManager.getToolWindow("MindSpore");
                    toolWindow.show();
                }
            }
        };
    }
    public void setVisible(boolean visible) {
        this.main.setVisible(visible);
    }
}
