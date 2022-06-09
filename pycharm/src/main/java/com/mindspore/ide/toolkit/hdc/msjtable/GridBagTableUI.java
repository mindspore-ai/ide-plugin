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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Enumeration;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * GridBagTableUI
 *
 * @since 2022-04-18
 */
public class GridBagTableUI extends BasicTableUI {
    int oldHeightPaintCells;
    JTableHeader headerPaintCells;
    TableColumn draggedColumnPaintCells;
    TableColumnModel cmPaintCells;
    int columnMarginPaintCells;
    Rectangle cellRectPaintCells;
    TableColumn aColumnPaintCells;
    int columnWidthPaintCells;

    /**
     * get Preferred Size
     *
     * @param jc jc
     * @return Dimension
     */
    public Dimension getPreferredSize(JComponent jc) {
        long width = 0L;
        Enumeration<TableColumn> enumeration = table.getColumnModel().getColumns();
        while (enumeration.hasMoreElements()) {
            TableColumn aColumn = enumeration.nextElement();
            width = width + aColumn.getPreferredWidth();
        }
        return createTableSize(width);
    }

    private Dimension createTableSize(long width) {
        int height = 0;
        int rowCount = table.getRowCount();
        if (rowCount > 0 && table.getColumnCount() > 0) {
            Rectangle re = table.getCellRect(rowCount - 1, 0, true);
            height = re.y + re.height;
        }
        // Width is always positive. The call to abs() is a workaround for
        // a bug in the 1.1.6 JIT on Windows.
        long tmp = Math.abs(width);
        if (tmp > Integer.MAX_VALUE) {
            tmp = Integer.MAX_VALUE;
        }
        return new Dimension((int) tmp, height);
    }

    /**
     * paint
     *
     * @param gr gr
     * @param jc jc
     */
    public void paint(Graphics gr, JComponent jc) {
        Rectangle clip = gr.getClipBounds();
        Rectangle bounds = table.getBounds();
        // account for the fact that the graphics has already been translated
        // into the table's bounds
        bounds.x = bounds.y = 0;
        // this check prevents us from painting the entire table
        // when the clip doesn't intersect our bounds at all
        if (table.getRowCount() <= 0 || table.getColumnCount() <= 0 || !bounds.intersects(clip)) {
            paintDropLines(gr);
            return;
        }
        boolean isLtr = table.getComponentOrientation().isLeftToRight();
        Point upperLeft = clip.getLocation();
        if (!isLtr) {
            upperLeft.x++;
        }
        Point lowerRight = new Point(clip.x + clip.width - (isLtr ? 1 : 0), clip.y + clip.height);
        int rMin = table.rowAtPoint(upperLeft);
        int rMax = table.rowAtPoint(lowerRight);
        // This should never happen (as long as our bounds intersect the clip,
        // which is why we bail above if that is the case).
        if (rMin == -1) {
            rMin = 0;
        }
        // If the table does not have enough rows to fill the view we'll get -1.
        // (We could also get -1 if our bounds don't intersect the clip,
        // which is why we bail above if that is the case).
        // Replace this with the index of the last row.
        if (rMax == -1) {
            rMax = table.getRowCount() - 1;
        }
        int cMin = table.columnAtPoint(isLtr ? upperLeft : lowerRight);
        int cMax = table.columnAtPoint(isLtr ? lowerRight : upperLeft);
        // This should never happen.
        if (cMin == -1) {
            cMin = 0;
        }
        // If the table does not have enough columns to fill the view we'll get -1.
        // Replace this with the index of the last column.
        if (cMax == -1) {
            cMax = table.getColumnCount() - 1;
        }
        // Paint the cells.
        paintCells(gr, rMin, rMax, cMin, cMax);
        paintDropLines(gr);
    }

