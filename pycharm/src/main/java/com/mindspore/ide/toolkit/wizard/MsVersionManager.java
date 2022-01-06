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

/**
 * ms version manager
 *
 * @since 1.0
 */
public enum MsVersionManager {
    INSTANCE;

    private final Logger LOG = LoggerFactory.getLogger(MsVersionManager.class);
    private HashMap<String, MSVersionInfo> mindSporeMap = new HashMap();
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
                map.put(msVersionInfo.getName(), msVersionInfo);
            }
        } catch (IOException | JsonParseException exception) {
            LOG.error("Error of reading from file : path is {}", jsonFile);
        }
    }

    /**
     * get hardware platform info
     *
     * @return ms version info
     */
    public HashSet<MSVersionInfo> hardwarePlatformInfo() {
        HashSet<MSVersionInfo> resList = new HashSet<>();
        mindSporeMap.forEach(
                (key, value) ->
                        value.getOsInfoList().forEach(
                                info -> {
                                    if (info.contains(curOsDesc)) {
                                        resList.add(value);
                                    }
                                }
                        )
        );
        return resList;
    }

    /**
     * get operating system info
     *
     * @param hardware hardware info
     * @return hashMap
     */
    public HashMap<String, String> operatingSystemInfo(@NotNull String hardware) {
        HashMap<String, String> resMap = new HashMap<>();
        mindSporeMap.get(hardware).getOsInfoList()
                .forEach(
                        value -> {
                            if (value.toLowerCase(Locale.ENGLISH).contains(curOsDesc)) {
                                resMap.put(value, "");
                            }
                        }
                );
        return resMap;
    }
}
