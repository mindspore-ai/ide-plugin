package com.mindspore.ide.toolkit.search;

import com.mindspore.ide.toolkit.search.entity.DocumentSearch;
import com.mindspore.ide.toolkit.search.entity.OperatorRecord;

import icons.MsIcons;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

/**
 * xxxx
 *
 * @since 2022-12-13
 */
public class MSElementsRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean isCellHasFocus) {
        if (value instanceof DocumentSearch) {
            JPanel jPanel = new JPanel(new BorderLayout());
            JLabel jLabel = new JLabel();
            jLabel.setIcon(MsIcons.MS_ICON_13PX);
            jLabel.setText(value.toString());
            if (isSelected) {
                jLabel.setForeground(list.getSelectionForeground());
                jPanel.setBackground(list.getSelectionBackground());
            } else {
                jLabel.setForeground(list.getForeground());
                jPanel.setBackground(list.getBackground());
            }
            jPanel.add(jLabel, BorderLayout.CENTER);
            DocumentSearch documentSearch = (DocumentSearch) value;
            if (documentSearch.getValue() instanceof OperatorRecord) {
                OperatorRecord operatorRecord = (OperatorRecord) documentSearch.getValue();
                JLabel jLabel1 = new JLabel();
                jLabel1.setText(operatorRecord.getVersionText());
                jLabel1.setForeground(Color.GRAY);
                jPanel.add(jLabel1, BorderLayout.EAST);
            }
            return jPanel;
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, isCellHasFocus);
    }
}
