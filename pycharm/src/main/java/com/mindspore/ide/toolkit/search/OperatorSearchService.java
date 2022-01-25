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

package com.mindspore.ide.toolkit.search;

import com.mindspore.ide.toolkit.search.structure.TrieNode;

import java.util.List;
import java.util.Map;

/**
 * operator search
 *
 * @since 1.0
 */
public enum OperatorSearchService {
    INSTANCE;

    private TrieNode root = new TrieNode();
    private SearchEveryWhereDataHub<String> mdFile2Map = SearchEveryWhereDataHub.getOperatorDataHub();

    OperatorSearchService() {
        mdFile2Map.searchable().forEach(root::addWord);
    }

    /**
     * search
     *
     * @param inputString input
     * @return search content
     */
    public Map<String, String> search(String inputString) {
        return search(inputString, Integer.MAX_VALUE);
    }

    /**
     * search
     *
     * @param inputString input
     * @param count       number
     * @return search content
     */
    public Map<String, String> search(String inputString, int count) {
        List<String> topSearch = root.search(inputString, count);
        return mdFile2Map.assemble(topSearch);
    }
}