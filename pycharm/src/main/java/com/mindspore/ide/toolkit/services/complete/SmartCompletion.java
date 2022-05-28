package com.mindspore.ide.toolkit.services.complete;

import com.mindspore.ide.toolkit.common.events.EventCenter;
import com.mindspore.ide.toolkit.common.events.SmartCompleteEvents;
import com.mindspore.ide.toolkit.services.filter.FilterContext;
import com.mindspore.ide.toolkit.services.filter.FilterManager;
import com.mindspore.ide.toolkit.services.weigher.CompleteSortWeigher;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionService;
import com.intellij.codeInsight.completion.CompletionSorter;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupEx;
import com.intellij.codeInsight.lookup.LookupManager;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.SwingUtilities;

/**
 * SmartCompletion
 *
 * @since 2022-1-19
 */
@Slf4j
public class SmartCompletion extends CompletionContributor {
    private static final long LONGEST_WAITING_TIME = 500L;

    private final SmartCompletionLookupListener smartCompletionLookupListener = new SmartCompletionLookupListener();

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        super.fillCompletionVariants(parameters, result);
        CompletionSorter sorter = CompletionService.getCompletionService()
                .defaultSorter(parameters, result.getPrefixMatcher())
                .weighBefore("liftShorterClasses", new CompleteSortWeigher());
        CompletionResultSet resultNew = result.withRelevanceSorter(sorter);
        resultNew.restartCompletionOnAnyPrefixChange();

        registerLookupListener(parameters);
        SmartCompleteEvents.CodeRecommendStart codeCompleteStart = new SmartCompleteEvents.CodeRecommendStart();
        publishInitData(codeCompleteStart, parameters, resultNew);
        Set<LookupElement> myProcessResults = new CopyOnWriteArraySet<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        uiThreadData(parameters, resultNew, myProcessResults, countDownLatch);
        Set<LookupElement> smartCompleteResult;
        Long startTime = System.currentTimeMillis();
        FilterContext filterContext;
        try {
            smartCompleteResult = codeCompleteStart.getPredictResult().get(LONGEST_WAITING_TIME, TimeUnit.MILLISECONDS);
            if (CollectionUtils.isNotEmpty(smartCompleteResult)) {
                EventCenter.INSTANCE.publish(new SmartCompleteEvents.CodeRecommendEnd());
                smartCompletionLookupListener.setMindspore(true);
                smartCompletionLookupListener.setPrefix(resultNew.getPrefixMatcher().getPrefix());
            }
            countDownLatchAwait(countDownLatch);
            filterContext = new FilterContext(parameters,
                codeCompleteStart,
                resultNew,
                smartCompleteResult,
                myProcessResults);
            FilterManager.INSTANCE.doFilter(filterContext);
        } catch (TimeoutException | InterruptedException | ExecutionException exception) {
            Long endTime = System.currentTimeMillis();
            countDownLatchAwait(countDownLatch);
            resultNew.addAllElements(myProcessResults);
            SmartCompleteEvents.CodeCompleteFailed completeException = new SmartCompleteEvents.CodeCompleteFailed();
            completeException.setException(exception);
            completeException.setDuration(endTime - startTime);
            EventCenter.INSTANCE.publish(completeException);
            log.info("failed to get smartcomplete result,", exception);
            return;
        }
        resultNew.addAllElements(filterContext.getElementSet());
    }

    private void registerLookupListener(CompletionParameters parameters) {
        LookupEx lookupEx = Objects.requireNonNull(LookupManager
            .getInstance(Objects.requireNonNull(parameters.getEditor().getProject())).getActiveLookup());
        smartCompletionLookupListener.setMindspore(false);
        lookupEx.removeLookupListener(smartCompletionLookupListener);
        lookupEx.addLookupListener(smartCompletionLookupListener);
    }

    private void publishInitData(SmartCompleteEvents.CodeRecommendStart codeCompleteStart,
        @NotNull CompletionParameters parameters,
        @NotNull CompletionResultSet result) {
        codeCompleteStart.setDoc(parameters.getEditor().getDocument());
        codeCompleteStart.setMyOffset(parameters.getOffset());
        codeCompleteStart.setPrefix(result.getPrefixMatcher().getPrefix());
        EventCenter.INSTANCE.publish(codeCompleteStart);
    }

    private void uiThreadData(@NotNull CompletionParameters parameters,
        @NotNull CompletionResultSet result,
        Set<LookupElement> myProcessResults,
        CountDownLatch countDownLatch) {
        SwingUtilities.invokeLater(() -> {
            result.runRemainingContributors(parameters,
                completionResult -> myProcessResults.add(completionResult.getLookupElement()));

            countDownLatch.countDown();
        });
    }

    private void countDownLatchAwait(CountDownLatch countDownLatch) {
        try {
            boolean isCountDownLatch = countDownLatch.await(LONGEST_WAITING_TIME, TimeUnit.MILLISECONDS);
            if (!isCountDownLatch) {
                log.warn("countDownLatchAwait failed");
            }
        } catch (InterruptedException interruptedException) {
            log.warn("countDownLatchAwait failed", interruptedException);
        }
    }
}