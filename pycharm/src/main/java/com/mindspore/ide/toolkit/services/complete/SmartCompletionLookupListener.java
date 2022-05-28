package com.mindspore.ide.toolkit.services.complete;

import com.google.common.collect.Lists;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.lookup.LookupEvent;
import com.intellij.codeInsight.lookup.LookupListener;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;

import com.mindspore.ide.toolkit.common.events.EventCenter;
import com.mindspore.ide.toolkit.common.events.SmartCompleteEvents;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * SmartCompletionLookupListener
 *
 * @since 2022-1-19
 */
@Slf4j
@Data
public class SmartCompletionLookupListener implements LookupListener {
    private static final Set<Character> SUFFIX_CHAR_SET = new HashSet<>();

    static {
        SUFFIX_CHAR_SET.addAll(Lists.newArrayList('<', '>',
                '[', ']',
                '(', ')',
                '{', '}',
                '"', '\'',
                ',',
                '+',
                ':',
                '='
        ));
    }

    private volatile boolean isMindspore;

    private volatile String prefix;

    @Override
    public void currentItemChanged(@NotNull LookupEvent event) {
    }

    @Override
    public void lookupCanceled(@NotNull LookupEvent event) {
    }

    @Override
    public void itemSelected(@NotNull LookupEvent event) {
        if (isMindspore) {
            EventCenter.INSTANCE.publish(new SmartCompleteEvents.CodeCompleteStart());
        }
        if (event.isCanceledExplicitly()) {
            log.info("Lookup event is canceled");
            return;
        }
        if (isMindspore && event.getItem() != null && event.getItem() instanceof MindSporeLookupElement) {
            EventCenter.INSTANCE.publish(new SmartCompleteEvents.CodeCompleteEnd());
            handleSuffix(event);
            AutoPopupController
                .getInstance(event.getLookup().getProject())
                .scheduleAutoPopup(event.getLookup().getEditor());
        }
    }

    private void handleSuffix(LookupEvent event) {
        List<Character> suffixCharList = new ArrayList<>();
        String lookupString = Objects.requireNonNull(event.getItem()).getLookupString();
        if (prefix != null && lookupString.startsWith(prefix)) {
            lookupString = lookupString.replaceFirst(Pattern.quote(prefix), "");
        }
        for (char lookupChar : lookupString.toCharArray()) {
            if (SUFFIX_CHAR_SET.contains(lookupChar)) {
                suffixCharList.add(lookupChar);
            }
        }

        if (suffixCharList.size() > 0) {
            Document document = event.getLookup().getEditor().getDocument();
            int offset = event.getLookup().getEditor().getCaretModel().getOffset();
            int lineNumber = document.getLineNumber(offset);
            int lineEndOffset = document.getLineEndOffset(lineNumber);
            String documentText = document.getText(new TextRange(offset, lineEndOffset));
            int documentTextLen = documentText.length();

            documentText = removeSameSuffixInStr(suffixCharList, documentText);
            lookupString = removeSameSuffixInStr(suffixCharList, lookupString);

            int maxPublicLen = getMaxPublicPrefix(lookupString, documentText).length();
            int finalMatchLen = documentTextLen - documentText.length() + maxPublicLen;

            WriteCommandAction.runWriteCommandAction(event.getLookup().getEditor().getProject(), () -> {
                document.deleteString(offset, offset + finalMatchLen);
            });
        }
    }

    private String removeSameSuffixInStr(List<Character> suffixCharList, String targetStr) {
        String result = targetStr;
        for (int i = 0; i < suffixCharList.size(); i++) {
            Character character = suffixCharList.get(i);
            int index = targetStr.indexOf(character);
            if (index != -1) {
                result = targetStr.substring(index + 1);
            } else {
                break;
            }
        }
        return result;
    }

    private String getMaxPublicPrefix(String oneStr, String twoStr) {
        if (StringUtils.isBlank(oneStr) || StringUtils.isBlank(twoStr)) {
            return StringUtils.EMPTY;
        }

        StringBuilder res = new StringBuilder();
        int checkedLength = Math.min(oneStr.length(), twoStr.length());
        for (int i = 0; i < checkedLength; i++) {
            char oneStrChar = oneStr.charAt(i);
            char twoStrChar = twoStr.charAt(i);
            if (oneStrChar != twoStrChar) {
                break;
            }
            res.append(oneStrChar);
        }
        return res.toString();
    }
}