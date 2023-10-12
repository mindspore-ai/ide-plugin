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

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.io.HttpRequests;
import com.mindspore.ide.toolkit.search.entity.ApiType;
import com.mindspore.ide.toolkit.search.entity.LinkInfo;
import com.mindspore.ide.toolkit.search.entity.OperatorRecord;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.ext.tables.TableBlock;
import com.vladsch.flexmark.ext.tables.TableBody;
import com.vladsch.flexmark.ext.tables.TableHead;
import com.vladsch.flexmark.ext.tables.TableRow;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.sequence.BasedSequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * md file to map
 *
 * @since 1.0
 */
public class OperatorMapDataHub implements SearchEveryWhereDataHub<String, OperatorRecord> {
    /**
     *
     * api支持平台类型
     */
    private String[] PLATFORM = {"Ascend", "GPU", "CPU"};

    /**
     * node数据
     */
    private Map<String, List<String>> nodeMap = new HashMap<>();

    /**
     * md文件处理后的数据
     */
    private Map<String, List<OperatorRecord>> operatorMap = new LinkedHashMap<>();

    public OperatorMapDataHub(MdDataGet mdDataGet) {
        if (mdDataGet.pytorchMdStr.isEmpty()) {
            mdStringList(MdPathString.PYTORCH_MD_STR, ApiType.PyTorch);
        } else {
            mdStringList(mdDataGet.pytorchMdStr, ApiType.PyTorch);
        }
        if (mdDataGet.tensorflowMdStr.isEmpty()) {
            mdStringList(MdPathString.TENSORFLOW_MD_STR, ApiType.TensorFlow);
        } else {
            mdStringList(mdDataGet.tensorflowMdStr, ApiType.TensorFlow);
        }
        for (Map.Entry<String, List<OperatorRecord>> entry : operatorMap.entrySet()) {
            suffixStringSplit(entry.getKey());
        }
        handleWhite();
    }

