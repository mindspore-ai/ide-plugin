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

import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.UIManager;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTableUI;

/**
 * GridBagTableUI
 *
 * @since 2022-04-18
 */
public class GridBagTableUI extends BasicTableUI {
    private Color dropLineColor = UIManager.getColor("Table.dropLineColor");
    private Color dropLineShortColor = UIManager.getColor("Table.dropLineShortColor");
    private int columnMarginPaintCells;
    private Rectangle cellRectPaintCells;
    private TableColumn aColumnPaintCells;
    private int oldHeightPaintCells;
    private JTableHeader headerPaintCells;
    private TableColumn draggedColumnPaintCells;
    private TableColumnModel cmPaintCells;
    private int columnWidthPaintCells;

    /**
     * paint
     *
     * @param graphics   graphics
     * @param jComponent jComponent
     */
    public void paint(Graphics graphics, JComponent jComponent) {
        Rectangle clipRectangle = graphics.getClipBounds();
        Rectangle boundsRectangle = table.getBounds();
        boundsRectangle.x = 0;
        boundsRectangle.y = 0;
        if (table.getRowCount() <= 0 || table.getColumnCount() <= 0 || !boundsRectangle.intersects(clipRectangle)) {
            paintDropLines(graphics);
            return;
        }
        boolean isLtr = table.getComponentOrientation().isLeftToRight();
        Point locationLeft = clipRectangle.getLocation();
        if (!isLtr) {
            locationLeft.x++;
        }
        Point locationRight = new Point(clipRectangle.x + clipRectangle.width - (isLtr ? 1 : 0),
                clipRectangle.y + clipRectangle.height);
        int rowPointMin = table.rowAtPoint(locationLeft);
        if (rowPointMin == -1) {
            rowPointMin = 0;
        }
        int rowPointMax = table.rowAtPoint(locationRight);
        if (rowPointMax == -1) {
            rowPointMax = table.getRowCount() - 1;
        }
        int columnPointMin;
        int columnPointMax;
        if (isLtr) {
            columnPointMin = table.columnAtPoint(locationLeft);
            columnPointMax = table.columnAtPoint(locationRight);
        } else {
            columnPointMin = table.columnAtPoint(locationRight);
            columnPointMax = table.columnAtPoint(locationLeft);
        }
        if (columnPointMin == -1) {
            columnPointMin = 0;
        }
        if (columnPointMax == -1) {
            columnPointMax = table.getColumnCount() - 1;
        }
        paintCells(graphics, rowPointMin, rowPointMax, columnPointMin, columnPointMax);
        paintDropLines(graphics);
    }

    private void paintCell(Graphics gr, Rectangle cellRect, int row, int column) {
        if (table.isEditing() && table.getEditingRow() == row && table.getEditingColumn() == column) {
            paintCellOne(cellRect);
        } else {
            paintCellTwo(gr, cellRect, row, column);
        }
    }

    private void paintCellOne(Rectangle cellRect) {
        Component component = table.getEditorComponent();
        component.setBounds(cellRect);
        component.validate();
    }

