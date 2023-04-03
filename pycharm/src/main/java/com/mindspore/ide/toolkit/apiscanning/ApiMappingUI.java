package com.mindspore.ide.toolkit.apiscanning;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.mindspore.ide.toolkit.search.entity.LinkInfo;
import com.mindspore.ide.toolkit.ui.search.BrowserWindowManager;

import java.awt.BorderLayout;
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
            {"Original Api", "Original Api Version", "MindSpore Api", "Description"};

    private static final String[] API_NULL_COLUMNS=
            {"Original Api", "Original Api Version", "MindSpore Api", "Description"};

    private static final String[] PAPI_COLUMNS=
            {"Original Api", "Original Api Version", "MindSpore Api", "Description"};

    private static final String NEW_LINE = "\n";

    private static final String API = "可以转换为MindSpore API的PyTorch/TensorFlow API";

    private static final String API_NULL = "暂时不能转换的API";

    private static final String PAPI = "可能是PyTorch/TensorFlow API的情况";

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

    private JTable papiJTable;

    private String fileName;

    /**
     * Construction method
     *
     * @param api api
     * @param papi papi
     * @param apiNull apiNull
     * @param project project
     * @param name trigger file name
     */
    public ApiMappingUI(Object[][] api, Object[][] papi, Object[][] apiNull, Project project, String name) {
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
     * @param api api
     * @param papi papi
     * @param apiNull apiNull
     * @param project project
     * @param name trigger file name
     * @return JComponent
     */
    public static JComponent build(Object[][] api, Object[][] papi, Object[][] apiNull, Project project, String name) {
        return new JScrollPane(new ApiMappingUI(api, papi, apiNull, project, name).main);
    }

    private void addApiPanel() {
        apiJTable = new JTable(new MsTableModel(api, API_COLUMNS));
        apiJTable.setDefaultRenderer(Object.class, new MsCellRender());
        apiJTable.addMouseListener(createListener(api, apiJTable));
        apiJPanel.add(apiJTable.getTableHeader(), BorderLayout.NORTH);
        apiJPanel.add(apiJTable, BorderLayout.SOUTH);
    }

    private void addApiNullPanel() {
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

    private void addPapiPanel() {
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

    private void buttonListener() {
        // 导出事件
        export.addActionListener(e -> {
            ExportDialog exportListDialog = new ExportDialog(initData().toString(), fileName);
            exportListDialog.setVisible(true);
        });
    }


    private StringBuilder initData() {
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

    private void append(StringBuilder builder, Object[][] table) {
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

    private MouseAdapter createListener(Object[][] data, JTable table) {
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
}
