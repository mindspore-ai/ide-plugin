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

package com.mindspore.ide.toolkit.common.events;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Document;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SmartCompleteEvents {
    public static class CodeRecommendStart {
        private Document doc;
        private int myOffset;
        private String prefix;
        private CompletableFuture<List<LookupElement>> predictResult = new CompletableFuture<>();

        public Document getDoc() {
            return doc;
        }

        public void setDoc(Document doc) {
            this.doc = doc;
        }

        public int getMyOffset() {
            return myOffset;
        }

        public void setMyOffset(int myOffset) {
            this.myOffset = myOffset;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public CompletableFuture<List<LookupElement>> getPredictResult() {
            return predictResult;
        }

        public void setPredictResult(CompletableFuture<List<LookupElement>> predictResult) {
            this.predictResult = predictResult;
        }
    }

    public static class CodeRecommendEnd {
    }

    public static class CodeCompleteStart {
    }

    public static class CodeCompleteEnd {
    }

    public static class CodeCompleteFailed extends FurionOptEvent {
        private Exception exception;

        public Exception getException() {
            return exception;
        }

        public void setException(Exception exception) {
            this.exception = exception;
        }
    }

    public static class DownloadCompleteModelStart {
    }

    public static class DownloadCompleteModelEnd {
    }
}