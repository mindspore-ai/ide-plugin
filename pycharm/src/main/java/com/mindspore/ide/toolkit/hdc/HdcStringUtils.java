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

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * string工具类
 *
 * @since 2022-04-18
 */
public class HdcStringUtils {
    /**
     * 错误开始的标题
     */
    public static final String ERROR_TITLE = "MindSpore FAR(Failure Analysis Report)";

    private static int lineNumber = -1;

    /**
     * 将获取的所有log数据从error中提取出来
     *
     * @param allList 所有的log数据列表
     * @return error数据
     */
    public static List<String> allListToErrorList(List<String> allList) {
        List<String> errorList = new LinkedList<>();
        lineNumber = -1;
        for (int i = 0; i < allList.size(); i++) {
            typeErrorList(i, errorList, allList);
        }
        return errorList;
    }

    private static void typeErrorList(int in, List<String> errorList, List<String> allList) {
        if (allList.get(in).contains(ERROR_TITLE)) {
            errorList.add(allList.get(in - 1));
            errorList.add(allList.get(in));
            lineNumber = in;
        } else {
            addErrorList(in, errorList, allList);
        }
    }

    private static void addErrorList(int in, List<String> errorList, List<String> allList) {
        if (lineNumber != -1 && in - lineNumber < 8) {
            errorList.add(allList.get(in));
        } else if (lineNumber != -1 && in - lineNumber >= 8) {
            if (allList.get(in).equals(errorList.get(0))) {
                errorList.add(allList.get(in));
                lineNumber = -1;
            } else {
                errorList.add(allList.get(in));
            }
        } else {
            errorList.size();
        }
    }

    /**
     * 将error数据列表转换成ErrorDataInfo
     *
     * @param errorList error数据列表
     * @return ErrorDataInfo
     */
    public static ErrorDataInfo errorListToErrorDataInfo(List<String> errorList) {
        ErrorDataInfo errorDataInfo = new ErrorDataInfo();
        List<String[]> stringList = new LinkedList<>();
        for (int i = 0; i < errorList.size(); i++) {
            if (i == 1) {
                // 标题
                errorDataInfo.setTitleString(errorList.get(i).replaceAll("\\|", "").trim());
            } else if (i == 3) {
                String str = errorList.get(i);
                String[] strings = str.split("\\|");
                if (strings.length > 2) {
                    String str1 = strings[1];
                    String str2 = strings[2];
                    errorDataInfo.setProjectString(str1.trim());
                    errorDataInfo.setDescriptionString(str2.trim());
                }
            } else if (i > 4 && i < errorList.size() - 1) {
                String str = errorList.get(i);
                int twoChar = getIndexOf(str, "\\|", 2);
                String projectStr = str.substring(1, twoChar).trim();
                String descriptionStr = str.substring(twoChar + 1, str.length() - 1).trim().replaceAll("\\|", "");
                if (descriptionStr.contains("+---") && descriptionStr.contains("---+")) {
                    descriptionStr = "";
                }
                String strHttp = HdcRegularUtils.filterSpecialStr(HdcRegularUtils.REGEX_HTTP, descriptionStr);
                if (!strHttp.isEmpty()) {
                    descriptionStr = "<html><body>"
                            + descriptionStr.replaceAll(strHttp, "<a herf=\"" + strHttp + "\">" + strHttp + "</a>")
                            + "</body></html>";
                    String[] strings = {projectStr, descriptionStr};
                    stringList.add(strings);
                }
            } else {
                errorList.size();
            }
        }
        errorDataInfo.setStrings(stringList);
        return errorDataInfo;
    }

    /**
     * java获取某个字符在指定字符串出现的第N次的位置
     *
     * @param data 指定字符串
     * @param str  需要定位的特殊字体或者字符串
     * @param num  第n次出现
     * @return 第n次出现的位置
     */
    public static int getIndexOf(String data, String str, int num) {
        Pattern pattern = Pattern.compile(str);
        Matcher findMatcher = pattern.matcher(data);
        int indexNum = 0;
        while (findMatcher.find()) {
            indexNum++;
            if (indexNum == num) {
                break;
            }
        }
        return findMatcher.start();
    }
}