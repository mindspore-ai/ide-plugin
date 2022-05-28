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

package com.mindspore.ide.toolkit.services.filter;

/**
 * 白泽的推荐结果或者是底座的推荐结果的过滤器
 * 实现该接口建议通过继承{@link AbstractFilter}来实现
 *
 * @since 2022-05-28
 */
public interface Filter extends Order {
    /**
     * 判断是否需要执行该filter
     *
     * @param filterContext filter上下文
     * @return true or false
     */
    default boolean match(FilterContext filterContext) {
        return true;
    }

    /**
     * 执行过滤动作，可以过滤白泽的推荐结果或者是底座的结果，或者两个都过滤
     *
     * @param filterContext 封装过滤所需的上下文信息
     */
    void filter(FilterContext filterContext);
}