    private void paintDropLines(Graphics gr) {
        JTable.DropLocation loc = table.getDropLocation();
        if (loc == null) {
            return;
        }
        Color color = UIManager.getColor("Table.dropLineColor");
        Color shortColor = UIManager.getColor("Table.dropLineShortColor");
        if (color == null && shortColor == null) {
            return;
        }
        Rectangle inputRect = getHDropLineRect(loc);
        if (inputRect != null) {
            int rx = inputRect.x;
            int rw = inputRect.width;
            if (color != null) {
                extendRect(inputRect, true);
                gr.setColor(color);
                gr.fillRect(inputRect.x, inputRect.y, inputRect.width, inputRect.height);
            }
            if (!loc.isInsertColumn() && shortColor != null) {
                gr.setColor(shortColor);
                gr.fillRect(rx, inputRect.y, rw, inputRect.height);
            }
        }
        inputRect = getVDropLineRect(loc);
        if (inputRect != null) {
            int ry = inputRect.y;
            int rh = inputRect.height;
            if (color != null) {
                extendRect(inputRect, false);
                gr.setColor(color);
                gr.fillRect(inputRect.x, inputRect.y, inputRect.width, inputRect.height);
            }
            if (!loc.isInsertRow() && shortColor != null) {
                gr.setColor(shortColor);
                gr.fillRect(inputRect.x, ry, inputRect.width, rh);
            }
        }
    }

    /**
     * Paints the grid lines within <I>aRect</I>, using the grid
     * color set with <I>setGridColor</I>. Paints vertical lines
     * if <code>getShowVerticalLines()</code> returns true and paints
     * horizontal lines if <code>getShowHorizontalLines()</code>  returns true.
     *
     * @param gr   gr
     * @param rMin rMin
     * @param rMax rMax
     * @param cMin cMin
     * @param cMax cMax
     */
    private void paintGrid(Graphics gr, int rMin, int rMax, int cMin, int cMax) {
        gr.setColor(table.getGridColor());
        Rectangle minCell = table.getCellRect(rMin, cMin, true);
        Rectangle maxCell = table.getCellRect(rMax, cMax, true);
        Rectangle damagedArea = minCell.union(maxCell);
        if (table.getShowHorizontalLines()) {
            int tableWidth = damagedArea.x + damagedArea.width;
            int dy = damagedArea.y;
            for (int row = rMin; row <= rMax; row++) {
                dy += table.getRowHeight(row);
                gr.drawLine(damagedArea.x, dy - 1, tableWidth - 1, dy - 1);
            }
        }
        if (table.getShowVerticalLines()) {
            TableColumnModel cm = table.getColumnModel();
            int tableHeight = damagedArea.y + damagedArea.height;
            int dx;
            if (table.getComponentOrientation().isLeftToRight()) {
                dx = damagedArea.x;
                for (int column = cMin; column <= cMax; column++) {
                    int cmw = cm.getColumn(column).getWidth();
                    dx += cmw;
                    gr.drawLine(dx - 1, 0, dx - 1, tableHeight - 1);
                }
            } else {
                dx = damagedArea.x;
                for (int column = cMax; column >= cMin; column--) {
                    int cmw = cm.getColumn(column).getWidth();
                    dx += cmw;
                    gr.drawLine(dx - 1, 0, dx - 1, tableHeight - 1);
                }
            }
        }
    }

    private void paintCells(Graphics gr, int rMin, int rMax, int cMin, int cMax) {
        headerPaintCells = table.getTableHeader();
        draggedColumnPaintCells = (headerPaintCells == null) ? null : headerPaintCells.getDraggedColumn();
        cmPaintCells = table.getColumnModel();
        columnMarginPaintCells = cmPaintCells.getColumnMargin();
        if (table.getComponentOrientation().isLeftToRight()) {
            ifPaintCells(gr, rMin, rMax, cMin, cMax);
        } else {
            elsePaintCells(gr, rMin, rMax, cMin, cMax);
        }
        // Paint the dragged column if we are dragging.
        if (draggedColumnPaintCells != null) {
            paintDraggedArea(gr, rMin, rMax, draggedColumnPaintCells, headerPaintCells.getDraggedDistance());
        }
        // Remove any renderers that may be left in the rendererPane.
        rendererPane.removeAll();
    }

