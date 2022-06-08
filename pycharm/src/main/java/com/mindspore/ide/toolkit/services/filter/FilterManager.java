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

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * 过滤器管理器，单例实现
 *
 * @since 2022-05-28
 */
public enum FilterManager {
    INSTANCE;

    private final List<Filter> filters = new LinkedList<>();

    FilterManager() {
        init();
    }

    /**
     * 过滤
     *
     * @param filterContext FilterContext
     */
    public void doFilter(FilterContext filterContext) {
        filters.forEach(filter -> filter.filter(filterContext));
    }

    private void init() {
        filters.add(new RepeatFilter());
        filters.add(new FinalFilter());
        filters.sort(Comparator.comparing(Filter::getOrder));
    }
}
