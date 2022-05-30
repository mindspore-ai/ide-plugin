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
import com.google.gson.stream.JsonReader;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Search methods
 *
 * @since 2022-04-18
 */
public class SearchRetriever {
    private static final Gson GSON = new GsonBuilder().create();

    /**
     * Use fusion search
     *
     * @param input  search word
     * @param fields fields
     * @return search result
     */
    public static FusionOuterResponse searchFusion(String input, String[] fields) {
        FusionSearchPost bean = new FusionSearchPost();
        bean.setQuery(input);
        bean.setFields(fields);
        bean.setLimit(100);
        String body = GSON.toJson(bean);
        return search(body, SearchRoute.FUSION_SEARCH);
    }

    /**
     * use fusion search whit mindspore data v1.6
     *
     * @param input  search word
     * @param fields fields
     * @return search result
     */
    public static FusionOuterResponse searchFusionWhit16(String input, String[] fields) {
        FusionSearchPost bean = new FusionSearchPost();
        bean.setQuery(input);
        bean.setFields(fields);
        bean.setLimit(100);
        String body = GSON.toJson(bean);
        return search(body, SearchRoute.FUSION_SEARCH_WITH16);
    }

    /**
     * Use AI search
     *
     * @param input       search word
     * @param userType    user type
     * @param isNeedDocInfo if need doc info
     * @return search result
     */
    public static List<AISearchResponse> searchAI(String input, String userType, boolean isNeedDocInfo) {
        AISearchPost bean = new AISearchPost();
        bean.setQuery(input);
        bean.setUserType(userType);
        bean.setDocInfo(isNeedDocInfo ? 1 : 0);
        String body = GSON.toJson(bean);
        return search(body, SearchRoute.AI_SEARCH);
    }

    private static <T> T search(String body, SearchRoute searchRoute) {
        Object result = null;
        try (CloseableHttpResponse response =
                HttpUtils.doJsonPost(searchRoute.getServer(), Collections.emptyMap(), body)) {
            if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                InputStream resultStream = response.getEntity().getContent();
                JsonReader reader = new JsonReader(new InputStreamReader(resultStream, StandardCharsets.UTF_8));
                ResponseBeanBuilder builder = ResponseBeanBuilder.builderGet(searchRoute).get();
                result = builder.builder(reader, GSON);
            }
        } catch (IOException | NoSuchElementException exception) {
            exception.toString();
        }
        return (T) result;
    }
}