package com.mindspore.ide.toolkit.smartcomplete;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.mindspore.ide.toolkit.common.events.SmartCompleteEvents;
import icons.MsIcons;
import com.mindspore.ide.toolkit.protomessage.CompleteReply;
import com.mindspore.ide.toolkit.protomessage.ResultEntries;
import com.mindspore.ide.toolkit.protomessage.ResultEntry;
import com.mindspore.ide.toolkit.smartcomplete.grpc.CompletionException;
import com.mindspore.ide.toolkit.smartcomplete.grpc.SCAIPublicUtils;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public enum CompleteService {
    COMPLETE;

    private final Icon icon = MsIcons.MS_ICON_16PX;

    public void onPredicting(SmartCompleteEvents.CodeRecommendStart codeRecommendStart) {
        String formattedContent = getContentBeforeCursor(codeRecommendStart);
        log.info("formattedContent:{}", formattedContent);
        List<String> backMsgList = getBackMsgFormModelToGrpc(formattedContent, codeRecommendStart.getPrefix());
        List<LookupElement> lookupElements = getListLookUpElement(backMsgList);
        codeRecommendStart.getPredictResult().complete(lookupElements);
    }

    private String getContentBeforeCursor(SmartCompleteEvents.CodeRecommendStart codeRecommendStart) {
        if (codeRecommendStart.getDoc() == null) {
            return "";
        }
        final int maxContentLength = 500;
        int endIndex = codeRecommendStart.getMyOffset();
        int beginIndex = Integer.max(0, endIndex - maxContentLength);
        return codeRecommendStart.getDoc().getText(new TextRange(beginIndex, endIndex));
    }

    private List<String> getBackMsgFormModelToGrpc(String content, String prefix) {
        List<String> predictList = new ArrayList<>();
        try {
            Optional<CompleteReply> completeReply = SCAIPublicUtils.predictNextToken(content, prefix);
            if (completeReply.isPresent()) {
                ResultEntries resultEntries = completeReply.get().getResults();
                for (int index = 0; index < resultEntries.getResultEntryCount(); index++) {
                    ResultEntry entry = resultEntries.getResultEntry(index);
                    predictList.add(entry.getNewPrefix().trim());
                    log.info("{}:{}", entry.getNewPrefix().trim(), entry.getDetails());
                }
            }
        } catch (CompletionException completionException) {
            log.info(completionException.getMessage());
        }
        return predictList;
    }

    private List<LookupElement> getListLookUpElement(List<String> predictList) {
        int maxResult = 5;
        List<LookupElement> lookupElements = new ArrayList<>(maxResult);
        for (String code : predictList) {
            LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(code)
                    .withIcon(icon).withBoldness(true);
            lookupElements.add(lookupElementBuilder);
        }
        return lookupElements;
    }
}