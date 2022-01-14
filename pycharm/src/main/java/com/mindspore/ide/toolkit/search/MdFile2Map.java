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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * md file to map
 *
 * @since 1.0
 */
public class MdFile2Map {
    /**
     * 获取api对应url
     *
     * @param mdStringList md数据列表
     * @return api对应url的map
     */
    public Map<String, String> md2MapList(List<String> mdStringList) {
        Map<String, String> mindSporeMap = new LinkedHashMap<>();
        for (String mdString : mdStringList) {
            mindSporeMap.putAll(md2MapList(mdString));
        }
        return mindSporeMap;
    }

    /**
     * 获取api对应url
     *
     * @param mdString md数据
     * @return api对应url的map
     */
    public Map<String, String> md2MapList(String mdString) {
        Map<String, String> mindSporeMap = new LinkedHashMap<>();
        mdStringList(mdString, mindSporeMap);
        return mindSporeMap;
    }

    /**
     * 处理md数据，获取api对应api和api对应url
     *
     * @param mdString     md数据
     * @param mindSporeMap api对应url
     * @return api对应api
     */
    public Map<String, String> mdStringList(String mdString, Map<String, String> mindSporeMap) {
        Map<String, String> otherMap = new LinkedHashMap<>();
        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        Node document = parser.parse(mdString);
        document.getChildIterator().forEachRemaining(node -> {
            if (node instanceof Paragraph) {
                List<Map<String, String>> mapList = new ArrayList<>();
                node.getChildIterator().forEachRemaining(node1 -> {
                    if (node1 instanceof Link) {
                        Link link = (Link) node1;
                        String text = link.getText().toString();
                        String url = link.getUrl().toString();
                        Map<String, String> stringMap = new LinkedHashMap<>();
                        stringMap.put(text, url);
                        mapList.add(stringMap);
                    } else if (node1 instanceof SoftLineBreak) {
                        // 换行处理缓存数据
                        getMapData(otherMap, mindSporeMap, mapList);
                    } else {
                        node1.getNodeName();
                    }
                });
                // 防止一条数据没有换行直接进入下一个循环导致数据丢失
                getMapData(otherMap, mindSporeMap, mapList);
            }
        });
        return otherMap;
    }

    /**
     * 处理缓存数据
     *
     * @param otherMap     其他api对应mindSpore的api
     * @param mindSporeMap mindSpore的api对应url
     * @param mapList      缓存数据
     */
    private void getMapData(Map<String, String> otherMap, Map<String, String> mindSporeMap,
                            List<Map<String, String>> mapList) {
        if (mapList.size() <= 0) {
            return;
        }
        String otherString = null;
        Map<String, String> stringMap1 = mapList.get(0);
        for (Map.Entry<String, String> entry : stringMap1.entrySet()) {
            otherString = entry.getKey();
        }
        // 去除无效数据
        if (mapList.size() >= 2) {
            Map<String, String> stringMap2 = mapList.get(1);
            // 去除第二条为diff的情况
            if (!stringMap2.containsKey("diff")) {
                String mindSporeString = null;
                for (Map.Entry<String, String> entry : stringMap2.entrySet()) {
                    mindSporeString = entry.getKey();
                    mindSporeMap.put(entry.getKey(), entry.getValue());
                }
                otherMap.put(otherString, mindSporeString);
            } else {
                otherMap.put(otherString, "");
            }
        } else {
            otherMap.put(otherString, "");
        }
        // 清除缓存数据
        mapList.clear();
    }
}