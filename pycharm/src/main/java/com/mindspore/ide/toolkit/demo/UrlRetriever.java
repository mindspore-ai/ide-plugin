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

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * id转换为url
 *
 * @since 2022-04-18
 */
public enum UrlRetriever {
    INSTANCE;
    private static final String SUCCESS = "success";
    private static final String STATE_HASH = "mindspore_index_state";
    private static final String URL_HASH = "mindspore_index_url";
    private static final String HOST = "10.44.141.111";
    private static final String PASSWORD = "redis123";
    private static final int PORT = 6379;

    private JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), HOST, PORT, 3000, PASSWORD);

    /**
     * 通过id获得url
     *
     * @param id id值
     * @return url
     */
    public String getUrl(int id) {
        String url;
        try (Jedis jedis = jedisPool.getResource()) {
            url = jedis.hget(URL_HASH, String.valueOf(id));
        }
        return checkHttp(url);
    }

    /**
     * id对应url是否能链接成功
     *
     * @param id id值
     * @return url
     */
    public boolean getState(int id) {
        boolean isResult;
        try (Jedis jedis = jedisPool.getResource()) {
            String state = jedis.hget(STATE_HASH, String.valueOf(id));
            isResult = SUCCESS.equals(state);
        }
        return isResult;
    }

    /**
     * 校验网址
     *
     * @param url url
     * @return 新网址
     */
    public String checkHttp(String url) {
        if (url == null) {
            return "";
        }
        if (url.startsWith("http")) {
            return url;
        } else {
            return "https://" + url;
        }
    }
}