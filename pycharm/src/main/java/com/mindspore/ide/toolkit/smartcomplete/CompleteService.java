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
import lombok.extern.slf4j.Slf4j;

import javax.swing.Icon;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CompleteService
 *
 * @since 2022-1-19
 */
@Slf4j
public enum CompleteService {
    COMPLETE;

    private final Icon icon = MsIcons.MS_ICON_16PX;

    /**
     * predict code
     *
     * @param codeRecommendStart code complete event
     */
    public void onPredicting(SmartCompleteEvents.CodeRecommendStart codeRecommendStart) {
        String formattedContent = getContentBeforeCursor(codeRecommendStart);
        log.info("Data send to model is: {}", formattedContent);
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
            Optional<CompleteReply> completeReply = ModelManager.INSTANCE.communicateWithModel(content, prefix);
            if (completeReply.isPresent()) {
                ResultEntries resultEntries = completeReply.get().getResults();
                log.info("Has data from model. Data count: {}", resultEntries.getResultEntryCount());
                for (int index = 0; index < resultEntries.getResultEntryCount(); index++) {
                    ResultEntry entry = resultEntries.getResultEntry(index);
                    predictList.add(entry.getNewPrefix().trim());
                    log.info("  Data from model is: {}:{}", entry.getNewPrefix().trim(), entry.getDetails());
                }
            } else {
                log.info("No data from model.");
            }
        } catch (CompletionException completionException) {
            log.error("Get message from completion model failed.", completionException);
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