    private void ifPaintCells(Graphics gr, int rMin, int rMax, int cMin, int cMax) {
        for (int row = rMin; row <= rMax; row++) {
            if (table instanceof GridBagTable) {
                cellRectPaintCells = ((GridBagTable) table).getGridCellRect(row, cMin, false);
            } else {
                cellRectPaintCells = table.getCellRect(row, cMin, false);
            }
            for (int column = cMin; column <= cMax; column++) {
                aColumnPaintCells = cmPaintCells.getColumn(column);
                columnWidthPaintCells = aColumnPaintCells.getWidth();
                cellRectPaintCells.width = columnWidthPaintCells - columnMarginPaintCells;
                oldHeightPaintCells = cellRectPaintCells.height;
                ifPaintCellsTwo(row, column);
                if (aColumnPaintCells != draggedColumnPaintCells) {
                    paintCell(gr, cellRectPaintCells, row, column);
                }
                cellRectPaintCells.height = oldHeightPaintCells;
                cellRectPaintCells.x += columnWidthPaintCells;
            }
        }
    }

    private void ifPaintCellsTwo(int row, int column) {
        if (table instanceof GridBagTable) {
            GridBagTable gridBagTable = (GridBagTable) table;
            if (gridBagTable.getGridBagModel()
                    .getCellState(row, column) == GridBagModel.COVERED) {
                cellRectPaintCells.width = 0;
                cellRectPaintCells.height = 0;
            } else {
                ifPaintCellsThree(row, column, gridBagTable);
            }
        }
    }

    private void ifPaintCellsThree(int row, int column, GridBagTable gridBagTable) {
        int th = gridBagTable.getGridBagModel().getColumnGrid(row, column);
        if (th > 1) {
            for (int n = 1; n < th; n++) {
                cellRectPaintCells.width += cmPaintCells.getColumn(column + n).getWidth();
            }
        }
        int vr = gridBagTable.getGridBagModel().getRowGrid(row, column);
        if (vr > 1) {
            for (int n = 1; n < vr; n++) {
                cellRectPaintCells.height += table.getRowHeight(row + n);
            }
        }
    }

    private void elsePaintCells(Graphics gr, int rMin, int rMax, int cMin, int cMax) {
        for (int row = rMin; row <= rMax; row++) {
            cellRectPaintCells = table.getCellRect(row, cMin, false);
            aColumnPaintCells = cmPaintCells.getColumn(cMin);
            if (aColumnPaintCells != draggedColumnPaintCells) {
                columnWidthPaintCells = aColumnPaintCells.getWidth();
                cellRectPaintCells.width = columnWidthPaintCells - columnMarginPaintCells;
                paintCell(gr, cellRectPaintCells, row, cMin);
            }
            for (int column = cMin + 1; column <= cMax; column++) {
                aColumnPaintCells = cmPaintCells.getColumn(column);
                columnWidthPaintCells = aColumnPaintCells.getWidth();
                cellRectPaintCells.width = columnWidthPaintCells - columnMarginPaintCells;
                cellRectPaintCells.x -= columnWidthPaintCells;
                if (aColumnPaintCells != draggedColumnPaintCells) {
                    paintCell(gr, cellRectPaintCells, row, column);
                }
            }
        }
    }

    private void paintCell(Graphics gr, Rectangle cellRect, int row, int column) {
        if (table.isEditing() && table.getEditingRow() == row && table.getEditingColumn() == column) {
            Component component = table.getEditorComponent();
            component.setBounds(cellRect);
            component.validate();
        } else {
            TableCellRenderer renderer = table.getCellRenderer(row, column);
            Component component = table.prepareRenderer(renderer, row, column);
            if (component instanceof JComponent) {
                ((JComponent) component).setBorder(BorderFactory.createLineBorder(Color.gray));
            }
            rendererPane.paintComponent(gr, component, table, cellRect.x,
                    cellRect.y, cellRect.width, cellRect.height, true);
        }
    }

    private Rectangle getHDropLineRect(JTable.DropLocation loc) {
        if (!loc.isInsertRow()) {
            return new Rectangle();
        }
        int row = loc.getRow();
        int col = loc.getColumn();
        if (col >= table.getColumnCount()) {
            col--;
        }
        Rectangle rect = table.getCellRect(row, col, true);
        if (row >= table.getRowCount()) {
            row--;
            Rectangle prevRect = table.getCellRect(row, col, true);
            rect.y = prevRect.y + prevRect.height;
        }
        if (rect.y == 0) {
            rect.y = -1;
        } else {
            rect.y -= 2;
        }
        rect.height = 3;
        return rect;
    }

