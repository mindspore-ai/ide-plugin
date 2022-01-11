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

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * operator search
 *
 * @since 1.0
 */
public enum OperatorSearch {
    INSTANCE;

    private Map<String, String> pytorch2ms;
    private Map<String, String> tf2ms;
    private Map<String, String> msLinks;
    private Map<String, String> nodeMap;
    private TrieNode root = new TrieNode();

    OperatorSearch() {
        MdFile2Map mdFile2Map = new MdFile2Map();
        String pytorchString = MdPathString.PYTORCH_MD_STR;
        String tfString = MdPathString.TENSORFLOW_MD_STR;
        List<String> strings = new ArrayList<>();
        strings.add(pytorchString);
        strings.add(tfString);
        pytorch2ms = mdFile2Map.mdStringList(pytorchString, new HashMap<>());
        tf2ms = mdFile2Map.mdStringList(tfString, new HashMap<>());
        msLinks = mdFile2Map.md2MapList(strings);

        nodeMap = new HashMap<>();

        pytorch2ms.entrySet().forEach(entry -> suffixSearch(entry.getKey(), nodeMap));
        tf2ms.entrySet().forEach(entry -> suffixSearch(entry.getKey(), nodeMap));
        msLinks.entrySet().forEach(entry -> suffixSearch(entry.getKey(), nodeMap));

        nodeMap.keySet().forEach(root::addWord);

        msLinks.keySet().forEach(root::addWord);
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
        if (inputString == null) {
            return new HashMap<>();
        }
        ArrayList<String> topOperators = root.search(inputString, count);
        Map<String, String> result = new LinkedHashMap<>();
        for (String operatorAlias : topOperators) {
            String operator = nodeMap.get(operatorAlias);
            String target = pytorch2ms.get(operator);
            if (target == null) {
                target = tf2ms.get(operator);
            }
            String link;
            if (target != null) {
                link = msLinks.get(target);
                target = operator + " -> " + target;
            } else {
                link = msLinks.get(operator);
                target = operator;
            }
            result.put(target, link);
        }
        return result;
    }

    private static void suffixSearch(String key, Map map) {
        String[] keyNode = key.toLowerCase(Locale.ENGLISH).split("\\.");
        StringBuffer sb = new StringBuffer();
        for (int i = keyNode.length - 1; i >= 0; i--) {
            sb.insert(0, keyNode[i]);
            map.put(sb.toString(), key);
            sb.insert(0, ".");
        }
    }
}