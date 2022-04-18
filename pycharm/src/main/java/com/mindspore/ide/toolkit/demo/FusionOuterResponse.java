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

/**
 * Outer response bean for fusion search server
 *
 * @since 2022-04-18
 */
public class FusionOuterResponse {
    private int tookMs;

    private FusionSearchResponse[] hits;

    public int getTookMs() {
        return tookMs;
    }

    public void setTookMs(int tookMs) {
        this.tookMs = tookMs;
    }

    public FusionSearchResponse[] getHits() {
        return hits;
    }

    public void setHits(FusionSearchResponse[] hits) {
        this.hits = hits;
    }
}