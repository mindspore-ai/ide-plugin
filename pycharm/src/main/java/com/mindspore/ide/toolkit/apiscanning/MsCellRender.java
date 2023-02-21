package com.mindspore.ide.toolkit.apiscanning;

import com.mindspore.ide.toolkit.search.entity.LinkInfo;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * xxx
 *
 * @since 2022-12-16
 */
public class MsCellRender implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = new JLabel();
        if (value instanceof LinkInfo) {
            LinkInfo linkInfo = (LinkInfo) value;
            String text = String.format("<HTML><a href=\"%s\">%s</a>%s</HTML>", linkInfo.getUrl(), linkInfo.getText(), linkInfo.getVersionString());
            label.setText(text);
        } else {
            label.setText(value == null ? "" : value.toString());
        }
        return label;
    }
}