    private void handleWhite() {
        Set<String> white = new HashSet<>(Arrays.asList(new String[]{"mindspore.ops.abs", "mindspore.ops.acos", "mindspore.ops.add", "mindspore.ops.argmin", "mindspore.ops.asin", "mindspore.ops.atan", "mindspore.ops.atan2", "mindspore.ops.bartlett_window", "mindspore.ops.bitwise_and", "mindspore.ops.bitwise_or", "mindspore.ops.bitwise_xor", "mindspore.ops.blackman_window", "mindspore.ops.BatchMatMul", "mindspore.ops.broadcast_to", "mindspore.ops.ceil", "mindspore.ops.clip", "mindspore.ops.conj", "mindspore.ops.cos", "mindspore.ops.cosh", "mindspore.ops.cross", "mindspore.numpy.cross", "mindspore.ops.cumprod", "mindspore.ops.cumsum", "mindspore.ops.diag", "mindspore.ops.div", "mindspore.numpy.empty", "mindspore.numpy.empty_like", "mindspore.ops.equal", "mindspore.ops.erfc", "mindspore.ops.erfinv", "mindspore.ops.exp", "mindspore.ops.expm1", "mindspore.ops.eye", "mindspore.ops.flatten", "mindspore.ops.floor", "mindspore.ops.full", "mindspore.ops.full_like", "mindspore.numpy.hanning", "mindspore.ops.isreal", "mindspore.ops.norm", "mindspore.ops.log", "mindspore.ops.max", "mindspore.ops.mean", "mindspore.ops.min", "mindspore.ops.matmul", "mindspore.ops.multiply", "mindspore.ops.not_equal", "mindspore.ops.ones", "mindspore.ops.repeat_interleave", "mindspore.ops.stack", "mindspore.ops.sqrt", "mindspore.ops.tanh", "mindspore.ops.topk", "mindspore.ops.expand_dims", "mindspore.ops.zeros", "mindspore.ops.AllGather", "mindspore.ops.AllReduce", "mindspore.communication.get_rank", "mindspore.communication.init", "mindspore.communication.create_group", "mindspore.nn.AdaptiveAvgPool2d", "mindspore.nn.AvgPool1d", "mindspore.nn.AvgPool2d", "mindspore.nn.AvgPool3d", "mindspore.nn.BCEWithLogitsLoss", "mindspore.nn.BatchNorm1d", "mindspore.nn.BatchNorm2d", "mindspore.nn.CTCLoss", "mindspore.nn.Conv1d", "mindspore.nn.Conv2d", "mindspore.nn.Conv3d", "mindspore.nn.Conv1dTranspose", "mindspore.nn.Conv2dTranspose", "mindspore.nn.Conv3dTranspose", "mindspore.nn.CosineEmbeddingLoss", "mindspore.nn.CrossEntropyLoss", "mindspore.nn.Dropout", "mindspore.ops.dropout", "mindspore.nn.FractionalMaxPool2d", "mindspore.nn.GELU", "mindspore.nn.GRU", "mindspore.nn.GroupNorm", "mindspore.nn.HShrink", "mindspore.nn.L1Loss", "mindspore.nn.LPPool1d", "mindspore.nn.LPPool2d", "mindspore.nn.LSTM", "mindspore.nn.LSTMCell", "mindspore.nn.LayerNorm", "mindspore.nn.LeakyReLU", "mindspore.nn.Dense", "mindspore.nn.MSELoss", "mindspore.nn.MarginRankingLoss", "mindspore.nn.MaxPool1d", "mindspore.nn.MaxPool2d", "mindspore.nn.MaxPool3d", "mindspore.nn.NLLLoss", "mindspore.nn.PReLU", "mindspore.nn.PixelShuffle", "mindspore.nn.PixelUnshuffle", "mindspore.nn.ReLU", "mindspore.nn.SequentialCell", "mindspore.nn.Sigmoid", "mindspore.nn.SmoothL1Loss", "mindspore.nn.Softmax", "mindspore.nn.SoftShrink", "mindspore.nn.Tanh", "mindspore.nn.Unfold", "mindspore.nn.BCELoss", "mindspore.ops.elu", "mindspore.ops.interpolate", "mindspore.nn.L1Loss", "mindspore.ops.log_softmax", "mindspore.ops.lp_pool1d", "mindspore.ops.lp_pool2d", "mindspore.ops.margin_ranking_loss", "mindspore.ops.max_unpool1d", "mindspore.ops.max_unpool2d", "mindspore.ops.max_unpool3d", "mindspore.nn.MSELoss", "mindspore.ops.pad", "mindspore.ops.pixel_shuffle", "mindspore.ops.pixel_unshuffle", "mindspore.ops.relu", "mindspore.nn.SoftMarginLoss", "mindspore.ops.softmax", "mindspore.Tensor.is_signed", "mindspore.Tensor.logsumexp", "mindspore.Tensor.norm", "mindspore.Tensor.repeat_interleave", "mindspore.Tensor.reshape_as", "mindspore.Tensor.rot90", "mindspore.ops.tensor_scatter_elements", "mindspore.ops.tensor_scatter_elements", "mindspore.Tensor.short", "mindspore.Tensor.t", "mindspore.nn.cosine_decay_lr", "mindspore.nn.exponential_decay_lr", "mindspore.nn.piecewise_constant_lr", "mindspore.nn.piecewise_constant_lr", "None", "mindspore.dataset.GeneratorDataset", "mindspore.dataset.DistributedSampler", "mindspore.dataset.RandomSampler", "mindspore.dataset.SequentialSampler", "mindspore.dataset.SubsetRandomSampler", "mindspore.dataset.WeightedRandomSampler", "mindspore.numpy.arange", "mindspore.ops.argmax", "mindspore.numpy.bincount", "mindspore.ops.BroadcastTo", "mindspore.ops.Split", "mindspore.numpy.diagflat", "mindspore.numpy.diagonal", "mindspore.ops.tensor_dot", "mindspore.ops.ReverseV2", "mindspore.ops.FloorDiv", "mindspore.ops.Mod", "mindspore.Tensor.from_numpy", "mindspore.ops.GatherD", "mindspore.ops.GreaterEqual", "mindspore.ops.Greater", "mindspore.numpy.hamming", "mindspore.ops.HistogramFixedWidth", "mindspore.ops.Imag", "mindspore.ops.IsFinite", "mindspore.ops.IsInf", "mindspore.ops.IsNan", "mindspore.ops.LessEqual", "mindspore.ops.Lerp", "mindspore.ops.LinSpace", "mindspore.load_checkpoint", "mindspore.ops.Log1p", "mindspore.numpy.log2", "mindspore.ops.LogicalAnd", "mindspore.numpy.logical_not", "mindspore.ops.LogicalOr", "mindspore.numpy.logical_xor", "mindspore.numpy.logspace", "mindspore.ops.Less", "mindspore.numpy.matrix_power", "mindspore.ops.Rank", "mindspore.ops.Maximum", "mindspore.ops.ReduceMean", "mindspore.ops.Meshgrid", "mindspore.ops.MatMul", "mindspore.ops.Mul", "mindspore.ops.multinomial", "mindspore.ops.NotEqual", "mindspore.ops.Neg", "mindspore.ops.Size", "mindspore.ops.OnesLike", "mindspore.ops.random_poisson", "mindspore.ops.Pow", "mindspore.ops.ReduceProd", "mindspore.numpy.promote_types", "mindspore.ops.UniformReal", "mindspore.ops.UniformReal", "mindspore.ops.UniformInt", "mindspore.ops.UniformInt", "mindspore.ops.StandardNormal", "mindspore.ops.Randperm", "mindspore.numpy.remainder", "mindspore.ops.Reshape", "mindspore.numpy.result_type", "mindspore.numpy.rot90", "mindspore.ops.Rint", "mindspore.ops.Rsqrt", "mindspore.save_checkpoint", "mindspore.ops.Sigmoid", "mindspore.ops.Sin", "mindspore.ops.Sinh", "mindspore.ops.Sort", "mindspore.SparseTensor", "mindspore.ops.Split", "mindspore.ops.Square", "mindspore.ops.Squeeze", "mindspore.Tensor.std", "mindspore.ops.ReduceMean", "mindspore.Tensor.take", "mindspore.ops.Tan", "mindspore.ops.Tanh", "mindspore.Tensor", "mindspore.Tensor", "mindspore.numpy.tensordot", "mindspore.Tensor.trace", "mindspore.Tensor.transpose", "mindspore.numpy.trapz", "mindspore.numpy.tril_indices", "mindspore.numpy.triu_indices", "mindspore.numpy.trunc", "mindspore.ops.Unique", "mindspore.Tensor.var", "mindspore.numpy.where", "mindspore.ops.ZerosLike", "mindspore.grad", "mindspore.ops.stop_gradient", "mindspore.grad", "mindspore.ops.stop_gradient", "mindspore.Parameter", "mindspore.communication.get_group_size", "mindspore.set_context", "mindspore.ops.Gamma", "mindspore.nn.Embedding", "mindspore.nn.Flatten", "mindspore.nn.FastGelu", "mindspore.ops.KLDivLoss", "mindspore.ops.MaxPool3D", "mindspore.nn.transformer.MultiHeadAttention", "mindspore.nn.Cell", "mindspore.nn.Cell.insert_child_to_cell", "mindspore.nn.Cell.untrainable_params", "mindspore.nn.Cell.cells", "mindspore.load_param_into_net", "mindspore.nn.Cell.name_cells", "mindspore.nn.Cell.cells_and_names", "mindspore.nn.Cell.get_parameters", "mindspore.nn.Cell.parameters_dict", "mindspore.nn.Cell.set_train", "mindspore.nn.CellList", "mindspore.Parameter", "mindspore.ParameterTuple", "mindspore.nn.Pad", "mindspore.ops.SeLU", "mindspore.nn.SyncBatchNorm", "mindspore.nn.transformer.Transformer", "mindspore.nn.transformer.TransformerEncoder", "mindspore.nn.transformer.TransformerDecoder", "mindspore.nn.transformer.TransformerEncoderLayer", "mindspore.nn.transformer.TransformerDecoderLayer", "mindspore.nn.ResizeBilinear", "mindspore.nn.AdaptiveAvgPool2d", "mindspore.nn.AvgPool1d", "mindspore.ops.AvgPool", "mindspore.ops.AvgPool3D", "mindspore.ops.BatchNorm", "mindspore.ops.Conv2D", "mindspore.nn.CosineEmbeddingLoss", "mindspore.ops.CTCLoss", "mindspore.ops.KLDivLoss", "mindspore.ops.LayerNorm", "mindspore.nn.LeakyReLU", "mindspore.ops.L2Normalize", "mindspore.ops.OneHot", "mindspore.nn.SmoothL1Loss", "mindspore.ops.Softplus", "mindspore.ops.Softsign", "mindspore.common.initializer.Constant", "mindspore.common.initializer.HeNormal", "mindspore.common.initializer.HeUniform", "mindspore.common.initializer.Normal", "mindspore.common.initializer.One", "mindspore.common.initializer.XavierUniform", "mindspore.common.initializer.Zero", "mindspore.common.initializer.Uniform", "mindspore.ops.ApplyAdadelta", "mindspore.nn.Adagrad", "mindspore.nn.Adam", "mindspore.ops.ApplyAdaMax", "mindspore.nn.AdamWeightDecay", "mindspore.nn.Optimizer", "mindspore.nn.TrainOneStepCell", "mindspore.nn.RMSProp", "mindspore.nn.SGD", "mindspore.ops.repeat_elements", "mindspore.Tensor.all", "mindspore.Tensor.any", "mindspore.ops.Minimum", "mindspore.Tensor.abs", "mindspore.Tensor.argmax", "mindspore.Tensor.argmin", "mindspore.ops.Split", "mindspore.Tensor.copy", "mindspore.set_context", "mindspore.Tensor.cumsum", "mindspore.Tensor.diagonal", "mindspore.Tensor.dtype", "mindspore.ops.BroadcastTo", "mindspore.Tensor.expand_as", "mindspore.ops.Fill", "mindspore.Tensor.flatten", "mindspore.ops.Cast", "mindspore.ops.InplaceAdd", "mindspore.Tensor.item", "mindspore.Tensor.max", "mindspore.Tensor.mean", "mindspore.Tensor.min", "mindspore.ops.MatMul", "mindspore.ops.Mul", "mindspore.Tensor.ndim", "mindspore.numpy.full", "mindspore.ops.Zeros", "mindspore.Tensor.asnumpy", "mindspore.ops.Pow", "mindspore.ops.Transpose", "mindspore.numpy.tile", "mindspore.Parameter.requires_grad", "mindspore.Tensor.reshape", "mindspore.Tensor.resize", "mindspore.ops.Round", "mindspore.ops.ScatterNdAdd", "mindspore.nn.Sigmoid", "mindspore.Tensor.shape", "mindspore.ops.Sqrt", "mindspore.Tensor.strides", "mindspore.Tensor.squeeze", "mindspore.ops.Sub", "mindspore.Tensor.sum", "mindspore.ops.Transpose", "mindspore.Tensor.T", "mindspore.Tensor.transpose", "mindspore.ops.ExpandDims", "mindspore.Tensor.view", "mindspore.Tensor.view", "mindspore.ops.ZerosLike", "mindspore.dataset.text.RegexReplace", "mindspore.dataset.text.SentencePieceVocab", "mindspore.dataset.text.Lookup", "mindspore.dataset.text.SentencePieceTokenizer", "mindspore.dataset.text.SentencePieceTokenizer", "mindspore.dataset.text.WhitespaceTokenizer", "mindspore.dataset.text.Ngram", "mindspore.dataset.CelebADataset", "mindspore.dataset.Cifar10Dataset", "mindspore.dataset.Cifar100Dataset", "mindspore.dataset.CocoDataset", "mindspore.dataset.ImageFolderDataset", "mindspore.dataset.MnistDataset", "mindspore.dataset.VOCDataset", "mindspore.dataset.VOCDataset", "mindspore.ops.NMSWithMask", "mindspore.ops.ROIAlign", "mindspore.dataset.vision.CenterCrop", "mindspore.dataset.vision.RandomColorAdjust", "mindspore.dataset.transforms.Compose", "mindspore.dataset.transforms.TypeCast", "mindspore.dataset.vision.FiveCrop", "mindspore.dataset.vision.GaussianBlur", "mindspore.dataset.vision.Grayscale", "mindspore.dataset.vision.LinearTransformation", "mindspore.dataset.vision.Normalize", "mindspore.dataset.vision.Pad", "mindspore.dataset.vision.RandomAffine", "mindspore.dataset.transforms.RandomApply", "mindspore.dataset.transforms.RandomChoice", "mindspore.dataset.vision.RandomCrop", "mindspore.dataset.vision.RandomErasing", "mindspore.dataset.vision.RandomGrayscale", "mindspore.dataset.vision.RandomHorizontalFlip", "mindspore.dataset.transforms.RandomOrder", "mindspore.dataset.vision.RandomPerspective", "mindspore.dataset.vision.RandomPosterize", "mindspore.dataset.vision.RandomResizedCrop", "mindspore.dataset.vision.RandomRotation", "mindspore.dataset.vision.RandomSolarize", "mindspore.dataset.vision.RandomVerticalFlip", "mindspore.dataset.vision.Resize", "mindspore.dataset.vision.TenCrop", "mindspore.dataset.vision.ToPIL", "mindspore.dataset.vision.ToTensor", "mindspore.ops.argmax", "mindspore.ops.argmin", "mindspore.ops.clip_by_value", "mindspore.ops.expand_dims", "mindspore.ops.eye", "mindspore.ops.fill", "mindspore.nn.Dense", "mindspore.nn.Momentum", "mindspore.nn.ProximalAdagrad", "mindspore.nn.RMSProp", "mindspore.nn.exponential_decay_lr", "mindspore.nn.CosineDecayLR", "mindspore.dataset.GeneratorDataset.apply", "mindspore.dataset.GeneratorDataset.batch", "mindspore.dataset.GeneratorDataset.concat", "mindspore.dataset.GeneratorDataset.filter", "mindspore.dataset.GeneratorDataset.flat_map", "mindspore.dataset.GeneratorDataset", "mindspore.dataset.NumpySlicesDataset", "mindspore.dataset.GeneratorDataset.map", "mindspore.dataset.config.set_prefetch_size", "mindspore.dataset.GeneratorDataset.repeat", "mindspore.dataset.GeneratorDataset.shuffle", "mindspore.dataset.GeneratorDataset.skip", "mindspore.dataset.GeneratorDataset.take", "mindspore.dataset.GeneratorDataset.zip", "mindspore.dataset.TextFileDataset", "mindspore.dataset.TFRecordDataset", "mindspore.dataset.GeneratorDataset.bucket_batch_by_length", "mindspore.dataset.CSVDataset", "mindspore.ops.add", "mindspore.ops.cumsum", "mindspore.ops.div", "mindspore.ops.erf", "mindspore.dataset.vision.CenterCrop", "mindspore.dataset.transforms.TypeCast", "mindspore.dataset.vision.Crop", "mindspore.dataset.vision.HorizontalFlip", "mindspore.dataset.vision.VerticalFlip", "mindspore.dataset.vision.ConvertColor", "mindspore.dataset.vision.HsvToRgb", "mindspore.dataset.vision.Pad", "mindspore.dataset.vision.Normalize", "mindspore.dataset.vision.RandomCrop", "mindspore.dataset.vision.RandomHorizontalFlip", "mindspore.dataset.vision.RandomVerticalFlip", "mindspore.dataset.vision.Inter", "mindspore.dataset.vision.Resize", "mindspore.dataset.vision.ConvertColor", "mindspore.dataset.vision.Rotate", "mindspore.nn.SSIM", "mindspore.nn.LSTM", "mindspore.nn.LayerNorm", "mindspore.nn.PReLU", "mindspore.nn.SGD", "mindspore.nn.BatchNorm2d", "mindspore.nn.Adagrad", "mindspore.nn.Adam", "mindspore.nn.FTRL", "mindspore.nn.AvgPool2d", "mindspore.ops.bias_add", "mindspore.nn.Conv2d", "mindspore.nn.Conv2dTranspose", "mindspore.ops.CTCLoss", "mindspore.ops.dropout", "mindspore.ops.elu", "mindspore.nn.LeakyReLU", "mindspore.nn.MaxPool2d", "mindspore.nn.Moments", "mindspore.nn.ReLU", "mindspore.nn.Softmax", "mindspore.nn.SoftmaxCrossEntropyWithLogits", "mindspore.ops.Gather", "mindspore.grad", "mindspore.ops.OnesLike", "mindspore.nn.Pad", "mindspore.ops.Print", "mindspore.Tensor.repeat", "mindspore.ops.Reshape", "mindspore.Tensor.reshape", "mindspore.ops.Shape", "mindspore.ops.Size", "mindspore.ops.Slice", "mindspore.Tensor.squeeze", "mindspore.ops.stop_gradient", "mindspore.Tensor", "mindspore.ops.Tile", "mindspore.ops.Transpose", "mindspore.ops.ZerosLike", "mindspore.Tensor.argmax", "mindspore.Tensor.argmin", "mindspore.dataset.vision.Decode", "mindspore.train.Model", "mindspore.train.Model.train", "mindspore.train.Model.train", "mindspore.train.Model.train", "mindspore.train.Model.train", "mindspore.train.Model.eval", "mindspore.train.Model.eval", "mindspore.train.Model.eval", "mindspore.train.Model.eval", "mindspore.ops.batch_dot", "mindspore.ops.dot", "mindspore.dataset.Cifar10Dataset", "mindspore.dataset.Cifar100Dataset", "mindspore.dataset.FashionMnistDataset", "mindspore.dataset.IMDBDataset", "mindspore.dataset.MnistDataset", "mindspore.common.initializer.Constant", "mindspore.common.initializer.One", "mindspore.common.initializer.Normal", "mindspore.common.initializer.Uniform", "mindspore.common.initializer.TruncatedNormal", "mindspore.common.initializer.XavierUniform", "mindspore.common.initializer.Zero", "mindspore.nn.Embedding", "mindspore.nn.Flatten", "mindspore.ops.DynamicRNN", "mindspore.train.Accuracy", "mindspore.train.Accuracy", "mindspore.train.Accuracy", "mindspore.train.Accuracy", "mindspore.train.Accuracy", "mindspore.train.Accuracy", "mindspore.train.Accuracy", "mindspore.train.Accuracy", "mindspore.train.Accuracy", "mindspore.train.Accuracy", "mindspore.train.auc", "mindspore.train.CosineSimilarity", "mindspore.train.Loss", "mindspore.train.MAE", "mindspore.train.MSE", "mindspore.train.Precision", "mindspore.train.Recall", "mindspore.dataset.vision.RandomRotation", "mindspore.dataset.vision.RandomAffine", "mindspore.dataset.vision.RandomAffine", "mindspore.ops.MatMul", "mindspore.ops.Greater", "mindspore.ops.LessEqual", "mindspore.ops.Log", "mindspore.ops.Mul", "mindspore.ops.Pow", "mindspore.Tensor.std", "mindspore.Tensor.sum", "mindspore.Tensor.var", "mindspore.nn.Sigmoid", "mindspore.ops.Sub", "mindspore.ops.IOU", "mindspore.ops.BatchNorm", "mindspore.ops.L2Loss", "mindspore.ops.L2Normalize", "mindspore.ops.MaxPoolWithArgmax", "mindspore.ops.SeLU", "mindspore.ops.SigmoidCrossEntropyWithLogits", "mindspore.ops.Gamma", "mindspore.ops.uniform", "mindspore.SparseTensor", "mindspore.nn.probability.bijector.Softplus"}));

        for (Map.Entry<String, List<OperatorRecord>> single: operatorMap.entrySet()){
            single.getValue().forEach(record ->{
                if (white.contains(record.getMindSporeOperator())) {
                    record.setInWhiteList(true);
                }
            });
        }
    }

