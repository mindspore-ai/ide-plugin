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

import java.util.List;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.util.Map;
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

    private static int iOne = 0;

    private static Map<Integer, String> mapOne;

    private static Map<Integer, String> mapTwo;

    private static StringBuilder stringBuilderTwo;

    private static List<int[]> intMerge;

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

    /**
     * 将获取的所有log数据从error中提取出来
     *
     * @param in        in
     * @param errorList errorList
     * @param allList   allList
     */
    private static void typeErrorList(int in, List<String> errorList, List<String> allList) {
        if (allList.get(in).contains(ERROR_TITLE)) {
            errorList.add(allList.get(in - 1));
            errorList.add(allList.get(in));
            lineNumber = in;
        } else {
            addErrorList(in, errorList, allList);
        }
    }

    /**
     * 将获取的所有log数据从error中提取出来
     *
     * @param in        in
     * @param errorList errorList
     * @param allList   allList
     */
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
        List<Integer> ints = new LinkedList<>();
        StringBuilder errorString = new StringBuilder();
        for (int i = 0; i < errorList.size(); i++) {
            errorString.append(errorList.get(i)).append(System.getProperty("line.separator"));
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
                    ints.add(stringList.size());
                }
                String[] strings = {projectStr, descriptionStr};
                stringList.add(strings);
            } else {
                errorList.size();
            }
        }
        errorDataInfo.setInts(ints);
        errorDataInfo.setStrings(stringList);
        errorDataInfo.setErrorString(errorString.toString());
        errorDataInfo = filterErrorDataInfo(errorDataInfo);
        return errorDataInfo;
    }

    /**
     * 过滤数据
     *
     * @param errorDataInfo errorDataInfo
     * @return errorDataInfo
     */
    public static ErrorDataInfo filterErrorDataInfo(ErrorDataInfo errorDataInfo) {
        mapOne = new LinkedHashMap<>();
        mapTwo = new LinkedHashMap<>();
        stringBuilderTwo = new StringBuilder();
        intMerge = new LinkedList<>();
        for (int i = 0; i < errorDataInfo.getStrings().size(); i++) {
            String[] strings = errorDataInfo.getStrings().get(i);
            mapOne.put(i, strings[0]);
            if (strings[0].equals("")) {
                equalsEmpty(strings, i, errorDataInfo);
            } else {
                equalsNoEmpty(errorDataInfo, i, strings);
                iOne = 0;
            }
        }
        List<String[]> listNew = new LinkedList<>();
        listFormat(listNew, mapOne, mapTwo);
        ErrorDataInfo errorDataInfoNew = new ErrorDataInfo();
        errorDataInfoFormat(errorDataInfo, errorDataInfoNew, listNew, intMerge);
        return errorDataInfoNew;
    }

    private static void equalsEmpty(String[] strings, int inInt, ErrorDataInfo errorDataInfo) {
        if (stringBuilderTwo.length() > 0) {
            stringBuilderTwo.append(System.getProperty("line.separator")).append(strings[1]);
        } else {
            iOne = inInt - 1;
            stringBuilderTwo.append(errorDataInfo.getStrings().get(inInt - 1)[1])
                    .append(System.getProperty("line.separator")).append(strings[1]);
        }
        if (inInt == errorDataInfo.getStrings().size() - 1) {
            // 最后一条数据
            lastDataFormat(inInt, errorDataInfo, strings);
            iOne = 0;
        } else {
            mapTwo.put(inInt, "");
        }
    }

    private static void equalsNoEmpty(ErrorDataInfo errorDataInfo, int inInt, String[] strings) {
        if (stringBuilderTwo.length() > 0) {
            int[] stringsMergeOne = {iOne, inInt - 1, 0, 0};
            if (errorDataInfo.getStrings().get(iOne)[0].contains("代码")) {
                int[] stringsMergeTwo = {iOne, inInt - 1, 1, 1};
                intMerge.add(stringsMergeTwo);
            }
            intMerge.add(stringsMergeOne);
            if (errorDataInfo.getStrings().get(iOne)[0].contains("代码")) {
                mapTwo.put(iOne, stringBuilderTwo.toString());
            }
            mapTwo.put(inInt, strings[1]);
            stringBuilderTwo.delete(0, stringBuilderTwo.length());
        } else {
            mapTwo.put(inInt, strings[1]);
        }
    }


    /**
     * 处理最后一条数据
     *
     * @param inInt         inInt
     * @param errorDataInfo errorDataInfo
     * @param strings       strings
     */
    private static void lastDataFormat(int inInt, ErrorDataInfo errorDataInfo, String[] strings) {
        if (stringBuilderTwo.length() > 0) {
            int[] stringsMergeOne = {iOne, inInt, 0, 0};
            if (errorDataInfo.getStrings().get(iOne)[0].contains("代码")) {
                int[] stringsMergeTwo = {iOne, inInt, 1, 1};
                intMerge.add(stringsMergeTwo);
            }
            intMerge.add(stringsMergeOne);
            if (errorDataInfo.getStrings().get(iOne)[0].contains("代码")) {
                mapTwo.put(iOne, stringBuilderTwo.toString());
                mapTwo.put(inInt, "");
            } else {
                mapTwo.put(inInt, strings[1]);
            }
            // 有值不为空变下一条
            stringBuilderTwo.delete(0, stringBuilderTwo.length());
        } else {
            // 无值
            mapTwo.put(inInt, strings[1]);
        }
    }

    /**
     * 处理表格数据
     *
     * @param listNew listNew
     * @param mapOne  mapOne
     * @param mapTwo  mapTwo
     */
    private static void listFormat(List<String[]> listNew, Map<Integer, String> mapOne, Map<Integer, String> mapTwo) {
        if (mapOne.size() != mapTwo.size()) {
            return;
        }
        for (Map.Entry<Integer, String> oneResult : mapOne.entrySet()) {
            for (Map.Entry<Integer, String> twoResult : mapTwo.entrySet()) {
                if (oneResult.getKey().equals(twoResult.getKey())) {
                    String[] strings = {oneResult.getValue(), twoResult.getValue()};
                    listNew.add(strings);
                }
            }
        }
    }

    /**
     * 将数据处理
     *
     * @param errorDataInfo    errorDataInfo
     * @param errorDataInfoNew errorDataInfoNew
     * @param listNew          listNew
     * @param intMerge         intMerge
     */
    private static void errorDataInfoFormat(ErrorDataInfo errorDataInfo, ErrorDataInfo errorDataInfoNew,
            List<String[]> listNew, List<int[]> intMerge) {
        errorDataInfoNew.setTitleString(errorDataInfo.getTitleString());
        errorDataInfoNew.setDescriptionString(errorDataInfo.getDescriptionString());
        errorDataInfoNew.setProjectString(errorDataInfo.getProjectString());
        errorDataInfoNew.setInts(errorDataInfo.getInts());
        errorDataInfoNew.setErrorString(errorDataInfo.getErrorString());
        errorDataInfoNew.setStrings(listNew);
        errorDataInfoNew.setIntMerge(intMerge);
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