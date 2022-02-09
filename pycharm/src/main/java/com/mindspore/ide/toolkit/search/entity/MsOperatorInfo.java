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

package com.mindspore.ide.toolkit.search.entity;

/**
 * entity for operator search value
 *
 * @since 2022/01/30
 */
public class MsOperatorInfo {
    private String mindSporeOperator;
    private String link;

    public MsOperatorInfo(String mindSporeOperator, String link) {
        this.mindSporeOperator = mindSporeOperator;
        this.link = link;
    }

    public String getMindSporeOperator() {
        return mindSporeOperator;
    }

    public String getLink() {
        return link;
    }
}


