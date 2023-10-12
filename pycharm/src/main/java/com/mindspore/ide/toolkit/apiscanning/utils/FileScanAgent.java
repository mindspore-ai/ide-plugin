package com.mindspore.ide.toolkit.apiscanning.utils;

import com.intellij.openapi.util.text.Strings;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyFromImportStatement;
import com.jetbrains.python.psi.PyImportElement;
import com.jetbrains.python.psi.PyImportStatement;
import com.jetbrains.python.psi.PyImportStatementBase;
import com.jetbrains.python.psi.PyReferenceExpression;
import com.mindspore.ide.toolkit.common.utils.RegularUtils;
import com.mindspore.ide.toolkit.search.OperatorSearchService;
import com.mindspore.ide.toolkit.search.entity.LinkInfo;
import com.mindspore.ide.toolkit.search.entity.OperatorRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
public class FileScanAgent {
    private Map<String, String> importMap;

    private Map<String, String> fromMap;

    private Map<String, String> fromAsMap;

    private Set<String> apiNameList;

    private List<Object[]> apiNameNullList;
    private List<Object[]> straightApiMappingList;
    private List<Object[]> blurredApiMappingList;

    private static final String NO_PACKAGE_PLACEHOLDER = "<no package>";

    private static final List<String> TORCH_TENSOR = Arrays.asList("new_tensor", "new_full", "new_empty",
            "new_ones", "new_zeros", "is_cuda", "is_quantized", "is_meta", "device", "grad", "ndim", "T", "real", "imag", "abs",
            "abs_", "absolute", "absolute_", "acos", "acos_", "arccos", "arccos_", "add", "add_", "addbmm", "addbmm_", "addcdiv"
            , "addcdiv_", "addcmul", "addcmul_", "addmm", "addmm_", "sspaddmm", "addmv", "addmv_", "addr", "addr_", "allclose", "amax", "amin", "angle", "apply_", "argmax", "argmin", "argsort", "asin", "asin_", "arcsin", "arcsin_", "as_strided", "atan", "atan_", "arctan", "arctan_", "atan2", "atan2_", "all", "any", "backward", "baddbmm", "baddbmm_", "bernoulli", "bernoulli_", "bfloat16", "bincount", "bitwise_not", "bitwise_not_", "bitwise_and", "bitwise_and_", "bitwise_or", "bitwise_or_", "bitwise_xor", "bitwise_xor_", "bmm", "bool", "byte", "broadcast_to", "cauchy_", "ceil", "ceil_", "char", "cholesky", "cholesky_inverse", "cholesky_solve", "chunk", "clamp", "clamp_", "clip", "clip_", "clone", "contiguous", "copy_", "conj", "copysign", "copysign_", "cos", "cos_", "cosh", "cosh_", "count_nonzero", "acosh", "acosh_", "arccosh", "arccosh_", "cpu", "cross", "cuda", "logcumsumexp", "cummax", "cummin", "cumprod", "cumprod_", "cumsum", "cumsum_", "data_ptr", "deg2rad", "dequantize", "det", "dense_dim", "detach", "detach_", "diag", "diag_embed", "diagflat", "diagonal", "fill_diagonal_", "fmax", "fmin", "diff", "digamma", "digamma_", "dim", "dist", "div", "div_", "divide", "divide_", "dot", "double", "eig", "element_size", "eq", "eq_", "equal", "erf", "erf_", "erfc", "erfc_", "erfinv", "erfinv_", "exp", "exp_", "expm1", "expm1_", "expand", "expand_as", "exponential_", "fix", "fix_", "fill_", "flatten", "flip", "fliplr", "flipud", "float", "float_power", "float_power_", "floor", "floor_", "floor_divide", "floor_divide_", "fmod", "fmod_", "frac", "frac_", "gather", "gcd", "gcd_", "ge", "ge_", "greater_equal", "greater_equal_", "geometric_", "geqrf", "ger", "get_device", "gt", "gt_", "greater", "greater_", "half", "hardshrink", "heaviside", "histc", "hypot", "hypot_", "i0", "i0_", "igamma", "igamma_", "igammac", "igammac_", "index_add_", "index_add", "index_copy_", "index_copy", "index_fill_", "index_fill", "index_put_", "index_put", "index_select", "indices", "inner", "int", "int_repr", "inverse", "isclose", "isfinite", "isinf", "isposinf", "isneginf", "isnan", "is_contiguous", "is_complex", "is_floating_point", "is_leaf", "is_pinned", "is_set_to", "is_shared", "is_signed", "is_sparse", "istft", "isreal", "item", "kthvalue", "lcm", "lcm_", "ldexp", "ldexp_", "le", "le_", "less_equal", "less_equal_", "lerp", "lerp_", "lgamma", "lgamma_", "log", "log_", "logdet", "log10", "log10_", "log1p", "log1p_", "log2", "log2_", "log_normal_", "logaddexp", "logaddexp2", "logsumexp", "logical_and", "logical_and_", "logical_not", "logical_not_", "logical_or", "logical_or_", "logical_xor", "logical_xor_", "logit", "logit_", "long", "lstsq", "lt", "lt_", "less", "less_", "lu", "lu_solve", "as_subclass", "map_", "masked_scatter_", "masked_scatter", "masked_fill_", "masked_fill", "masked_select", "matmul", "matrix_power", "matrix_exp", "max", "maximum", "mean", "median", "nanmedian", "min", "minimum", "mm", "smm", "mode", "movedim", "moveaxis", "msort", "mul", "mul_", "multiply", "multiply_", "multinomial", "mv", "mvlgamma", "mvlgamma_", "nansum", "narrow", "narrow_copy", "ndimension", "nan_to_num", "nan_to_num_", "ne", "ne_", "not_equal", "not_equal_", "neg", "neg_", "negative", "negative_", "nelement", "nextafter", "nextafter_", "nonzero", "norm", "normal_", "numel", "numpy", "orgqr", "ormqr", "outer", "permute", "pin_memory", "pinverse", "polygamma", "polygamma_", "pow", "pow_", "prod", "put_", "qr", "qscheme", "quantile", "nanquantile", "q_scale", "q_zero_point", "q_per_channel_scales", "q_per_channel_zero_points", "q_per_channel_axis", "rad2deg", "random_", "ravel", "reciprocal", "reciprocal_", "record_stream", "register_hook", "remainder", "remainder_", "renorm", "renorm_", "repeat", "repeat_interleave", "requires_grad", "requires_grad_", "reshape", "reshape_as", "resize_", "resize_as_", "retain_grad", "roll", "rot90", "round", "round_", "rsqrt", "rsqrt_", "scatter", "scatter_", "scatter_add_", "scatter_add", "select", "set_", "share_memory_", "short", "sigmoid", "sigmoid_", "sign", "sign_", "signbit", "sgn", "sgn_", "sin", "sin_", "sinc", "sinc_", "sinh", "sinh_", "asinh", "asinh_", "arcsinh", "arcsinh_", "size", "slogdet", "solve", "sort", "split", "sparse_mask", "sparse_dim", "sqrt", "sqrt_", "square", "square_", "squeeze", "squeeze_", "std", "stft", "storage", "storage_offset", "storage_type", "stride", "sub", "sub_", "subtract", "subtract_", "sum", "sum_to_size", "svd", "swapaxes", "swapdims", "symeig", "t", "t_", "tensor_split", "tile", "to", "to_mkldnn", "take", "tan", "tan_", "tanh", "tanh_", "atanh", "atanh_", "arctanh", "arctanh_", "tolist", "topk", "to_sparse", "trace", "transpose", "transpose_", "triangular_solve", "tril", "tril_", "triu", "triu_", "true_divide", "true_divide_", "trunc", "trunc_", "type", "type_as", "unbind", "unfold", "uniform_", "unique", "unique_consecutive", "unsqueeze", "unsqueeze_", "values", "var", "vdot", "view", "view_as", "where", "xlogy", "xlogy_", "zero_");

