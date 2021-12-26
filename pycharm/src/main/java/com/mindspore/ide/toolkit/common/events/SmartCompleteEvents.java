package com.mindspore.ide.toolkit.common.events;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Document;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SmartCompleteEvents {
    @Data
    public static class CodeRecommendStart {
        private Document doc;

        private int myOffset;

        private String prefix;

        private CompletableFuture<List<LookupElement>> predictResult = new CompletableFuture<>();
    }

    public static class CodeRecommendEnd {
    }

    public static class CodeCompleteStart {
    }

    public static class CodeCompleteEnd {
    }

    @Getter
    @Setter
    public static class CodeCompleteFailed extends FurionOptEvent {
        private Exception exception;
    }

    public static class DownloadCompleteModelStart {
    }

    public static class DownloadCompleteModelEnd {
    }
}