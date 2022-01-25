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

import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.ast.SoftLineBreak;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * md file to map
 *
 * @since 1.0
 */
public enum OperatorMapDataHub implements SearchEveryWhereDataHub<String> {
    INSTANCE;

    private Map<String, String> linkMap = new HashMap<>();
    private Map<String, String> operatorMap = new HashMap<>();
    private Map<String, String> nodeMap = new HashMap<>();

    OperatorMapDataHub() {
        mdStringList(MdPathString.PYTORCH_MD_STR);
        mdStringList(MdPathString.TENSORFLOW_MD_STR);
        operatorMap.entrySet().forEach(entry -> suffixStringSplit(entry.getKey()));
        linkMap.entrySet().forEach(entry -> suffixStringSplit(entry.getKey()));
    }

    @Override
    public Map<String, String> assemble(List<String> topOperators, String input) {
        Map<String, String> result = new LinkedHashMap<>();
        for (String operatorAlias : topOperators) {
            String operator = nodeMap.get(operatorAlias);
            if (!operator.contains(input)) {
                continue;
            }
            if (operatorMap.containsKey(operator)) {
                String target = operatorMap.get(operator);
                result.put(operator + " -> " + target, linkMap.get(target));
            } else {
                result.put(operator, linkMap.get(operator));
            }
        }
        return result;
    }

    @Override
    public Set<String> searchable() {
        return nodeMap.keySet();
    }

    /**
     * 处理md数据，获取api对应api和api对应url
     *
     * @param mdString md数据
     * @return api对应api
     */
    private void mdStringList(String mdString) {
        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        Node document = parser.parse(mdString);
        document.getChildIterator().forEachRemaining(paragraphNode -> {
            if (paragraphNode instanceof Paragraph) {
                LinkedHashMap<String, String> mapList = new LinkedHashMap<>();
                paragraphNode.getChildIterator().forEachRemaining(innerNode -> {
                    if (innerNode instanceof Link) {
                        Link link = (Link) innerNode;
                        String text = link.getText().toString();
                        String url = link.getUrl().toString();
                        mapList.put(text, url);
                    } else if (innerNode instanceof SoftLineBreak) {
                        // 换行处理缓存数据
                        setMapData(mapList);
                    } else {
                        innerNode.getNodeName();
                    }
                });
                // 防止一条数据没有换行直接进入下一个循环导致数据丢失
                setMapData(mapList);
            }
        });
    }

    /**
     * 处理缓存数据
     *
     * @param mapList 缓存数据
     */
    private void setMapData(LinkedHashMap<String, String> mapList) {
        if (mapList.size() <= 0) {
            return;
        }
        Map.Entry<String, String>[] entries = mapList.entrySet().toArray(new Map.Entry[]{});
        String otherString = entries[0].getKey();
        // 去除无效数据
        if (entries.length >= 2) {
            Map.Entry<String, String> entry = entries[1];
            // 去除第二条为diff的情况
            if (!entry.getKey().equals("diff")) {
                linkMap.put(entry.getKey(), entry.getValue());
                operatorMap.put(otherString, entry.getKey());
            }
        }
        // 清除缓存数据
        mapList.clear();
    }

    private void suffixStringSplit(String key) {
        String[] keyNode = key.toLowerCase(Locale.ENGLISH).split("\\.");
        StringBuffer sb = new StringBuffer();
        for (int i = keyNode.length - 1; i >= 0; i--) {
            sb.insert(0, keyNode[i]);
            if (!nodeMap.containsKey(sb.toString())) {
                nodeMap.putIfAbsent(sb.toString(), key);
            } else {
                nodeMap.putIfAbsent(sb + "." + keyNode[0], key);
            }
            sb.insert(0, ".");
        }
    }
}