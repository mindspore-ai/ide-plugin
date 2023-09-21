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

import com.mindspore.ide.toolkit.search.entity.OperatorRecord;
import com.mindspore.ide.toolkit.search.structure.TrieNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * operator search
 *
 * @since 1.0
 */
public enum OperatorSearchService {
    INSTANCE;

    private TrieNode root = new TrieNode();
    private Map<String, SearchEveryWhereDataHub<String, OperatorRecord>> mdFile2Map = new ConcurrentHashMap<>();
    private SearchEveryWhereDataHub<String, OperatorRecord> recentHub;

    OperatorSearchService() {
    }

    public boolean changeSearchDataHub(String version) {
        MsVersionDataConfig.MsVersionData msVersionData = MsVersionDataConfig.newVersionData(version);
        SearchEveryWhereDataHub<String, OperatorRecord> tempHub =
                mdFile2Map.getOrDefault(msVersionData.getMdVersion(), new OperatorMapDataHub(msVersionData));
        if (tempHub != null) {
            this.recentHub = tempHub;
            root = new TrieNode();
            recentHub.searchable().forEach(root::addWord);
            return true;
        } else {
            return false;
        }
    }

    /**
     * search
     *
     * @param inputString input
     * @return search content
     */
    public List<OperatorRecord> search(String inputString) {
        return search(inputString, Integer.MAX_VALUE);
    }

    /**
     * search
     *
     * @param inputString input
     * @param count       number
     * @return search content
     */
    public List<OperatorRecord> search(String inputString, int count) {
        if (inputString == null || inputString.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> topSearch = root.search(inputString, count);
        return recentHub.assemble(topSearch, inputString, count);
    }

    /**
     * search
     *
     * @param inputString input
     * return search content
     */
    public List<OperatorRecord> searchFullMatch(String inputString) {
        return recentHub.fetchAllMatch(inputString);
    }
}