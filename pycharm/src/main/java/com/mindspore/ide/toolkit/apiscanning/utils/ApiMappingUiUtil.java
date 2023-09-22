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
    private static final String NEW_LINE = "\n";

    private static final String API = "可以转换为MindSpore API的PyTorch/TensorFlow API";

    private static final String API_NULL = "暂未提供直接映射关系的PyTorch API";

    private static final String PAPI = "可能是torch.Tensor API的结果";

    public static void buttonListener(JButton export, ActionListener actionListener) {
        // 导出事件
        export.addActionListener(actionListener);
    }


    public static StringBuilder initData(Object[][] api, Object[][] apiNull, Object[][] papi) {
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
