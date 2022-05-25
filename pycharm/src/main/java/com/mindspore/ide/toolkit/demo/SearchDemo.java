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

package com.mindspore.ide.toolkit.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * demo search method
 *
 * @since 2022-04-18
 */
public class SearchDemo {
    /**
     * use fusion search
     *
     * @param search   search word
     * @param userType userType
     * @return search result
     */
    public static List<FusionSearchResponse> search(String search, UserType userType) {
        List<FusionSearchResponse> resultAll;
        if (search.equals("mindspore")) {
            return initMindSpore(search, userType);
        }
        if (search.equals("数据集")) {
            initData(search, userType);
        }
        if (search.equals("nn")) {
            initNn(search, userType);
        }
        if (search.equals("模型")) {
            List<FusionSearchResponse> raw = new ArrayList<>(Arrays.asList(
                    SearchRetriever.searchFusion(search, new String[]{"title", "textContent"}).getHits()));
            List<FusionSearchResponse> result = new LinkedList<>();
        }
        if (search.equals("optimizer")) {
            List<FusionSearchResponse> raw = new ArrayList<>(Arrays.asList(
                    SearchRetriever.searchFusion(search, new String[]{"title", "textContent"}).getHits()));
            List<FusionSearchResponse> result = new LinkedList<>();
        }
        if (search.equals("dataset")) {
            List<FusionSearchResponse> raw = new ArrayList<>(Arrays.asList(
                    SearchRetriever.searchFusion(search, new String[]{"title", "textContent"}).getHits()));
            List<FusionSearchResponse> result = new LinkedList<>();
        }
        if (search.equals("example")) {
            List<FusionSearchResponse> raw = new ArrayList<>(Arrays.asList(
                    SearchRetriever.searchFusion(search, new String[]{"title", "textContent"}).getHits()));
            List<FusionSearchResponse> result = new LinkedList<>();
        }
        if (search.equals("数据处理")) {
            List<FusionSearchResponse> raw = new ArrayList<>(Arrays.asList(
                    SearchRetriever.searchFusion(search, new String[]{"title", "textContent"}).getHits()));
            List<FusionSearchResponse> result = new LinkedList<>();
        }
        if (search.equals("install")) {
            List<FusionSearchResponse> raw = new ArrayList<>(Arrays.asList(
                    SearchRetriever.searchFusion(search, new String[]{"title", "textContent"}).getHits()));
            List<FusionSearchResponse> result = new LinkedList<>();
        }
        if (search.equals("train")) {
            List<FusionSearchResponse> raw = new ArrayList<>(Arrays.asList(
                    SearchRetriever.searchFusion(search, new String[]{"title", "textContent"}).getHits()));
            List<FusionSearchResponse> result = new LinkedList<>();
        }
        return new ArrayList(Arrays.asList(SearchRetriever.searchFusionWhit16(search,
                new String[]{"title", "textContent"}).getHits()));
    }

    private static List<FusionSearchResponse> initMindSpore(String search, UserType userType) {
        List<FusionSearchResponse> raw = new ArrayList<>(Arrays.asList(
                SearchRetriever.searchFusion(search, new String[]{"title", "textContent"}).getHits()));
        List<FusionSearchResponse> result = new LinkedList<>();
        initMindSporeNewbie(userType, raw, result);
        if (userType == UserType.valueOf("TRANSFER")) {
            List<FusionSearchResponse> rawTrans = new ArrayList<>(Arrays.asList(
                    SearchRetriever.searchFusionWhit16("mindspore pytorch",
                            new String[]{"title", "textContent"}).getHits()));
            result.add(rawTrans.get(0));
            result.add(rawTrans.get(3));
            result.add(raw.get(14));
            result.add(raw.get(24));
            result.add(raw.get(0));
            result.add(raw.get(1));
            result.add(raw.get(3));
            result.add(raw.get(5));
            result.add(raw.get(12));
            result.add(raw.get(20));
            result.add(raw.get(22));
            result.add(raw.get(25));
        }
        if (userType == UserType.valueOf("MASTER")) {
            result.add(raw.get(12));
            result.add(raw.get(18));
            result.add(raw.get(20));
            result.add(raw.get(22));
            result.add(raw.get(30));
            result.add(raw.get(31));
            result.add(raw.get(0));
            result.add(raw.get(1));
            result.add(raw.get(3));
            result.add(raw.get(5));
        }
        return result;
    }

