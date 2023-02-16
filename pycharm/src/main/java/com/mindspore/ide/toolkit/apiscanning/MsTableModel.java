package com.mindspore.ide.toolkit.apiscanning;

import javax.swing.table.DefaultTableModel;

/**
 * xxx
 *
 * @since 2022-12-16
 */
public class MsTableModel extends DefaultTableModel {
    public MsTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
