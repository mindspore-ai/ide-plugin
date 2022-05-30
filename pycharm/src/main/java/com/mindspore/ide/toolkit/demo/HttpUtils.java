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

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import static org.apache.http.HttpVersion.HTTP_1_1;

/**
 * http utils download ai mode
 *
 * @since 2022-04-18
 */
public class HttpUtils {
    /**
     * get data from some url
     *
     * @param url       file url
     * @param headerMap request header
     * @return CloseableHttpResponse
     * @throws IOException io exception
     */
    public static CloseableHttpResponse doGet(String url, Map<String, String> headerMap) throws IOException {
        return HttpClients.createDefault().execute(initHttpGet(url, headerMap, -1));
    }

    /**
     * get data from some url
     *
     * @param url       file url
     * @param headerMap request header
     * @param timeout   timeout(ms)
     * @return CloseableHttpResponse
     * @throws IOException io exception
     */
    public static CloseableHttpResponse doGet(String url, Map<String, String> headerMap,
            int timeout) throws IOException {
        return HttpClients.createDefault().execute(initHttpGet(url, headerMap, timeout));
    }

    /**
     * json post
     *
     * @param url       url
     * @param headerMap map
     * @param body      body
     * @return CloseableHttpResponse
     * @throws IOException io exception
     */
    public static CloseableHttpResponse doJsonPost(String url, Map<String, String> headerMap,
            String body) throws IOException {
        return HttpClients.createDefault().execute(initHttpJsonPost(url, headerMap, body, -1));
    }

    /**
     * json post
     *
     * @param url       url
     * @param headerMap map
     * @param body      body
     * @param timeout   timeout
     * @return CloseableHttpResponse
     * @throws IOException io exception
     */
    public static CloseableHttpResponse doJsonPost(String url, Map<String, String> headerMap, String body,
            int timeout) throws IOException {
        return HttpClients.createDefault().execute(initHttpJsonPost(url, headerMap, body, timeout));
    }

    private static HttpGet initHttpGet(String url, Map<String, String> headerMap, int timeout) {
        HttpGet httpGet = new HttpGet();
        httpGet.setURI(URI.create(url));
        if (!headerMap.isEmpty()) {
            headerMap.forEach(httpGet::setHeader);
        }
        if (timeout != -1) {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(timeout)
                    .setConnectTimeout(timeout)
                    .setSocketTimeout(timeout).build();
            httpGet.setConfig(requestConfig);
        }
        return httpGet;
    }

    /**
     * init Http Json Post
     *
     * @param url       url
     * @param headerMap headerMap
     * @param body      body
     * @param timeout   timeout
     * @return HttpPost
     */
    public static HttpPost initHttpJsonPost(String url, Map<String, String> headerMap, String body, int timeout) {
        HttpPost httpPost = new HttpPost(URI.create(url));
        httpPost.setProtocolVersion(HTTP_1_1);
        headerMap.forEach(httpPost::addHeader);
        httpPost.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
        if (timeout != -1) {
            RequestConfig requestConfig =
                    RequestConfig.custom()
                            .setConnectionRequestTimeout(timeout)
                            .setConnectTimeout(timeout)
                            .setSocketTimeout(timeout)
                            .build();
            httpPost.setConfig(requestConfig);
        }
        return httpPost;
    }
}