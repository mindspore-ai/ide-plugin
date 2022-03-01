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

import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * OperatorSearchService ut
 *
 * @since 1.0
 */
public class OperatorSearchServiceTest {
    @Test
    public void testOperatorSearchService() {
        Assert.assertEquals(0, OperatorSearchService.INSTANCE.search("mindspore").size());
        Assert.assertEquals(0, OperatorSearchService.INSTANCE.search("mindspore.ops").size());
        Assert.assertEquals(380, OperatorSearchService.INSTANCE.search("torch").size());
        Assert.assertEquals(380, OperatorSearchService.INSTANCE.search("TORCH").size());
        Assert.assertEquals(10, OperatorSearchService.INSTANCE.search("torch", 10).size());
        Assert.assertEquals(10, OperatorSearchService.INSTANCE.search("TORCH", 10).size());
        Assert.assertEquals(103, OperatorSearchService.INSTANCE.search("tf").size());
        Assert.assertEquals(103, OperatorSearchService.INSTANCE.search("TF").size());
        Assert.assertEquals(10, OperatorSearchService.INSTANCE.search("tf", 10).size());
        Assert.assertEquals(10, OperatorSearchService.INSTANCE.search("TF", 10).size());

        Map<String, String> map1 = new LinkedHashMap<>();
        map1.put("tf.metrics.mean_iou -> mindspore.ops.IOU",
        "https://mindspore.cn/docs/api/zh-CN/r1.6/api_python/ops/mindspore.ops.IOU.html");
        Assert.assertEquals(map1, OperatorSearchService.INSTANCE.search("tf.metrics.mean_iou"));
        Assert.assertEquals(map1, OperatorSearchService.INSTANCE.search("metrics.mean_iou"));
        Assert.assertEquals(map1, OperatorSearchService.INSTANCE.search("mean_iou"));
        Assert.assertEquals(map1, OperatorSearchService.INSTANCE.search("TF.METRICS.MEAN_IOU"));
        Assert.assertEquals(map1, OperatorSearchService.INSTANCE.search("METRICS.MEAN_IOU"));
        Assert.assertEquals(map1, OperatorSearchService.INSTANCE.search("MEAN_IOU"));

        Map<String, String> map2 = new LinkedHashMap<>();
        map2.put("torch.abs -> mindspore.ops.Abs",
        "https://mindspore.cn/docs/api/zh-CN/r1.6/api_python/ops/mindspore.ops.Abs.html#mindspore.ops.Abs");
        Assert.assertEquals(map2, OperatorSearchService.INSTANCE.search("torch.abs"));
        Assert.assertEquals(map2, OperatorSearchService.INSTANCE.search("torch.Abs"));
    }

    @Test
    public void testOperatorSearchServiceSpecial() {
        // PyTorch一个的api没有mindSpore的api对应
        Assert.assertEquals(0, OperatorSearchService.INSTANCE.search("torch.utils.data.DataLoader").size());
        Assert.assertEquals(0, OperatorSearchService.INSTANCE.search("utils.data.DataLoader").size());
        Assert.assertEquals(0, OperatorSearchService.INSTANCE.search("data.DataLoader").size());
        Assert.assertEquals(0, OperatorSearchService.INSTANCE.search("DataLoader").size());

        // PyTorch一个的api指向mindSpore多个api
        Map<String, String> map1 = new LinkedHashMap<>();
        map1.put("torch.nn.GELU -> mindspore.nn.GELU",
        "https://mindspore.cn/docs/api/zh-CN/r1.6/api_python/nn/mindspore.nn.GELU.html#mindspore.nn.GELU");
        map1.put("torch.nn.GELU -> mindspore.nn.FastGelu",
        "https://mindspore.cn/docs/api/zh-CN/r1.6/api_python/nn/mindspore.nn.FastGelu.html#mindspore.nn.FastGelu");
        Assert.assertEquals(map1, OperatorSearchService.INSTANCE.search("torch.nn.GELU"));

        // mindSpore一个api对应多个url
        Map<String, String> map2 = new LinkedHashMap<>();
        map2.put("tf.keras.optimizers.Adagrad -> mindspore.nn.Adagrad",
        "https://mindspore.cn/docs/api/zh-CN/r1.6/api_python/nn/mindspore.nn.Adagrad.html");
        map2.put("torch.optim.Adagrad -> mindspore.nn.Adagrad",
        "https://mindspore.cn/docs/api/zh-CN/r1.6/api_python/nn/mindspore.nn.Adagrad.html#mindspore.nn.Adagrad");
        Assert.assertEquals(map2, OperatorSearchService.INSTANCE.search("Adagrad"));

        Map<String, String> map3 = new LinkedHashMap<>();
        map3.put("torch.Tensor.take -> mindspore.Tensor.take",
        "https://www.mindspore.cn/docs/api/zh-CN/r1.6/api_python/mindspore/mindspore.Tensor.html#mindspore.Tensor.take"
        );
        map3.put("torch.take -> mindspore.Tensor.take",
        "https://mindspore.cn/docs/api/zh-CN/r1.6/api_python/mindspore/mindspore.Tensor.html#mindspore.Tensor.take");
        Assert.assertEquals(map3, OperatorSearchService.INSTANCE.search("take"));
    }

    @Test
    public void testOperatorSearchServiceError() {
        Assert.assertEquals(0, OperatorSearchService.INSTANCE.search("").size());
        Assert.assertEquals(0, OperatorSearchService.INSTANCE.search("", 10).size());
        Assert.assertEquals(0, OperatorSearchService.INSTANCE.search(null).size());
        Assert.assertEquals(0, OperatorSearchService.INSTANCE.search(null, 10).size());

        Assert.assertEquals(0, OperatorSearchService.INSTANCE.search("mindspore", 0).size());
        Assert.assertEquals(0, OperatorSearchService.INSTANCE.search("mindspore", -1).size());
        Assert.assertEquals(0, OperatorSearchService.INSTANCE.search("spore").size());
        Assert.assertEquals(0, OperatorSearchService.INSTANCE.search("spore", 10).size());
    }
}