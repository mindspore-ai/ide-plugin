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

/**
 * one item in a page
 *
 * @since 2022-04-18
 */
public class GuessItem {
    /**
     * item tag
     */
    private String tag;

    /**
     * item title
     */
    private String title;

    /**
     * item url
     */
    private String url;

    public GuessItem(String tagIn, String titleIn, String urlIn) {
        tag = tagIn;
        title = titleIn;
        url = urlIn;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 把GuessItem格式转化成RecommendationBean格式
     *
     * @return RecommendationBean格式
     */
    public RecommendationBean toRecommendationBean() {
        RecommendationBean res = new RecommendationBean();
        res.setTitle(this.getTitle());
        res.setUrl(this.getUrl());
        res.setTag(this.getTag());
        res.setId(-1);
        return res;
    }
}