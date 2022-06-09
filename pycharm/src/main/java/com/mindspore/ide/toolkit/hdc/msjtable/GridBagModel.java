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

/**
 * GridBagModel
 *
 * @since 2022-04-18
 */
public interface GridBagModel {
    /**
     * 格子处于正常状态
     */
    int DEFAULT = 0;

    /**
     * 格子合并了其他的格子
     */
    int MERGE = 1;

    /**
     * 格子被其他格子合并
     */
    int COVERED = -1;

    /**
     * get Grid
     *
     * @param row    行
     * @param column 列
     * @return 该单元格在行、列的跨度
     */
    Point getGrid(int row, int column);

    /**
     * 在Y轴方向的跨度
     *
     * @param row    row
     * @param column column
     * @return 跨度
     */
    int getRowGrid(int row, int column);

    /**
     * 在X轴方向的跨度
     *
     * @param row    row
     * @param column column
     * @return 跨度
     */
    int getColumnGrid(int row, int column);

    /**
     * can Merge Cells
     *
     * @param rows    行集合
     * @param columns 列集合
     * @return 单元格集合是否可以合并在一起
     */
    boolean canMergeCells(int[] rows, int[] columns);

    /**
     * 判断该单元格状态
     *
     * @param row    row
     * @param column column
     * @return MERGE|DEFAULT|COVERED
     */
    int getCellState(int row, int column);

    /**
     * 将单元格集合合并
     *
     * @param startRow    开始行
     * @param endRow      结束行
     * @param startColumn 开始列
     * @param endColumn   结束列
     * @return 是否合并成功
     */
    boolean mergeCells(int startRow, int endRow, int startColumn, int endColumn);

    /**
     * 将单元格集合合并
     *
     * @param rows    行集合
     * @param columns 列集合
     * @return 是否合并成功
     */
    boolean mergeCells(int[] rows, int[] columns);

    /**
     * 拆分单元格
     *
     * @param row    行
     * @param column 列
     * @return 是否拆分成功
     */
    boolean spliteCellAt(int row, int column);

    /**
     * 清除 所有合并
     */
    void clearMergence();
}