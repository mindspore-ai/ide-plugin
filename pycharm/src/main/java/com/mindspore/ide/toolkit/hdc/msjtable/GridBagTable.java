/*
 * Copyright 2021-2022 Huawei Technologies Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mindspore.ide.toolkit.hdc.msjtable;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.EventObject;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * GridBagTable
 *
 * @since 2022-04-18
 */
public class GridBagTable extends JTable {
    GridBagModel gridBagModel;

    /**
     * GridBagTable
     *
     * @param dm dm
     */
    public GridBagTable(AbstractTableModel dm) {
        super(dm);
        getTableHeader().setReorderingAllowed(false);
        gridBagModel = new DefaultGridBagTableModel(dm);
        getColumnModel().setColumnSelectionAllowed(true);
    }

    /**
     * get GridBagModel
     *
     * @return GridBagModel
     */
    public GridBagModel getGridBagModel() {
        return gridBagModel;
    }

    /**
     * set GridBagModel
     *
     * @param gridBagModel gridBagModel
     */
    public void setGridBagModel(GridBagModel gridBagModel) {
        if (gridBagModel != null && gridBagModel != this.gridBagModel) {
            this.gridBagModel = gridBagModel;
        }
    }

    private void updateSubComponentUI(Object componentShell) {
        if (componentShell == null) {
            return;
        }
        Component component = null;
        if (componentShell instanceof Component) {
            component = (Component) componentShell;
        }
        if (componentShell instanceof DefaultCellEditor) {
            component = ((DefaultCellEditor) componentShell).getComponent();
        }
        if (component != null) {
            SwingUtilities.updateComponentTreeUI(component);
        }
    }

    /**
     * update UI
     */
    public void updateUI() {
        // Update the UIs of the cell renderers, cell editors and header renderers.
        TableColumnModel cm = getColumnModel();
        for (int column = 0; column < cm.getColumnCount(); column++) {
            TableColumn aColumn = cm.getColumn(column);
            updateSubComponentUI(aColumn.getCellRenderer());
            updateSubComponentUI(aColumn.getCellEditor());
            updateSubComponentUI(aColumn.getHeaderRenderer());
        }
        // Update the UIs of all the default renderers.
        Enumeration defaultRenderers = defaultRenderersByColumnClass.elements();
        while (defaultRenderers.hasMoreElements()) {
            updateSubComponentUI(defaultRenderers.nextElement());
        }
        // Update the UIs of all the default editors.
        Enumeration defaultEditors = defaultEditorsByColumnClass.elements();
        while (defaultEditors.hasMoreElements()) {
            updateSubComponentUI(defaultEditors.nextElement());
        }
        // Update the UI of the table header
        if (tableHeader != null && tableHeader.getParent() == null) {
            tableHeader.updateUI();
        }
        setUI(new GridBagTableUI());
    }

    /**
     * get GridCellRect
     *
     * @param row              row
     * @param column           column
     * @param isIncludeSpacing isIncludeSpacing
     * @return Rectangle
     */
    public Rectangle getGridCellRect(int row, int column, boolean isIncludeSpacing) {
        return super.getCellRect(row, column, isIncludeSpacing);
    }

    /**
     * get CellRect
     *
     * @param row              row
     * @param column           column
     * @param isIncludeSpacing isIncludeSpacing
     * @return Rectangle
     */
    public Rectangle getCellRect(int row, int column, boolean isIncludeSpacing) {
        Rectangle cellRect = super.getCellRect(row, column, isIncludeSpacing);
        int cols = gridBagModel.getColumnGrid(row, column);
        TableColumnModel cm = getColumnModel();
        for (int n = 1; n < cols; n++) {
            cellRect.width += cm.getColumn(column + n).getWidth();
        }
        int rows = gridBagModel.getRowGrid(row, column);
        for (int n = 1; n < rows; n++) {
            cellRect.height += getRowHeight(row + n);
        }
        return cellRect;
    }

    /**
     * tableChanged
     *
     * @param tableModelEvent tableModelEvent
     */
    public void tableChanged(TableModelEvent tableModelEvent) {
        super.tableChanged(tableModelEvent);
    }

    /**
     * merge Cells
     *
     * @param startRow    startRow
     * @param endRow      endRow
     * @param startColumn startColumn
     * @param endColumn   endColumn
     * @return boolean
     */
    public boolean mergeCells(int startRow, int endRow, int startColumn, int endColumn) {
        if (gridBagModel.mergeCells(startRow, endRow, startColumn, endColumn)) {
            repaint();
            return true;
        }
        return false;
    }

    /**
     * merge Cells
     *
     * @param rows    rows
     * @param columns columns
     * @return boolean
     */
    public boolean mergeCells(int[] rows, int[] columns) {
        if (gridBagModel.mergeCells(rows, columns)) {
            repaint();
            return true;
        }
        return false;
    }

    /**
     * split Cell At
     *
     * @param row    row
     * @param column column
     * @return boolean
     */
    public boolean splitCellAt(int row, int column) {
        if (gridBagModel.spliteCellAt(row, column)) {
            repaint();
            return true;
        }
        return false;
    }

    /**
     * change Selection
     *
     * @param inputRow    inputRow
     * @param inputColumn inputColumn
     * @param isToggle    isToggle
     * @param isExtend    isExtend
     */
    public void changeSelection(int inputRow, int inputColumn, boolean isToggle, boolean isExtend) {
        int inputRowNew = inputRow;
        int inputColumnNew = inputColumn;
        if (gridBagModel.getCellState(inputRowNew, inputColumnNew) != GridBagModel.COVERED) {
            super.changeSelection(inputRowNew, inputColumnNew, isToggle, isExtend);
        }
        Point po;
        for (int row = inputRowNew; row >= 0; row--) {
            for (int col = inputColumnNew; col >= 0; col--) {
                po = gridBagModel.getGrid(row, col);
                if (col + po.x > inputColumnNew && row + po.y > inputRowNew) {
                    inputRowNew = row;
                    inputColumnNew = col;
                    break;
                }
            }
        }
        super.changeSelection(inputRowNew, inputColumnNew, isToggle, isExtend);
        repaint();
    }

    /**
     * edit Cell At
     *
     * @param inputRow    inputRow
     * @param inputColumn inputColumn
     * @param eventObject eventObject
     * @return boolean
     */
    public boolean editCellAt(int inputRow, int inputColumn, EventObject eventObject) {
        int inputRowNew = inputRow;
        int inputColumnNew = inputColumn;
        if (gridBagModel.getCellState(inputRowNew, inputColumnNew) != GridBagModel.COVERED) {
            return super.editCellAt(inputRowNew, inputColumnNew, eventObject);
        }
        Point po;
        for (int row = inputRowNew; row >= 0; row--) {
            for (int col = inputColumnNew; col >= 0; col--) {
                po = gridBagModel.getGrid(row, col);
                if (col + po.x > inputColumnNew && row + po.y > inputRowNew) {
                    inputRowNew = row;
                    inputColumnNew = col;
                    break;
                }
            }
        }
        return super.editCellAt(inputRowNew, inputColumnNew, eventObject);
    }
}