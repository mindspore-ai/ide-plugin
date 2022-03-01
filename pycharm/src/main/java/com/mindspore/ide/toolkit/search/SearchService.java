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

import com.mindspore.ide.toolkit.common.config.DocSearchConfig;
import com.mindspore.ide.toolkit.common.utils.GsonUtils;
import com.mindspore.ide.toolkit.common.utils.HttpUtils;
import com.mindspore.ide.toolkit.search.constant.Constants;
import com.mindspore.ide.toolkit.search.entity.DocumentResultModel;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public enum SearchService {
    INSTANCE;

    public DocumentResultModel requestSearchResult(String searchText) {
        DocumentResultModel doc = null;
        Map<String, String> contentMap = new HashMap<>();
        contentMap.put(Constants.HTTP_REQUEST_SEARCHTEXT, searchText);
        try {
            HttpResponse response = HttpUtils.doPost(DocSearchConfig.get().getSearchApi(), getHeader(), contentMap);
            if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                String bodyStr = EntityUtils.toString(response.getEntity(), Constants.CHARSET_UTF_8);
                if (!bodyStr.isEmpty()) {
                    doc = GsonUtils.INSTANCE.getGson().fromJson(bodyStr, DocumentResultModel.class);
                    log.info("request success");
                } else {
                    log.info("request success , but no data");
                }
            }
        } catch (IOException e) {
            log.error("request error3", e);
        }
        return doc;
    }

    private HashMap<String, String> getHeader() {
        HashMap<String, String> header = new HashMap<>();
        header.put("Accept", "application/json");
        header.put("Connection", "keep-alive");
        return header;
    }
}