    @Override
    public List<OperatorRecord> assemble(List<String> topResults, String input, int count) {
        List<OperatorRecord> result = new ArrayList<>();
        for (String topResult : topResults) {
            for (String operator : nodeMap.get(topResult).stream().sorted().collect(Collectors.toList())) {
                if (result.size() >= count) {
                    return result;
                }
                result.addAll(operatorMap.get(operator));
            }
        }
        return result;
    }

    @Override
    public Set<String> searchable() {
        return nodeMap.keySet();
    }

    private void suffixStringSplit(String key) {
        String[] keyNode = key.toLowerCase(Locale.ENGLISH).split("\\.");
        StringBuilder sb = new StringBuilder();
        for (int i = keyNode.length - 1; i >= 0; i--) {
            sb.insert(0, keyNode[i]);
            List<String> operators = nodeMap.getOrDefault(sb.toString(), new ArrayList<>());
            operators.add(key);
            nodeMap.put(sb.toString(), operators);
            sb.insert(0, ".");
        }
    }

    /**
     * 处理md数据，获取api对应api和api对应url
     *
     * @param mdString md数据
     * @param apiType type
     */
    private void mdStringList(String mdString, ApiType apiType) {
        MutableDataSet options = new MutableDataSet();
        options.setFrom(ParserEmulationProfile.MARKDOWN);
        options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create()));
        Parser parser = Parser.builder(options).build();
        Node document = parser.parse(mdString);
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        document.getChildIterator().forEachRemaining(paragraphNode -> {
            if (!(paragraphNode instanceof TableBlock)) {
                return;
            }
            final String[] versionTitle = new String[1];
            paragraphNode.getChildIterator().forEachRemaining(nodeBlock -> {
                if (nodeBlock instanceof TableBody) {
                    nodeBlock.getChildIterator().forEachRemaining(nodeBody -> {
                        if (nodeBody instanceof TableRow) {
                            List<Node> nodeList = new ArrayList<>();
                            nodeBody.getChildIterator().forEachRemaining(nodeList::add);
                            if (nodeList.size() == 3) {
                                Node nodePytorch = nodeList.get(0);
                                Node nodeMindSpore = nodeList.get(1);
                                Node nodeDescription = nodeList.get(2);
                                List<LinkInfo> linkInfoPytorch = getListData(nodePytorch);
                                List<LinkInfo> linkInfoMindSpore = getListData(nodeMindSpore);
                                LinkInfo linkInfoDescription = getListDescriptionData(nodeDescription);
                                List<Runnable> runnable = setMapData(linkInfoPytorch, linkInfoMindSpore, linkInfoDescription, apiType,
                                        versionTitle);
                                for (Runnable single: runnable) {
                                    futures.add(CompletableFuture.runAsync(single, executorService));
                                }
                            }
                        }
                    });
                } else if (nodeBlock instanceof TableHead) {
                    nodeBlock.getChildIterator().forEachRemaining(nodeHead -> {
                        if (nodeHead instanceof TableRow) {
                            TableRow tableRow = (TableRow) nodeHead;
                            List<Node> nodeList = new ArrayList<>();
                            tableRow.getChildIterator().forEachRemaining(nodeList::add);
                            if (nodeList.size() == 3 && tableRow.getFirstChild() != null) {
                                BasedSequence basedSequence = tableRow.getFirstChild().getChars();
                                String title = basedSequence.toString().replaceAll("\\|", "").trim();
                                versionTitle[0] = title;
                            }
                        }
                    });
                }
            });
        });
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private List<LinkInfo> getListData(Node nodeRow) {
        List<LinkInfo> list = new ArrayList<>();
        nodeRow.getChildIterator().forEachRemaining(nodeCell -> {
            if (nodeCell instanceof Link) {
                Link link = (Link) nodeCell;
                String text = link.getText().toString();
                String url = link.getUrl().toString();
                LinkInfo linkInfo = new LinkInfo(text, url);
                list.add(linkInfo);
            } else {
                String text = nodeCell.getChars().toString();
                if (!text.equals("<br>") && !text.equals("")) {
                    String url = "";
                    LinkInfo linkInfo = new LinkInfo(text, url);
                    list.add(linkInfo);
                }
            }
        });
        return list;
    }

    private LinkInfo getListDescriptionData(Node nodeRow) {
        final String[] descriptionText = {""};
        final String[] descriptionUrl = {""};
        nodeRow.getChildIterator().forEachRemaining(nodeCell -> {
            if (nodeCell instanceof Link) {
                Link link = (Link) nodeCell;
                descriptionText[0] = descriptionText[0] + link.getText().toString();
                descriptionUrl[0] = link.getUrl().toString();
            } else {
                String text = nodeCell.getChars().toString();
                descriptionText[0] = descriptionText[0] + text;
            }
        });
        return new LinkInfo(descriptionText[0], descriptionUrl[0]);
    }

    private List<Runnable> setMapData(List<LinkInfo> linkInfoPytorch, List<LinkInfo> linkInfoMindSpore,
                            LinkInfo linkInfoDescription, ApiType apiType, String[] versionTitle) {
        List<Runnable> runnable = new ArrayList<>();
        for (LinkInfo linkInfo : linkInfoPytorch){
            for(LinkInfo linkInfo1 :linkInfoMindSpore) {
                OperatorRecord operatorRecord = new OperatorRecord();
                operatorRecord.setVersionText(versionTitle[0])
                        .setApiType(apiType)
                        .setOriginalOperator(linkInfo.getText())
                        .setOriginalLink(linkInfo.getUrl())
                        .setMindSporeOperator(linkInfo1.getText())
                        .setMindSporeLink(linkInfo1.getUrl())
                        .setDescription(linkInfoDescription.getText())
                        .setDescriptionLink(linkInfoDescription.getUrl());

                operatorMap.computeIfAbsent(linkInfo.getText(), (key) -> new ArrayList<>()).add(operatorRecord);
                runnable.add(() ->operatorRecord.setPlatform(getPlatformInfo(linkInfo1)));
            }
        }
        return runnable;
    }

    @Override
    public List<OperatorRecord> fetchAllMatch(String input) {
        return operatorMap.getOrDefault(input, new ArrayList<>());
    }

    private String getPlatformInfo(LinkInfo linkInfo) {
        String apiName = linkInfo.getText();
        List platformList = new ArrayList();
        if (!apiName.startsWith("mindspore.") || linkInfo.getUrl() == "") {
            return "";
        }
        try {
            String htmlText = HttpRequests.request(linkInfo.getUrl()).connectTimeout(5000).readString();
            int apiNameIndex = htmlText.indexOf("<dt class=\"sig sig-object py\" id=\"" + apiName);
            int platformIndex = htmlText.indexOf("支持平台", apiNameIndex);
            // md与html源码中得apiName不一致时，apiNameIndex为-1
            if (apiNameIndex > 0 && platformIndex > 0 && !htmlText.substring(apiNameIndex, platformIndex).contains("class=\"py ")) {
                String platformString = htmlText.substring(platformIndex, htmlText.indexOf("</dd>", platformIndex));
                for (String type : PLATFORM) {
                    if (platformString.contains(type)) {
                        platformList.add(type);
                    }
                }
                if (platformList.size() > 0) {
                    return StringUtil.join(platformList, ",");
                }
            } else {
                return "暂无数据";
            }
        } catch (Throwable ioException) {
        }
        return "";
    }
}