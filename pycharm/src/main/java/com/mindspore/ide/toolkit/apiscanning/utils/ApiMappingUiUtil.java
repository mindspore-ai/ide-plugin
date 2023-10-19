package com.mindspore.ide.toolkit.apiscanning.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.mindspore.ide.toolkit.search.entity.LinkInfo;
import com.mindspore.ide.toolkit.ui.search.BrowserWindowManager;

import javax.swing.JButton;
import javax.swing.JTable;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ApiMappingUiUtil {
    public static final String NEW_LINE = "\n";

    public static final String[] API_COLUMNS =
            {"Original Api", "Original Api Version", "MindSpore Api", "Platform", "Description"};

    public static final String[] API_NULL_COLUMNS =
            {"Original Api", "Description","Missing API Processing Policy"};

    public static final String[] PAPI_COLUMNS =
            {"Original Api", "Original Api Version", "MindSpore Api", "Platform", "Description"};

    public static final String API = "可以转换为MindSpore API的PyTorch API";

    public static final String API_NULL = "暂未提供直接映射关系的PyTorch API";

    public static final String PAPI = "可能是torch.Tensor API的结果";

    public static void buttonListener(JButton export, ActionListener actionListener) {
        // 导出事件
        export.addActionListener(actionListener);
    }


    public static StringBuilder initData(Object[][] api, Object[][] apiNull, Object[][] papi) {
        StringBuilder str = new StringBuilder();
        if (api.length > 0) {
            str.append(API).append(",").append(NEW_LINE);
            str.append(String.join(",", API_COLUMNS)).append(",").append(NEW_LINE);
            append(str, api);
            str.append(",").append(NEW_LINE);
        }
        if (papi.length > 0) {
            str.append(PAPI).append(",").append(NEW_LINE);
            str.append(String.join(",", PAPI_COLUMNS)).append(",").append(NEW_LINE);
            append(str, papi);
            str.append(",").append(NEW_LINE);
        }
        if (apiNull.length > 0) {
            str.append(API_NULL).append(",").append(NEW_LINE);
            str.append(String.join(",", API_NULL_COLUMNS)).append(",").append(NEW_LINE);
            append(str, apiNull);
        }
        return str;
    }

    public static void append(StringBuilder builder, Object[][] table) {
        if (table == null) {
            return;
        }
        for (Object[] objects : table) {
            for (Object object : objects) {
                if (object instanceof LinkInfo) {
                    LinkInfo linkInfo = (LinkInfo) object;
                    String text =
                            String.format("\"=HYPERLINK(\"\"%s\"\",\"\"%s\"\")\"", linkInfo.getUrl(),
                                    linkInfo.getText() + linkInfo.getVersionString());
                    builder.append(text.trim()).append(",");
                } else {
                    builder.append(object == null ? "" : object.toString().trim()).append(",");
                }
            }
            builder.setLength(builder.length() - 1);
            builder.append(NEW_LINE);
        }
    }

    public static MouseAdapter createListener(Object[][] data, JTable table, Project project) {
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