    public FileScanAgent() {
        importMap = new HashMap<>();
        fromMap = new HashMap<>();
        fromAsMap = new HashMap<>();
        apiNameList = new LinkedHashSet<>();
    }
    public FileScanAgent(PsiFile psiFile) {
        this();
        scan(psiFile);
        assembleResultAndSearch();
    }
    public void scan(PsiFile psiFile) {
        // 保存import或from映射
        importMap = new HashMap<>();
        fromMap = new HashMap<>();
        fromAsMap = new HashMap<>();
        translateImport(psiFile, importMap, fromMap, fromAsMap);
        // 获取apiName
        translateCallExpression(psiFile);



    }

    public void assembleResultAndSearch() {
        // 过滤无效api，只含有字母数字点和下划线的认为是有效api
        // 对api和import进行拼接，并搜索内容
        Set<String> sortedApiList = sortList();
        straightApiMappingList = new ArrayList<>();
        blurredApiMappingList = new ArrayList<>();
        apiNameNullList = new ArrayList<>();
        searchData(sortedApiList);

//        blurredApiMappingList = sortListAnti(apiBlurredNameFiltering, null);
    }
    public void translateCallExpression(PsiElement psiElement) {
        List<String[]> formerFunction = new ArrayList<>();
        for (PsiElement element : psiElement.getChildren()) {
            if (element instanceof PyCallExpression) {
                PyCallExpression pyCallExpression = (PyCallExpression) element;
                String apiString = pyCallExpression.getCallee().getText();
                String prefix;
                String apiName;
                String[] attrList = apiString.split("\\.");
                List<String> resultList = new ArrayList<>();
                for (String single : attrList) {
                    if (!single.contains("(")) {
                        resultList.add(single);
                    } else {
                        resultList.add(attrList[attrList.length -1]);
                        break;
                    }
                }
                if (resultList.size() > 1) {
                    prefix = String.join(".",resultList.subList(0, resultList.size() - 1));
                    apiName = resultList.get(resultList.size() - 1);
                } else {
                    prefix = NO_PACKAGE_PLACEHOLDER;
                    apiName = resultList.get(0);
                }
                formerFunction.add(new String[] {prefix, apiName});
                translateCallExpressionInPyCallExpression(element, formerFunction);
            }
//            else if (element instanceof PyReferenceExpression && !MSPsiUtils.isPsiImport(element) && element.getChildren().length > 0 && !element.getText().toLowerCase().equals(element.getText())) {
//                apiNameList.add(element.getText());
//            }
            else {
                translateCallExpression(element);
            }
        }
    }

