package com.mindspore.ide.toolkit.statusbar.utils;

import com.mindspore.ide.toolkit.search.MdDataGet;
import com.mindspore.ide.toolkit.search.MdPathString;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MindSporeVersionUtils {
    private static final String PYTORCH_URL = "https://gitee.com/mindspore/docs/raw/%s/docs/mindspore/source_zh_cn/note/api_mapping/pytorch_api_mapping.md";

    public static final Map<String, String> VERSION_MARKDOWN_MAP = new ConcurrentHashMap<>();

    public static boolean getMdData4SpecifyVersion(String version) {
        String url = String.format(PYTORCH_URL, "r"+version);
        String mdData = MdDataGet.getInstance().getPytorchMdStr(url);
        if(StringUtils.isNotEmpty(mdData)) {
            VERSION_MARKDOWN_MAP.put(version, mdData);
            return true;
        }
        return false;
    }

    public static void initVersionMarkdownMap(String initVersion) {
        if (!MdDataGet.pytorchMdStr.isEmpty()) {
            VERSION_MARKDOWN_MAP.put(initVersion, MdDataGet.pytorchMdStr);
            return;
        }
        VERSION_MARKDOWN_MAP.put(initVersion, MdPathString.PYTORCH_MD_STR);
    }

    public static String getMdDataFromMap(@NotNull String version) {
        return VERSION_MARKDOWN_MAP.get(version);
    }
}
