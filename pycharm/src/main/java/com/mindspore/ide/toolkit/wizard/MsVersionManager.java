package com.mindspore.ide.toolkit.wizard;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import com.mindspore.ide.toolkit.common.utils.GsonUtils;
import com.mindspore.ide.toolkit.common.utils.OsUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;

public enum MsVersionManager {
    INSTANCE;

    private final Logger LOG = LoggerFactory.getLogger(MsVersionManager.class);
    private HashMap<String, List<OSInfo>> mindSporeMap = new HashMap();
    private String curOsDesc = OsUtils.getDescriptionOPfCurrentOperatingSystem();

    MsVersionManager() {
        parseJsonFile("/jsons/MSVersionInfo.json", mindSporeMap);
    }

    private void parseJsonFile(@NotNull String jsonFile, @NotNull HashMap map) {
        try (InputStream inputStream = MsVersionManager.class.getResourceAsStream(jsonFile);
             InputStreamReader fileReader = new InputStreamReader(inputStream, "UTF-8");
             JsonReader reader = new JsonReader(fileReader)) {
            Type collectionType = new TypeToken<Collection<MSVersionInfo>>() {
            }.getType();
            Collection<MSVersionInfo> msVersionInfos = GsonUtils.INSTANCE.getGson().fromJson(reader, collectionType);
            for (MSVersionInfo msVersionInfo : msVersionInfos) {
                map.put(msVersionInfo.getName(), msVersionInfo.getOsInfoList());
            }
        } catch (IOException | JsonParseException exception) {
            LOG.error("Error of reading from file : path is {}", jsonFile);
        }
    }

    public HashSet<String> hardwarePlatformInfo() {
        HashSet<String> resList = new HashSet<>();
        mindSporeMap.forEach(
                (key, value) ->
                        value.forEach(
                                info -> {
                                    if (info.getUrl().toLowerCase(Locale.ENGLISH).indexOf(curOsDesc) >= 0) {
                                        resList.add(key);
                                    }
                                }
                        )
        );
        return resList;
    }

    public HashMap<String, String> operatingSystemInfo(@NotNull String hardware) {
        HashMap<String, String> resMap = new HashMap<>();
        mindSporeMap.get(hardware)
                .forEach(
                        value -> {
                            if (value.getUrl().toLowerCase(Locale.ENGLISH).indexOf(curOsDesc) >= 0) {
                                resMap.put(value.getName(), value.getUrl());
                            }
                        }
                );
        return resMap;
    }
}