    /**
     * 获取文件中的import
     *
     * @param psiElement psiElement
     * @param importMap import xxx    or    import xxx as yyy
     * @param fromMap from xxx import yyy     or     from xxx import *
     * @param fromAsMap from xxx import yyy as zzz
     */
    public void translateImport(PsiElement psiElement, Map<String, String> importMap, Map<String, String> fromMap, Map<String, String> fromAsMap) {
        PyImportElement importElement;
        for (PsiElement element : psiElement.getChildren()) {
            try {
                if (element instanceof PyImportStatementBase) {
                    PyImportStatementBase importStatementBase = (PyImportStatementBase) element;
                    if (element instanceof PyImportStatement) {
                        // import mindspore.numpy as mnp; key是mindspore， value是mnp
                        // import mindspore; key是mindspore, value是null
                        importElement = importStatementBase.getImportElements()[0];
                        importMap.put(importElement.getImportedQName().toString(), importElement.getAsName());
                    } else if (element instanceof PyFromImportStatement) {
                        PyFromImportStatement pyFromImportStatement = (PyFromImportStatement) element;
                        if (pyFromImportStatement.getImportElements().length >= 1) {
                            if (pyFromImportStatement.getImportElements()[0].getAsName() == null) {
                                // from mindspore.numpy import nn; key是mindspore.numpy.nn, value是nn
                                for (PyImportElement pyImportElement : importStatementBase.getImportElements()) {
                                    fromMap.put(pyFromImportStatement.getImportSourceQName().toString() + "." + pyImportElement.getText(), pyImportElement.getText());
                                }
                            } else {
                                // from d2l import mindspore as dl; key是mindspore.d2l, value是dl
                                fromAsMap.put(importStatementBase.getFullyQualifiedObjectNames().get(0), importStatementBase.getImportElements()[0].getAsName());
                            }
                        } else {
                            // from mindspore import *; key是mindspore, value是null
                            fromMap.put(pyFromImportStatement.getImportSourceQName().toString(), null);
                        }
                    } else {
                        log.info("psiFile exceptions");
                    }
                } else {
                    translateImport(element, importMap, fromMap, fromAsMap);
                }
            } catch (Throwable ex) {
                log.debug("import analyse error", ex);
            }
        }
    }

