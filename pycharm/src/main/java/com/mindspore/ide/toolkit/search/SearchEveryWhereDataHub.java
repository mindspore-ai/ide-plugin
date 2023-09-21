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

package com.mindspore.ide.toolkit.search;

import java.util.List;
import java.util.Set;

/**
 * store data for search every where
 *
 * @param <V> search input type
 * @since 2022/01/24
 */
public interface SearchEveryWhereDataHub<K, V> {
    /**
     * all records should be indexed
     *
     * @return resource set
     */
    Set<K> searchable();

    /**
     * assemble well-prepared map for search everywhere
     *
     * @param rawList raw search result from index
     * @param search search info
     * @param count expected result set length
     * @return result map to show
     */
    List<V> assemble(List<K> rawList, K search, int count);

    /**
     * Precise full search
     *
     * @param input input
     * @return result map to show
     */
    List<V> fetchAllMatch(String input);

    /**
     * get operator data hub by this interface
     *
     * @return operator data hub
     */
}
