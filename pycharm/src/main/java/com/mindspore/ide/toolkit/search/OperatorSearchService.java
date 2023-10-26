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

import com.intellij.notification.NotificationType;
import com.mindspore.ide.toolkit.common.utils.NotificationUtils;
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
    private final Map<String, SearchEveryWhereDataHub<String, OperatorRecord>> mdFile2Map = new ConcurrentHashMap<>();
    private SearchEveryWhereDataHub<String, OperatorRecord> recentHub;

    OperatorSearchService() {
    }

    public boolean changeSearchDataHub(String version) {
        return changeSearchDataHub(version, false);
    }

    public boolean changeSearchDataHub(String version, boolean isInit) {
        MsVersionDataConfig.MsVersionData msVersionData = MsVersionDataConfig.newVersionData(version);
        SearchEveryWhereDataHub<String, OperatorRecord> tempHub;
        if (mdFile2Map.containsKey(msVersionData.getMdVersion())) {
            tempHub = mdFile2Map.get(msVersionData.getMdVersion());
        } else {
            MdDataGet mdDataGet = new MdDataGet(msVersionData);
            if (!isInit && mdDataGet.pytorchMdStr.isEmpty()) {
                NotificationUtils.notify(NotificationUtils.NotifyGroup.SEARCH,
                        NotificationType.ERROR,
                        "Failed to retrieve API mapping data of MindSpore " + version +
                                ". Please check if network is proper or version is right. MindSpore master option is " +
                                "available as newest version.");
                return false;
            }
            tempHub = new OperatorMapDataHub(mdDataGet);
        }

        this.recentHub = tempHub;
        root = new TrieNode();
        recentHub.searchable().forEach(root::addWord);
        mdFile2Map.putIfAbsent(msVersionData.getMdVersion(), tempHub);
        return true;
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

    public boolean isInit() {
        if (recentHub == null) {
            return false;
        }
        return true;
    }
}