    public Set<String> filteringData(Set<String> apiNameList) {
        Set<String> apiString = new LinkedHashSet<>();
        Iterator<String> it = apiNameList.iterator();
        while (it.hasNext()) {
            String str = it.next();
            if (str.startsWith("torch")) {
                apiString.add(str);
                it.remove();
            } else {
                String[] split = str.split("\\.");
                str = split[split.length - 1];
                if (TORCH_TENSOR.contains(str)) {
                    apiString.add(str);
                    it.remove();
                }
            }
        }
        return apiString;
    }

    public Set<String> filteringDataReverse(Set<String> apiNameList) {
        Set<String> apiString = new LinkedHashSet<>();
        Iterator<String> it = apiNameList.iterator();
        while (it.hasNext()) {
            String str = it.next();
            String[] api = str.split("\\.");
            if (api.length > 0 && "tensorflow".equals(api[0])) {
                str = str.replaceFirst("tensorflow", "tf");
            }
            String[] apiNew = str.split("\\.");
            if (!("tf".equals(apiNew[0]) || "torch".equals(apiNew[0]) || "torchtext".equals(apiNew[0]) ||
                    "torchvision".equals(apiNew[0]))) {
                apiString.add(str);
                it.remove();
            }
        }
        return apiString;
    }

    public void searchData(Set<String> apiStringSet) {
        List<Object[]> nonMatchApiList = new LinkedList<>();
        for (String str : apiStringSet) {
            boolean isTensor = false;
            if (TORCH_TENSOR.contains(str)) {
                isTensor = true;
                str = "torch.Tensor." + str;
            }
            if (!str.startsWith("torch")) {
                continue;
            }
            List<OperatorRecord> records = OperatorSearchService.INSTANCE.searchFullMatch(str);
            for (OperatorRecord record : records) {
                Object[] cells = new Object[5];
                if (Strings.isEmpty(record.getOriginalLink())) {
                    cells[0] = record.getOriginalLink();
                } else {
                    cells[0] = new LinkInfo(record.getOriginalOperator(), record.getOriginalLink());
                }
                cells[1] = record.getVersionText();
                if (Strings.isEmpty(record.getMindSporeLink())) {
                    cells[2] = record.getMindSporeOperator();
                } else {
                    cells[2] = new LinkInfo(record.getMindSporeOperator(), record.getMindSporeLink());
                }
                cells[3] = record.getPlatform();
                if (Strings.isEmpty(record.getDescriptionLink())) { // 注释
                    cells[4] =
                            record.getDescription() + (record.isInWhiteList() ? "" : "（仅支持2.0及以上版本MindSpore）");
                } else {
                    cells[4] =
                            new LinkInfo(record.getDescription(), record.getDescriptionLink(), !record.isInWhiteList());
                }
                if (!isTensor) {
                    straightApiMappingList.add(cells);
                } else {
                    blurredApiMappingList.add(cells);
                }
            }

            if (records.isEmpty()) {
                nonMatchApiList.add(new Object[]{str, isTensor?"可能是torch.Tensor的API":"", new LinkInfo("缺失API处理策略",
                        "https://www.mindspore.cn/docs/zh-CN/master/migration_guide/analysis_and_preparation.html" +
                                "#%E7%BC%BA%E5%A4%B1api%E5%A4%84%E7%90%86%E7%AD%96%E7%95%A5")});
            }
        }
        Objects.requireNonNullElse(apiNameNullList, straightApiMappingList).addAll(nonMatchApiList);
    }