    private static void initMindSporeNewbie(UserType userType, List<FusionSearchResponse> raw,
            List<FusionSearchResponse> result) {
        if (userType == UserType.valueOf("NEWBIE")) {
            List<FusionSearchResponse> rawInstall = new ArrayList<>(Arrays.asList(
                    SearchRetriever.searchFusionWhit16("mindspore install",
                            new String[]{"title", "textContent"}).getHits()));
            List<FusionSearchResponse> rawTutor = new ArrayList<>(Arrays.asList(
                    SearchRetriever.searchFusionWhit16("mindspore 入门",
                            new String[]{"title", "textContent"}).getHits()));
            result.add(rawInstall.get(1));
            result.add(rawInstall.get(4));
            result.add(rawInstall.get(8));
            result.add(rawTutor.get(0));
            result.add(rawTutor.get(9));
            result.add(raw.get(8));
            result.add(raw.get(10));
            result.add(raw.get(0));
            result.add(raw.get(1));
            result.add(raw.get(3));
            result.add(raw.get(5));
            result.add(raw.get(24));
            result.add(raw.get(25));
            result.add(raw.get(27));
            result.add(raw.get(12));
        }
    }

    private static void initData(String search, UserType userType) {
        List<FusionSearchResponse> raw = new ArrayList<>(Arrays.asList(
                SearchRetriever.searchFusion(search, new String[]{"title", "textContent"}).getHits()));
        List<FusionSearchResponse> result = new LinkedList<>();
        if (userType == UserType.valueOf("NEWBIE")) {
            result.add(raw.get(8));
            result.add(raw.get(10));
            result.add(raw.get(25));
            result.add(raw.get(35));
            result.add(raw.get(14));
            result.add(raw.get(16));
            result.add(raw.get(4));
            result.add(raw.get(2));
            result.add(raw.get(12));
            result.add(raw.get(5));
        }
        if (userType == UserType.valueOf("TRANSFER")) {
            List<FusionSearchResponse> rawTrans = new ArrayList<>(Arrays.asList(
                    SearchRetriever.searchFusionWhit16("数据迁移", new String[]{"title", "textContent"}).getHits()));
            result.add(rawTrans.get(9));
            result.add(rawTrans.get(12));
            result.add(rawTrans.get(36));
            result.add(raw.get(16));
            result.add(raw.get(4));
            result.add(raw.get(8));
            result.add(raw.get(10));
            result.add(raw.get(20));
            result.add(raw.get(2));
            result.add(raw.get(12));
            result.add(raw.get(17));
            result.add(raw.get(22));
        }
        if (userType == UserType.valueOf("MASTER")) {
            result.add(raw.get(5));
            result.add(raw.get(0));
            result.add(raw.get(2));
            result.add(raw.get(4));
            result.add(raw.get(10));
            result.add(raw.get(12));
            result.add(raw.get(16));
            result.add(raw.get(20));
            result.add(raw.get(27));
            result.add(raw.get(14));
        }
    }

    private static void initNn(String search, UserType userType) {
        List<FusionSearchResponse> raw = new ArrayList<>(Arrays.asList(
                SearchRetriever.searchFusion(search, new String[]{"title", "textContent"}).getHits()));
        List<FusionSearchResponse> result = new LinkedList<>();
        if (userType == UserType.valueOf("NEWBIE")) {
            List<FusionSearchResponse> rawInstall = new ArrayList<>(Arrays.asList(
                    SearchRetriever.searchFusionWhit16("nn 教程", new String[]{"title", "textContent"}).getHits()));
        }
        if (userType == UserType.valueOf("TRANSFER")) {
            result.add(raw.get(5));
            result.add(raw.get(0));
            result.add(raw.get(2));
            result.add(raw.get(4));
            result.add(raw.get(10));
            result.add(raw.get(12));
            result.add(raw.get(16));
            result.add(raw.get(20));
            result.add(raw.get(27));
            result.add(raw.get(14));
        }
        if (userType == UserType.valueOf("MASTER")) {
            result.add(raw.get(5));
            result.add(raw.get(0));
            result.add(raw.get(2));
            result.add(raw.get(4));
            result.add(raw.get(10));
            result.add(raw.get(12));
            result.add(raw.get(16));
            result.add(raw.get(20));
            result.add(raw.get(27));
            result.add(raw.get(14));
        }
    }

    /**
     * 调用的搜索接口，在此加入用户画像类型
     *
     * @param search 搜索词
     * @return 结果列表
     */
    public static List<FusionSearchResponse> search(String search) {
        List<FusionSearchResponse> result = search(search, UserType.getCurrent());
        return result == null ? new ArrayList<>() : result;
    }
}