    private void paintDraggedArea(Graphics gr, int rMin, int rMax, TableColumn draggedColumn, int distance) {
        int draggedColumnIndex = viewIndexForColumn(draggedColumn);
        Rectangle minCell = table.getCellRect(rMin, draggedColumnIndex, true);
        Rectangle maxCell = table.getCellRect(rMax, draggedColumnIndex, true);
        Rectangle vacatedColumnRect = minCell.union(maxCell);
        // Paint a gray well in place of the moving column.
        gr.setColor(table.getParent().getBackground());
        gr.fillRect(vacatedColumnRect.x, vacatedColumnRect.y, vacatedColumnRect.width, vacatedColumnRect.height);
        // Move to the where the cell has been dragged.
        vacatedColumnRect.x += distance;
        // Fill the background.
        gr.setColor(table.getBackground());
        gr.fillRect(vacatedColumnRect.x, vacatedColumnRect.y, vacatedColumnRect.width, vacatedColumnRect.height);
        // Paint the vertical grid lines if necessary.
        if (table.getShowVerticalLines()) {
            gr.setColor(table.getGridColor());
            int x1 = vacatedColumnRect.x;
            int y1 = vacatedColumnRect.y;
            int x2 = x1 + vacatedColumnRect.width - 1;
            int y2 = y1 + vacatedColumnRect.height - 1;
            // Left
            gr.drawLine(x1 - 1, y1, x1 - 1, y2);
            // Right
            gr.drawLine(x2, y1, x2, y2);
        }
        for (int row = rMin; row <= rMax; row++) {
            // Render the cell value
            Rectangle re = table.getCellRect(row, draggedColumnIndex, false);
            re.x += distance;
            paintCell(gr, re, row, draggedColumnIndex);
            // Paint the (lower) horizontal grid line if necessary.
            if (table.getShowHorizontalLines()) {
                gr.setColor(table.getGridColor());
                Rectangle rcr = table.getCellRect(row, draggedColumnIndex, true);
                rcr.x += distance;
                int x1 = rcr.x;
                int y1 = rcr.y;
                int x2 = x1 + rcr.width - 1;
                int y2 = y1 + rcr.height - 1;
                gr.drawLine(x1, y2, x2, y2);
            }
        }
    }

    private int viewIndexForColumn(TableColumn aColumn) {
        TableColumnModel cm = table.getColumnModel();
        for (int column = 0; column < cm.getColumnCount(); column++) {
            if (cm.getColumn(column) == aColumn) {
                return column;
            }
        }
        return -1;
    }

    private Rectangle extendRect(Rectangle rect, boolean isHorizontal) {
        if (rect == null) {
            return rect;
        }
        if (isHorizontal) {
            rect.x = 0;
            rect.width = table.getWidth();
        } else {
            rect.y = 0;
            if (table.getRowCount() != 0) {
                Rectangle lastRect = table.getCellRect(table.getRowCount() - 1, 0, true);
                rect.height = lastRect.y + lastRect.height;
            } else {
                rect.height = table.getHeight();
            }
        }
        return rect;
    }

    private Rectangle getVDropLineRect(JTable.DropLocation loc) {
        if (!loc.isInsertColumn()) {
            return new Rectangle();
        }
        boolean isLtr = table.getComponentOrientation().isLeftToRight();
        int col = loc.getColumn();
        Rectangle rect = table.getCellRect(loc.getRow(), col, true);
        if (col >= table.getColumnCount()) {
            col--;
            rect = table.getCellRect(loc.getRow(), col, true);
            if (isLtr) {
                rect.x = rect.x + rect.width;
            }
        } else if (!isLtr) {
            rect.x = rect.x + rect.width;
        } else {
            rect.getX();
        }
        if (rect.x == 0) {
            rect.x = -1;
        } else {
            rect.x -= 2;
        }
        rect.width = 3;
        return rect;
    }
}