    private void paintCellTwo(Graphics gr, Rectangle cellRect, int row, int column) {
        TableCellRenderer renderer = table.getCellRenderer(row, column);
        Component component = table.prepareRenderer(renderer, row, column);
        if (component instanceof JComponent) {
            ((JComponent) component).setBorder(BorderFactory.createLineBorder(Color.gray));
        }
        rendererPane.paintComponent(gr, component, table, cellRect.x,
                cellRect.y, cellRect.width, cellRect.height, true);
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
        if (draggedColumnPaintCells != null) {
            paintDraggedArea(gr, rMin, rMax, draggedColumnPaintCells, headerPaintCells.getDraggedDistance());
        }
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

    private int viewIndexForColumn(TableColumn aColumn) {
        TableColumnModel cm = table.getColumnModel();
        for (int column = 0; column < cm.getColumnCount(); column++) {
            if (cm.getColumn(column) == aColumn) {
                return column;
            }
        }
        return -1;
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

    private void paintDraggedArea(Graphics gr, int rMin, int rMax, TableColumn draggedColumn, int distance) {
        int draggedColumnIndex = viewIndexForColumn(draggedColumn);
        Rectangle minCell = table.getCellRect(rMin, draggedColumnIndex, true);
        Rectangle maxCell = table.getCellRect(rMax, draggedColumnIndex, true);
        Rectangle vacatedColumnRect = minCell.union(maxCell);
        paintDraggedAreaZero(vacatedColumnRect, gr, distance);
        paintDraggedAreaOne(gr, vacatedColumnRect);
        paintDraggedAreaTwo(rMin, rMax, distance, gr, draggedColumnIndex);
    }

    private void paintDraggedAreaZero(Rectangle vacatedColumnRect, Graphics gr, int distance) {
        gr.setColor(table.getParent().getBackground());
        gr.fillRect(vacatedColumnRect.x, vacatedColumnRect.y, vacatedColumnRect.width, vacatedColumnRect.height);
        vacatedColumnRect.x += distance;
        gr.setColor(table.getBackground());
        gr.fillRect(vacatedColumnRect.x, vacatedColumnRect.y, vacatedColumnRect.width, vacatedColumnRect.height);
    }

    private void paintDraggedAreaOne(Graphics gr, Rectangle vacatedColumnRect) {
        if (table.getShowVerticalLines()) {
            gr.setColor(table.getGridColor());
            int x1 = vacatedColumnRect.x;
            int y1 = vacatedColumnRect.y;
            int x2 = x1 + vacatedColumnRect.width - 1;
            int y2 = y1 + vacatedColumnRect.height - 1;
            gr.drawLine(x1 - 1, y1, x1 - 1, y2);
            gr.drawLine(x2, y1, x2, y2);
        }
    }

    private void paintDraggedAreaTwo(int rMin, int rMax, int distance, Graphics gr, int draggedColumnIndex) {
        for (int row = rMin; row <= rMax; row++) {
            Rectangle re = table.getCellRect(row, draggedColumnIndex, false);
            re.x += distance;
            paintCell(gr, re, row, draggedColumnIndex);
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

    private void paintDropLines(Graphics gr) {
        JTable.DropLocation loc = table.getDropLocation();
        if (loc == null) {
            return;
        }
        if (dropLineColor == null && dropLineShortColor == null) {
            return;
        }
        Rectangle inputRect = getHDropLineRect(loc);
        initPaintDropLines(inputRect, dropLineColor, gr, dropLineShortColor, loc);
    }

    private void getVDropLineRectOne(JTable.DropLocation loc, boolean isLtr, int col, Rectangle rect) {
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
    }

    private Rectangle getVDropLineRect(JTable.DropLocation loc) {
        if (!loc.isInsertColumn()) {
            return new Rectangle();
        }
        boolean isLtr = table.getComponentOrientation().isLeftToRight();
        int col = loc.getColumn();
        Rectangle rect = table.getCellRect(loc.getRow(), col, true);
        getVDropLineRectOne(loc, isLtr, col, rect);
        return rect;
    }

    private void initPaintDropLines(Rectangle inputRect, Color color, Graphics gr,
                                    Color shortColor, JTable.DropLocation loc) {
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
        Rectangle lineInputRect = getVDropLineRect(loc);
        int ry = lineInputRect.y;
        int rh = lineInputRect.height;
        if (color != null) {
            extendRect(lineInputRect, false);
            gr.setColor(color);
            gr.fillRect(lineInputRect.x, lineInputRect.y, lineInputRect.width, lineInputRect.height);
        }
        if (!loc.isInsertRow() && shortColor != null) {
            gr.setColor(shortColor);
            gr.fillRect(lineInputRect.x, ry, lineInputRect.width, rh);
        }
    }

    private Rectangle getHDropLineRectOne(JTable.DropLocation loc, int row, int col) {
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

    private Rectangle getHDropLineRect(JTable.DropLocation loc) {
        if (!loc.isInsertRow()) {
            return new Rectangle();
        }
        int row = loc.getRow();
        int col = loc.getColumn();
        return getHDropLineRectOne(loc, row, col);
    }

    private void extendRectOne(Rectangle rectangle) {
        rectangle.y = 0;
        if (table.getRowCount() != 0) {
            Rectangle lastRect = table.getCellRect(table.getRowCount() - 1, 0, true);
            rectangle.height = lastRect.y + lastRect.height;
        } else {
            rectangle.height = table.getHeight();
        }
    }

    private void extendRect(Rectangle rectangle, boolean isHorizontal) {
        if (rectangle == null) {
            return;
        }
        if (isHorizontal) {
            rectangle.x = 0;
            rectangle.width = table.getWidth();
        } else {
            extendRectOne(rectangle);
        }
    }
}