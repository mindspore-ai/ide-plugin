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

import com.mindspore.ide.toolkit.services.complete.MindSporeLookupElement;

import com.intellij.codeInsight.lookup.LookupElement;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 过滤底座中和白泽相同的结果
 *
 * @since 2022-05-28
 */
public class RepeatFilter extends AbstractFilter {
    private static final int REPEAT_FILTER_ORDER = 10;

    @Override
    public int getOrder() {
        return REPEAT_FILTER_ORDER;
    }

    @Override
    protected void doFilter(FilterContext filterContext) {
        // 白泽推荐出来的结果
        Set<LookupElement> smartCompleteResult = filterContext.getSmartResults();
        // 白泽和底座相同的结果
        Set<LookupElement> sameCompleteResult = new LinkedHashSet<>();
        // 底座推荐出来的结果
        Set<LookupElement> myProcessResults = filterContext.getMyProcessResults();

        if (smartCompleteResult.isEmpty()) {
            return;
        }

        smartCompleteResult.removeIf(smartResult -> compareAndExtractResult(myProcessResults,
                sameCompleteResult, smartResult));

        List<String> sameLookupElementStrList = sameCompleteResult.stream()
                .map(sameElement -> sameElement.getLookupString().trim())
                .collect(Collectors.toList());
        myProcessResults.removeIf(myElement -> sameLookupElementStrList.contains(myElement.getLookupString().trim()));

        filterContext.getElementSet().addAll(sameCompleteResult);
    }

    private boolean compareAndExtractResult(Collection<LookupElement> myProcessResults,
                                            Collection<LookupElement> sameCompleteResult,
                                            LookupElement smartResult) {
        // ai返回的结果是否包含底座返回的该结果
        boolean isContain = myProcessResults.stream()
                .anyMatch(myProcessResult -> myProcessResult.getLookupString().trim()
                        .equals(smartResult.getLookupString().trim()));
        // 如果包含，则放入相同结果集；否则，放入底座结果集
        if (isContain) {
            MindSporeLookupElement msLookupElement = new MindSporeLookupElement(smartResult,
                    MindSporeLookupElement.DataType.BOTH_IN_MINDSPORE_AND_JETBRAINS);
            sameCompleteResult.add(msLookupElement);
        }
        return isContain;
    }
}
