package com.mindspore.ide.toolkit.services.complete;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementWeigher;
import com.intellij.codeInsight.lookup.LookupEx;
import com.intellij.codeInsight.lookup.LookupManager;
import com.mindspore.ide.toolkit.common.events.EventCenter;
import com.mindspore.ide.toolkit.common.events.SmartCompleteEvents;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public class SmartCompletion extends CompletionContributor {
    private static final long LONGEST_WAITING_TIME = 500;

    private final SmartCompletionLookupListener smartCompletionLookupListener = new SmartCompletionLookupListener();

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        super.fillCompletionVariants(parameters, result);
        result.restartCompletionOnAnyPrefixChange();
        registerLookupListener(parameters);
        SmartCompleteEvents.CodeRecommendStart codeCompleteStart = new SmartCompleteEvents.CodeRecommendStart();
        codeCompleteStart.setDoc(parameters.getEditor().getDocument());
        codeCompleteStart.setMyOffset(parameters.getOffset());
        codeCompleteStart.setPrefix(result.getPrefixMatcher().getPrefix());
        EventCenter.INSTANCE.publish(codeCompleteStart);
        List<LookupElement> smartCompleteResult;
        Long startTime = System.currentTimeMillis();
        try {
            smartCompleteResult = codeCompleteStart.getPredictResult().get(LONGEST_WAITING_TIME, TimeUnit.MILLISECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException exception) {
            Long endTime = System.currentTimeMillis();
            SmartCompleteEvents.CodeCompleteFailed completeException = new SmartCompleteEvents.CodeCompleteFailed();
            completeException.setException(exception);
            completeException.setDuration(endTime - startTime);
            EventCenter.INSTANCE.publish(completeException);
            log.info("failed to get smartcomplete result,", exception);
            return;
        }
        CompletionResultSet resultSort = restartElement(smartCompleteResult, parameters, result);
        if (smartCompleteResult != null && smartCompleteResult.size() > 0) {
            EventCenter.INSTANCE.publish(new SmartCompleteEvents.CodeRecommendEnd());
            resultSort.addAllElements(smartCompleteResult);
            smartCompletionLookupListener.setMindspore(true);
        }
    }

    private CompletionResultSet restartElement(List<LookupElement> smartCompleteResult, CompletionParameters parameters, CompletionResultSet result) {
        PrefixMatcher originalMatcher = result.getPrefixMatcher();
        LookupElementWeigher lookupElementWeigher = new LookupElementWeigher("SmartComplete", false, false) {
            @Override
            public Integer weigh(@NotNull LookupElement element) {
                if (smartCompleteResult.contains(element)) {
                    return smartCompleteResult.indexOf(element);
                }
                return Integer.MAX_VALUE;
            }
        };
        CompletionResultSet resultSort = result.withRelevanceSorter(CompletionSorter.defaultSorter(parameters, originalMatcher)
                .weighBefore("liftShorterClasses", lookupElementWeigher));
        resultSort.restartCompletionOnAnyPrefixChange();
        return result;
    }

    private void registerLookupListener(CompletionParameters parameters) {
        LookupEx lookupEx = Objects.requireNonNull(LookupManager.getInstance(Objects.requireNonNull(parameters.getEditor().getProject())).getActiveLookup());
        smartCompletionLookupListener.setMindspore(false);
        lookupEx.removeLookupListener(smartCompletionLookupListener);
        lookupEx.addLookupListener(smartCompletionLookupListener);
    }
}