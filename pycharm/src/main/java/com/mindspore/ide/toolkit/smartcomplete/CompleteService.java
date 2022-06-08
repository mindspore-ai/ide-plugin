package com.mindspore.ide.toolkit.smartcomplete;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;

import com.mindspore.ide.toolkit.common.events.SmartCompleteEvents;
import com.mindspore.ide.toolkit.protomessage.CompleteReply;
import com.mindspore.ide.toolkit.protomessage.ResultEntries;
import com.mindspore.ide.toolkit.protomessage.ResultEntry;
import com.mindspore.ide.toolkit.services.complete.MindSporeLookupElement;
import com.mindspore.ide.toolkit.smartcomplete.grpc.CompletionException;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * CompleteService
 *
 * @since 2022-1-19
 */
@Slf4j
public enum CompleteService {
    COMPLETE;

    /**
     * predict code
     *
     * @param codeRecommendStart code complete event
     */
    public void onPredicting(SmartCompleteEvents.CodeRecommendStart codeRecommendStart) {
        String formattedContent = getContentBeforeCursor(codeRecommendStart);
        log.info("Data send to model is: {}", formattedContent);
        Set<String> backMsgSet = getBackMsgFormModelToGrpc(formattedContent, codeRecommendStart.getPrefix());
        Set<LookupElement> lookupElements = getLookupElementSet(backMsgSet);
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

    private Set<String> getBackMsgFormModelToGrpc(String content, String prefix) {
        Set<String> predictSet = new LinkedHashSet<>();
        try {
            Optional<CompleteReply> completeReply = ModelManager.INSTANCE.communicateWithModel(content, prefix);
            if (completeReply.isPresent()) {
                String oldPrefix = completeReply.get().getOldPrefix();
                ResultEntries resultEntries = completeReply.get().getResults();
                log.info("Has data from model. Data count: {}", resultEntries.getResultEntryCount());
                for (int index = 0; index < resultEntries.getResultEntryCount(); index++) {
                    ResultEntry entry = resultEntries.getResultEntry(index);
                    log.info("  Data from model is: {}:{}", entry.getNewPrefix(), entry.getDetails());
                    String newPrefix = trimEnd(entry.getNewPrefix().toCharArray());
                    if (!Objects.equals(prefix, oldPrefix) && newPrefix.startsWith(oldPrefix)) {
                        newPrefix = newPrefix.replaceFirst(Pattern.quote(oldPrefix), prefix);
                    }
                    if (!Objects.equals(prefix, newPrefix) && !newPrefix.isBlank()) {
                        predictSet.add(newPrefix);
                        log.info("  Data presented to user is: {}:{}", newPrefix, entry.getDetails());
                    } else {
                        log.info("  Data is not displayed to user: {}:{}", entry.getNewPrefix(), entry.getDetails());
                    }
                }
            } else {
                log.info("No data from model.");
            }
        } catch (CompletionException completionException) {
            log.warn("Get message from completion model failed.", completionException);
        }
        return predictSet;
    }

    private Set<LookupElement> getLookupElementSet(Set<String> predictSet) {
        int maxResult = 5;
        Set<LookupElement> lookupElements = new LinkedHashSet<>(maxResult);
        for (String code : predictSet) {
            lookupElements.add(new MindSporeLookupElement(LookupElementBuilder
                    .create(code).withBoldness(true)));
        }
        return lookupElements;
    }

    private String trimEnd(char[] value) {
        int len = value.length;
        int startIndex = 0;
        while ((startIndex < len) && (value[len - 1] <= ' ')) {
            len--;
        }
        String finalValue = new String(value);
        return len < value.length ? finalValue.substring(startIndex, len) : finalValue;
    }
}