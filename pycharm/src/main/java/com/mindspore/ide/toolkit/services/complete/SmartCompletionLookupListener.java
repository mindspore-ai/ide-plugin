package com.mindspore.ide.toolkit.services.complete;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupEvent;
import com.intellij.codeInsight.lookup.LookupListener;
import com.mindspore.ide.toolkit.common.events.EventCenter;
import com.mindspore.ide.toolkit.common.events.SmartCompleteEvents;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * SmartCompletionLookupListener
 *
 * @since 2022-1-19
 */
@Slf4j
@Data
public class SmartCompletionLookupListener implements LookupListener {
    private volatile boolean isMindspore;

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
        if (isMindspore && event.getItem() != null && event.getItem() instanceof LookupElementBuilder) {
            EventCenter.INSTANCE.publish(new SmartCompleteEvents.CodeCompleteEnd());
        }
    }
}