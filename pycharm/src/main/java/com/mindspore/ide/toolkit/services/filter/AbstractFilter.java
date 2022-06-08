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

import java.util.Objects;

/**
 * 过滤器的基类，增加模板方法
 *
 * @since 2022-05-28
 */
public abstract class AbstractFilter implements Filter {
    @Override
    public void filter(FilterContext filterContext) {
        if (Objects.isNull(filterContext) || !match(filterContext)) {
            return;
        }

        doFilter(filterContext);
    }

    /**
     * 真正执行过滤的逻辑
     *
     * @param filterContext 过滤所需的上下文
     */
    protected abstract void doFilter(FilterContext filterContext);
}
