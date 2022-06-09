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

import java.awt.Point;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

/**
 * DefaultGridBagTableModel
 *
 * @since 2022-04-18
 */
public class DefaultGridBagTableModel implements GridBagModel, TableModelListener {
    private AbstractTableModel model;
    private List<List<Point>> gridInfo;

    DefaultGridBagTableModel(AbstractTableModel model) {
        gridInfo = new Vector<List<Point>>();
        setTableModel(model);
    }

    /**
     * set TableModel
     *
     * @param model model
     */
    public void setTableModel(AbstractTableModel model) {
        if (model != null && model != this.model) {
            if (this.model != null) {
                this.model.removeTableModelListener(this);
            }
            // 防止多次添加监听器
            model.removeTableModelListener(this);
            model.addTableModelListener(this);
            this.model = model;
            clearMergence();
        }
    }

    /**
     * clear Mergence
     */
    public void clearMergence() {
        if (gridInfo == null) {
            gridInfo = new Vector<List<Point>>();
        } else {
            gridInfo.clear();
        }
        if (model == null) {
            return;
        }
        // 初始化，每个格子占的格子数为(1,1);
        for (int row = model.getRowCount(); --row >= 0;) {
            List<Point> infos = new Vector<Point>();
            gridInfo.add(infos);
            for (int col = model.getColumnCount(); --col >= 0;) {
                infos.add(getDefaultPoint());
            }
        }
    }

    /**
     * get DefaultPoint
     *
     * @return Point
     */
    public Point getDefaultPoint() {
        return new Point(1, 1);
    }

    @Override
    public boolean canMergeCells(int[] rows, int[] columns) {
        if (rows == null || columns == null) {
            return false;
        }
        Arrays.sort(rows);
        for (int index = 0; index < rows.length - 1; index++) {
            if (rows[index + 1] - rows[index] > 1) {
                return false;
            }
        }
        Arrays.sort(columns);
        for (int index = 0; index < columns.length - 1; index++) {
            if (columns[index + 1] - columns[index] > 1) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getCellState(int row, int column) {
        Point grid = getGrid(row, column);
        if (grid == null) {
            return DEFAULT;
        }
        if (grid.x > 1 || grid.y > 1) {
            return MERGE;
        }
        if (grid.x <= 0 || grid.y <= 0) {
            return COVERED;
        }
        return DEFAULT;
    }

    @Override
    public int getColumnGrid(int row, int column) {
        if (gridInfo != null && row >= 0 && row < gridInfo.size()) {
            List<Point> gridRow = gridInfo.get(row);
            if (gridRow != null && column >= 0 && column < gridRow.size()) {
                Point point = gridRow.get(column);
                if (point != null) {
                    return point.x;
                }
            }
        }
        return 1;
    }

    @Override
    public Point getGrid(int row, int column) {
        if (gridInfo != null && row >= 0 && row < gridInfo.size()) {
            List<Point> gridRow = gridInfo.get(row);
            if (gridRow != null && column >= 0 && column < gridRow.size()) {
                return gridRow.get(column);
            }
        }
        return getDefaultPoint();
    }

    @Override
    public int getRowGrid(int row, int column) {
        if (gridInfo != null && row >= 0 && row < gridInfo.size()) {
            List<Point> gridRow = gridInfo.get(row);
            if (gridRow != null && column >= 0 && column < gridRow.size()) {
                Point point = gridRow.get(column);
                if (point != null) {
                    return point.y;
                }
            }
        }
        return 1;
    }

    private boolean setGrid(int row, int column, Point grid) {
        if (gridInfo != null && row >= 0 && row < gridInfo.size()) {
            List<Point> gridRow = gridInfo.get(row);
            if (gridRow != null && column >= 0 && column < gridRow.size()) {
                Point point = gridRow.get(column);
                if (point != null) {
                    point.setLocation(grid);
                } else {
                    gridRow.set(column, grid.getLocation());
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean spliteCellAt(int row, int column) {
        if (gridInfo != null && row >= 0 && row < gridInfo.size()) {
            List<Point> gridRow = gridInfo.get(row);
            if (gridRow != null && column >= 0 && column < gridRow.size()) {
                Point point = gridRow.get(column);
                forData(point, row, column, gridRow);
                return true;
            }
        }
        return false;
    }

    private void forData(Point inputPo, int row, int column, List<Point> gridRow) {
        Point inputPoNew = inputPo;
        if (inputPoNew != null) {
            inputPoNew = inputPo.getLocation();
            for (int a = 0; a < inputPoNew.y; a++) {
                for (int b = 0; b < inputPoNew.x; b++) {
                    setGrid(row + a, column + b, getDefaultPoint());
                }
            }
        } else {
            gridRow.set(column, getDefaultPoint());
        }
    }

    @Override
    /**
     * table中发生行的添加和删除的时候需要修改该模型
     */
    public void tableChanged(TableModelEvent tableModelEvent) {
    }

    @Override
    public boolean mergeCells(int[] rows, int[] columns) {
        if (!canMergeCells(rows, columns)) {
            return false;
        }
        Arrays.sort(rows);
        Arrays.sort(columns);
        return mergeCells(rows[0], rows[rows.length - 1], columns[0], columns[columns.length - 1]);
    }

    @Override
    public boolean mergeCells(int startRow, int endRow, int startColumn, int endColumn) {
        setGrid(startRow, startColumn, new Point(endColumn - startColumn + 1, endRow - startRow + 1));
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startColumn; col <= endColumn; col++) {
                if (row == startRow && col == startColumn) {
                    continue;
                } else {
                    setGrid(row, col, new Point(COVERED, COVERED));
                }
            }
        }
        return true;
    }

    /**
     * toString
     *
     * @return String
     */
    public String toString() {
        if (gridInfo == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (List<Point> rowInfo : gridInfo) {
            for (Point grid : rowInfo) {
                sb.append("[" + grid.x + "," + grid.y + "], ");
            }
            sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }
}