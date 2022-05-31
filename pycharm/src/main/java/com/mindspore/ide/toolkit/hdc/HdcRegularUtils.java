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

package com.mindspore.ide.toolkit.hdc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则工具类
 *
 * @since 2022-04-18
 */
public class HdcRegularUtils {
    /**
     * 网址规则
     */
    public static final String REGEX_HTTP = "((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})"
            + "|([0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?";

    /**
     * 取出字符串中网址
     *
     * @param regex 规则
     * @param data  字符串
     * @return 网址
     */
    public static String filterSpecialStr(String regex, String data) {
        StringBuilder stringBuilder = new StringBuilder();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);
        while (matcher.find()) {
            stringBuilder.append(matcher.group());
            return stringBuilder.toString();
        }
        return "";
    }
}