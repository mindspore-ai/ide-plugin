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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.util.LinkedList;
import java.util.List;

/**
 * AI search response entity builder
 *
 * @since 2022-04-18
 */
public enum AiSearchResponseBuilder implements ResponseBeanBuilder<List<AISearchResponse>> {
    BUILDER;

    @Override
    public List<AISearchResponse> builder(JsonReader reader, Gson gson) {
        List<AISearchResponse> result = new LinkedList<>();
        JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
        for (JsonElement single : array) {
            if (single instanceof JsonArray) {
                AISearchResponse res = gson.fromJson(((JsonArray) single).get(0), AISearchResponse.class);
                res.setSimilarity(gson.fromJson(((JsonArray) single).get(1), float.class));
                result.add(res);
            }
        }
        return result;
    }
}