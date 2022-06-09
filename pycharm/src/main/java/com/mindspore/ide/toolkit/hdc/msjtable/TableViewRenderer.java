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

import javax.swing.JTextArea;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * TableViewRenderer
 *
 * @since 2022-04-18
 */
public class TableViewRenderer extends JTextArea implements TableCellRenderer {
    private List<Integer> ints = new ArrayList<>();

    /**
     * construction method
     *
     * @param ints ints
     */
    public TableViewRenderer(List<Integer> ints) {
        // 将表格设为自动换行,利用JTextArea的自动换行方法
        setLineWrap(true);
        this.ints = ints;
    }

    @Override
    public Component getTableCellRendererComponent(JTable jtable, Object obj, boolean isSelected,
            boolean hasFocus, int row, int column) {
        // 利用JTextArea的setText设置文本方法
        setText(obj == null ? "" : obj.toString());
        for (Integer anInt : ints) {
            if (row == anInt && column == 1) {
                setForeground(Color.green);
            }
        }
        return this;
    }
}