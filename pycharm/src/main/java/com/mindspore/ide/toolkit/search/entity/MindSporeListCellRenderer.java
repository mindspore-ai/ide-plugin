package com.mindspore.ide.toolkit.search.entity;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.LayeredIcon;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.speedSearch.SpeedSearchUtil;
import com.intellij.util.ui.EmptyIcon;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.accessibility.AccessibleContext;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.openapi.keymap.KeymapUtil.getActiveKeymapShortcuts;

public class MindSporeListCellRenderer extends DefaultListCellRenderer {
    private static final long serialVersionUID = 1L;

    private static final Icon EMPTY_ICON = EmptyIcon.ICON_18;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object obj, int index, boolean isSelected, boolean cellHasFocus) {
        SimpleColoredComponent nameComponent = new SimpleColoredComponent();
        JPanel panel = new JPanel(new BorderLayout()){
            @Override
            public AccessibleContext getAccessibleContext() {
                return nameComponent.getAccessibleContext();
            }
        };
        panel.setBorder(JBUI.Borders.empty(2));
        panel.setOpaque(true);
        Color color = UIUtil.getListBackground(isSelected,cellHasFocus);
        panel.setBackground(color);
        nameComponent.setBackground(color);
        panel.add(nameComponent,BorderLayout.CENTER);
        Color groupFg = isSelected? UIUtil.getListSelectionBackground(true) : UIUtil.getInactiveTextColor();
        if(obj instanceof DocumentSearch){
            Object value = ((DocumentSearch)obj).getValue();
            String pattern = ((DocumentSearch)obj).getPattern();
            if(value instanceof DocumentSearch){
                AnAction anAction = (AnAction) value;
                Presentation presentation = anAction.getTemplatePresentation();
                panel.add(createIconLabel(presentation.getIcon(),false),BorderLayout.WEST);
                panel.setToolTipText(presentation.getDescription());
                Shortcut[] shortcuts = getActiveKeymapShortcuts(ActionManager.getInstance().getId(anAction)).getShortcuts();
                String shortcutText = KeymapUtil.getPreferredShortcutText(shortcuts);
                String name = cutName(presentation.getText(),shortcutText,list,panel,nameComponent);
                Color defaultColor = defaultActionForeground(isSelected,cellHasFocus,presentation);
                appendWithColoredMatches(nameComponent,name,pattern,defaultColor,isSelected);
                if(StringUtil.isNotEmpty(shortcutText)){
                    nameComponent.append(" "+shortcutText,new SimpleTextAttributes(
                            SimpleTextAttributes.STYLE_SMALLER | SimpleTextAttributes.STYLE_BOLD,groupFg
                    ));
                }
            }else {
                panel.add(new JLabel(EMPTY_ICON),BorderLayout.CENTER);
                if(value instanceof DocumentValue){
                    String str = ((DocumentValue)value).getTitle();
                    nameComponent.append(str,
                            new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN,
                                    UIUtil.getListForeground()));
                }
            }
        }
        return panel;
    }

    private static Color defaultActionForeground(boolean isSelected,boolean hasFocus, @NotNull Presentation presentation){
        if(isSelected){
            return UIUtil.getListBackground(hasFocus);
        }
        if(presentation != null && !presentation.isEnabledAndVisible()){
            return UIUtil.getInactiveTextColor();
        }
        return UIUtil.getListForeground();
    }
    @NotNull
    private static JLabel createIconLabel(Icon icon,boolean disabled){
        LayeredIcon layeredIcon = new LayeredIcon(2);
        layeredIcon.setIcon(EMPTY_ICON,0);
        if(icon == null){
            return  new JLabel(layeredIcon);
        }
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();
        int emptyIconWidth = EMPTY_ICON.getIconWidth();
        int emptyIconHeight = EMPTY_ICON.getIconHeight();
        if(width <= emptyIconWidth && height <= emptyIconHeight){
            layeredIcon.setIcon(disabled && IconLoader.isGoodSize(icon) ? IconLoader.getDisabledIcon(icon) : icon,1,(emptyIconWidth - width)/2,(emptyIconHeight - height) /2);
        }
        return new JLabel(layeredIcon);
    }
    private static void appendWithColoredMatches(SimpleColoredComponent nameComponent, @NotNull String name,@NotNull String pattern,Color color,boolean selected){
        SimpleTextAttributes plain = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN,color);
        SimpleTextAttributes highlighted = new SimpleTextAttributes(null,color,null,SimpleTextAttributes.STYLE_SEARCH_MATCH);
        List<TextRange> fragments = new ArrayList<>();
        nameComponent.setDynamicSearchMatchHighlighting(false);
        if(selected){
            int matchStart = StringUtil.indexOfIgnoreCase(name,pattern,0);
            if(matchStart >= 0 ){
                nameComponent.setDynamicSearchMatchHighlighting(true);
                fragments.add(TextRange.from(matchStart,pattern.length()));
            }
        }
        SpeedSearchUtil.appendColoredFragments(nameComponent,name,fragments,plain,highlighted);
    }

    private static String cutName(String name,String shortcutText,JList<?> list,JPanel panel,SimpleColoredComponent nameComponent){
        if(!list.isShowing() || list.getWidth() <= 0 ){
            return StringUtil.first(name,60,true);
        }

        if(name.length() < 40){
            return  name;
        }
        int freeSpace = calcFreeSpace(list,panel,nameComponent,shortcutText);
        if(freeSpace <= 0){
            return name;
        }
        FontMetrics fontMetrics = nameComponent.getFontMetrics(nameComponent.getFont());
        int strWidth = fontMetrics.stringWidth(name);
        if(strWidth <= freeSpace){
            return name;
        }
        int nameLength = name.length();
        int cutSymbolIndex =(int)((((double)freeSpace - fontMetrics.stringWidth("..."))/strWidth)*nameLength);
        cutSymbolIndex = Integer.max(1,cutSymbolIndex);
        String resultName = name.substring(0,cutSymbolIndex);
        while (fontMetrics.stringWidth(resultName+ "...") > freeSpace && nameLength > 1){
            resultName = resultName.substring(0,nameLength -1);
        }
        return resultName.trim()+ "...";
    }

    private static int calcFreeSpace(JList<?> list ,JPanel panel,SimpleColoredComponent nameComponent,String shortcutText){
        BorderLayout layout;
        if(panel.getLayout() instanceof BorderLayout){
            layout = (BorderLayout) panel.getLayout();
        }else {
            layout = new BorderLayout();
        }
        Component eastComponent = layout.getLayoutComponent(BorderLayout.EAST);
        Component westComponent = layout.getLayoutComponent(BorderLayout.WEST);
        int freeSpace = list.getWidth() - (list.getInsets().right + list.getInsets().left)
                - (panel.getInsets().right + panel.getInsets().left)
                - (eastComponent == null ? 0 : eastComponent.getPreferredSize().width)
                - (westComponent == null ? 0 : westComponent.getPreferredSize().width)
                - (nameComponent.getInsets().right + nameComponent.getInsets().left)
                - (nameComponent.getIpad().right + nameComponent.getIpad().left)
                - nameComponent.getIconTextGap();
        if(StringUtil.isNotEmpty(shortcutText)){
            FontMetrics fontMetrics = nameComponent.getFontMetrics(nameComponent.getFont().deriveFont(Font.BOLD));
            freeSpace -= fontMetrics.stringWidth(" "+shortcutText);
        }
        return freeSpace;
    }
}
