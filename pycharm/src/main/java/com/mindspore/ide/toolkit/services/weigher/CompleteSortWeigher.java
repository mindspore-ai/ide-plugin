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

package com.mindspore.ide.toolkit.services.weigher;

import com.mindspore.ide.toolkit.services.complete.MindSporeLookupElement;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementWeigher;

import org.jetbrains.annotations.NotNull;

/**
 * 自定义补全排序比较器
 *
 * @since 2022-05-28
 */
public class CompleteSortWeigher extends LookupElementWeigher {
    public CompleteSortWeigher() {
        super("CompleteSortWeigher", false, false);
    }

    @Override
    public Integer weigh(@NotNull LookupElement element) {
        if (!(element.getObject() instanceof MindSporeLookupElement)) {
            return Integer.MAX_VALUE;
        }

        MindSporeLookupElement msLookupElement = (MindSporeLookupElement) element.getObject();
        if (msLookupElement.getDataType() == MindSporeLookupElement.DataType.BOTH_IN_MINDSPORE_AND_JETBRAINS) {
            // ai和底座共同的推荐结果排首位
            return 1;
        } else {
            // ai单独推荐出来的结果，排中间
            return 2;
        }
    }
}
