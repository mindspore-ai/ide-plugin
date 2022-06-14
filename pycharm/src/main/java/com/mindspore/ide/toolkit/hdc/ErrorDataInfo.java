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

/**
 * 错误提示实体类
 *
 * @since 2022-04-18
 */
public class ErrorDataInfo {
    /**
     * 标题
     */
    private String titleString;

    /**
     * 菜单栏第一条
     */
    private String projectString;

    /**
     * 菜单栏第二条
     */
    private String descriptionString;

    /**
     * 表格数据
     */
    private List<String[]> strings;

    /**
     * 合并数据
     */
    private List<int[]> intMerge;

    /**
     * 整体错误信息
     */
    private String errorString;

    public String getTitleString() {
        return titleString;
    }

    public void setTitleString(String titleString) {
        this.titleString = titleString;
    }

    public String getProjectString() {
        return projectString;
    }

    public void setProjectString(String projectString) {
        this.projectString = projectString;
    }

    public String getDescriptionString() {
        return descriptionString;
    }

    public void setDescriptionString(String descriptionString) {
        this.descriptionString = descriptionString;
    }

    public List<String[]> getStrings() {
        return strings;
    }

    public void setStrings(List<String[]> strings) {
        this.strings = strings;
    }

    public List<int[]> getIntMerge() {
        return intMerge;
    }

    public void setIntMerge(List<int[]> intMerge) {
        this.intMerge = intMerge;
    }

    public String getErrorString() {
        return errorString;
    }

    public void setErrorString(String errorString) {
        this.errorString = errorString;
    }
}