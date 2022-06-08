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

package com.mindspore.ide.toolkit.services.filter;

import com.mindspore.ide.toolkit.common.events.SmartCompleteEvents;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 过滤器所需要的上下文
 *
 * @since 2022-05-28
 */
public class FilterContext {
    /**
     * 底座过程值，最终需要加入底座结果
     */
    public final Set<LookupElement> myProcessResults;

    private final CompletionParameters parameters;

    private final SmartCompleteEvents.CodeRecommendStart codeRecommendStart;

    /**
     * 底座结果
     */
    private final CompletionResultSet result;

    /**
     * 白泽智能推荐结果
     */
    private final Set<LookupElement> smartResults;

    private final Set<LookupElement> elementSet = new LinkedHashSet<>();

    public FilterContext(@NotNull CompletionParameters parameters,
        @NotNull SmartCompleteEvents.CodeRecommendStart codeRecommendStart,
        @NotNull CompletionResultSet result,
        @NotNull Set<LookupElement> smartResults,
        @NotNull Set<LookupElement> myProcessResults) {
        this.parameters = parameters;
        this.codeRecommendStart = codeRecommendStart;
        this.result = result;
        this.smartResults = smartResults;
        this.myProcessResults = myProcessResults;
    }

    /**
     * 获取底座中的{@link LookupElement}
     *
     * @return 返回 {@link CompletionResultSet} 封装的底座推荐的 {@link LookupElement} 集合
     */
    public final Set<LookupElement> getResultLookupElements() {
        final Set<LookupElement> results = new LinkedHashSet<>();
        result.runRemainingContributors(parameters,
                completionResult -> results.add(completionResult.getLookupElement()));
        return results;
    }

    public Set<LookupElement> getMyProcessResults() {
        return myProcessResults;
    }

    public CompletionParameters getParameters() {
        return parameters;
    }

    public SmartCompleteEvents.CodeRecommendStart getCodeRecommendStart() {
        return codeRecommendStart;
    }

    public CompletionResultSet getResult() {
        return result;
    }

    public Set<LookupElement> getSmartResults() {
        return smartResults;
    }

    public Set<LookupElement> getElementSet() {
        return elementSet;
    }
}