    public void translateCallExpressionInPyCallExpression(PsiElement psiElement, List<String[]> formerFunction) {
        for (PsiElement element : psiElement.getChildren()) {
            if (element instanceof PyCallExpression) {
                PyCallExpression pyCallExpression = (PyCallExpression) element;
                String apiString = pyCallExpression.getCallee().getText();
                int lastDot = apiString.lastIndexOf('.');
                String prefix;
                String apiName;
                if (lastDot > 0 && apiString.length() > lastDot) {
                    prefix = apiString.substring(0, lastDot);
                    apiName = apiString.substring(lastDot + 1);
                } else {
                    prefix = NO_PACKAGE_PLACEHOLDER;
                    apiName = apiString;
                }
                formerFunction.add(new String[] {prefix, apiName});
                translateCallExpressionInPyCallExpression(element, formerFunction);
            } else if (element instanceof PyReferenceExpression && element.getChildren().length > 0 && !element.getText().toLowerCase().equals(element.getText())) {
                apiNameList.add(element.getText());
                translateCallExpressionInPyCallExpression(element, formerFunction);
            } else {
                translateCallExpressionInPyCallExpression(element, formerFunction);
            }
        }

        if (formerFunction.size() <= 0) {
            log.info("The length of the formerFunction is 0");
        } else {
            int last = formerFunction.size() - 1;
            String prefix = formerFunction.get(last)[0];
            formerFunction.forEach(record -> {
                if (NO_PACKAGE_PLACEHOLDER.equals(record[0])) {
                    apiNameList.add(record[1]);
                } else {
                    apiNameList.add(prefix + "." + record[1]);
                }
            });
        }
        formerFunction.clear();
    }


    public Set<String> sortList() {
        apiNameList = filteringApi(apiNameList);
        Set<String> apiString = filteringData(apiNameList);

        outerLoop : for (String str : apiNameList) {
            for (Map.Entry<String, String> entry : importMap.entrySet()) {
                String value = entry.getValue();
                String key = entry.getKey();
                if (value == null) {
                    String[] split = str.split("\\.");
                    if (split.length > 0) {
                        if (split[0].equals(key)) {
                            apiString.add(str);
                            continue outerLoop;
                        }
                    } else {
                        if (str.equals(key)) {
                            apiString.add(str);
                            continue outerLoop;
                        }
                    }
                } else {
                    String[] split = str.split("\\.");
                    if (split.length > 0) {
                        if (split[0].equals(value)) {
                            apiString.add(str.replaceFirst(value, key));
                            continue outerLoop;
                        }
                    } else {
                        if (str.equals(value)) {
                            apiString.add(str.replaceFirst(value, key));
                            continue outerLoop;
                        }
                    }
                }
            }
            for (Map.Entry<String, String> entry : fromMap.entrySet()) {
                String value = entry.getValue();
                String key = entry.getKey();
                if (value == null) {
                    apiString.add(key + "." + str);
                    continue outerLoop;
                } else {
                    String[] split = str.split("\\.");
                    if (split.length > 0) {
                        if (split[0].equals(value)) {
                            apiString.add(key + str.replaceFirst(split[0], ""));
                            continue outerLoop;

                        }
                    } else {
                        if (str.equals(value)) {
                            apiString.add(key);
                            continue outerLoop;

                        }
                    }
                }
            }
            for (Map.Entry<String, String> entry : fromAsMap.entrySet()) {
                String value = entry.getValue();
                String key = entry.getKey();
                String[] split = str.split("\\.");
                if (split.length > 0) {
                    if (split[0].equals(value)) {
                        apiString.add(key + str.replaceFirst(split[0], ""));
                        continue outerLoop;

                    }
                } else {
                    if (str.equals(value)) {
                        apiString.add(key);
                        continue outerLoop;

                    }
                }

            }

        }
        return apiString;
    }


    private Set<String>  blurSort(Set<String> apiString, Set<String> apiNameList) {
        Set<String> result = new LinkedHashSet<>();
        apiString.forEach((value) -> {
            String[] split = value.split("\\.");
            String last = split[split.length-1];
            result.add(last);
        });
        return result;
    }

    public Set<String> filteringApi(Set<String> apiNameList) {
        Set<String> stringSet = new LinkedHashSet<>();
        apiNameList.forEach(s -> {
            if (RegularUtils.isApi(s)) {
                stringSet.add(s);
            }
        });
        return stringSet;
    }

    public Object[][] apiArray(){
        return trans(straightApiMappingList);
    }

    public Object[][] papiArray(){
        return trans(blurredApiMappingList);
    }

    public Object[][] apiNullArray(){
        return trans(apiNameNullList);
    }


    public Object[][] trans(List<Object[]> data) {
        if (CollectionUtils.isEmpty(data)) {
            return new Object[][] {};
        }
        Object[][] objects = new Object[data.size()][data.get(0).length];
        for (int i = 0; i < data.size(); i++) {
            objects[i] = data.get(i);
        }
        return objects;
    }
}
