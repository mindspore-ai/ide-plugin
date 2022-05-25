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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * web端交互的方法
 *
 * @since 2022-04-18
 */
public class WebCommunicator {
    private static Gson gson = new GsonBuilder().create();

    /**
     * 搜索
     *
     * @param searchWords 搜索词
     * @return 搜索结果json字符串
     */
    public static String search(String searchWords) {
        if (searchWords == null || searchWords.length() == 0) {
            return recommendation();
        }
        List<FusionSearchResponse> raw = SearchDemo.search(searchWords);
        List<WebBean> webBeans = new ArrayList<>(10);
        for (FusionSearchResponse single : raw) {
            webBeans.add(webBeanConvertorForSearch(single));
        }
        return gson.toJson(webBeans);
    }

    /**
     * 猜你喜欢
     *
     * @return 结果json字符串
     */
    public static String recommendation() {
        GuessYouLike guess = GuessYouLike.INSTANCE;
        // num 是要求显示某一类用户的第几张页面
        List<GuessItem> raw = guess.getNextPage(UserType.getCurrent());
        List<WebBean> webBeans = new ArrayList<>(18);
        for (GuessItem single : raw) {
            webBeans.add(webBeanConvertorForRecommendation(single.toRecommendationBean()));
        }
        return gson.toJson(webBeans);
    }

    private static WebBean webBeanConvertorForSearch(FusionSearchResponse result) {
        WebBean bean = new WebBean();
        bean.setTitle("[" + result.getType() + "]" + result.getTitle());
        bean.setTitle(result.getTextContent());
        bean.setTitle(UrlRetriever.INSTANCE.getUrl(result.getId()));
        return bean;
    }

    private static WebBean webBeanConvertorForRecommendation(RecommendationBean result) {
        WebBean bean = new WebBean();
        bean.setTitle(result.getTag());
        bean.setTitle(result.getTitle());
        bean.setTitle(result.getUrl());
        return bean;
    }
}