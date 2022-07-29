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

import com.intellij.util.ui.UIUtil;
import com.mindspore.ide.toolkit.hdc.HdcRegularUtils;

import javax.swing.JTextPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.Component;
import java.awt.Color;
import java.awt.Font;

/**
 * TableViewRenderer
 *
 * @since 2022-04-18
 */
public class TableViewRenderer extends JTextPane implements TableCellRenderer {
    /**
     * 链接颜色
     */
    public static final Color HTML_COLOR = new Color(40, 107, 221);

    @Override
    public Component getTableCellRendererComponent(JTable jtable, Object obj, boolean isSelected,
            boolean hasFocus, int row, int column) {
        setFont(new Font("Default", 0, 16));
        if (obj != null) {
            setText(obj.toString().trim());
            String strHttp = HdcRegularUtils.filterSpecialStr(HdcRegularUtils.REGEX_HTTP, obj.toString());
            if (!strHttp.isEmpty()) {
                SimpleAttributeSet set = new SimpleAttributeSet();
                StyleConstants.setUnderline(set, true);
                StyleConstants.setForeground(set, HTML_COLOR);
                getStyledDocument().setCharacterAttributes(0, getText().length(), set, false);
            } else {
                if (UIUtil.isUnderDarcula()) {
                    setForeground(Color.WHITE);
                } else {
                    setForeground(Color.BLACK);
                }
            }
        } else {
            setText("");
        }
        return this;
    }
}