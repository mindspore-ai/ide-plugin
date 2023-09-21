package com.mindspore.ide.toolkit.search;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import com.mindspore.ide.toolkit.common.utils.GsonUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取ms版本列表
 *
 * @since 2022-08-10
 */
public class MsVersionDataConfig {
    private static final String RESOURCE_FILE = "/jsons/MsVersionData.json";

    private static final Logger LOG = LoggerFactory.getLogger(MsVersionDataConfig.class);

    private MsVersionDataConfig() {
    }

    /**
     * 单例静态内部类
     *
     * @return MsVersionDataConfig
     */
    public static MsVersionDataConfig getInstance() {
        return SingleMsVersionDataConfig.INSTANCE;
    }

    /**
     * 读取本地json获取ms版本列表
     *
     * @return ms版本列表
     */
    public List<MsVersionData> parseVersionJsonFile() {
        try (InputStream inputStream = MsVersionDataConfig.class.getResourceAsStream(MsVersionDataConfig.RESOURCE_FILE);
            InputStreamReader fileReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            JsonReader reader = new JsonReader(fileReader)) {
            Type collectionType = new TypeToken<List<MsVersionData>>() {}.getType();
            return GsonUtils.INSTANCE.getGson().fromJson(reader, collectionType);
        } catch (IOException | JsonParseException exception) {
            LOG.warn("Error of reading from file: path is {}", MsVersionDataConfig.RESOURCE_FILE);
        }
        return new ArrayList<>();
    }

    private static class SingleMsVersionDataConfig {
        private static final MsVersionDataConfig INSTANCE = new MsVersionDataConfig();
    }

    /**
     * MsVersionData
     *
     * @since 2022-08-10
     */
    @Getter
    @Setter
    @AllArgsConstructor
    public static class MsVersionData {
        private String mdVersion;

        private String pytorchMdUrl;

        private String tensorflowMdUrl;
    }

    public static MsVersionData newVersionData(String version) {
        String mdVersion = "r" + version;
        String pytorchMdUrl = "https://gitee.com/mindspore/docs/raw/" + mdVersion + "/docs/mindspore/source_zh_cn/note" +
                "/api_mapping/pytorch_api_mapping.md";
        String tensorflowMdUrl = "https://gitee.com/mindspore/docs/raw/" + mdVersion + "/docs/mindspore/source_zh_cn" +
                "/note/api_mapping/tensorflow_api_mapping.md";
        return new MsVersionData(mdVersion, pytorchMdUrl, tensorflowMdUrl);
    }
}
