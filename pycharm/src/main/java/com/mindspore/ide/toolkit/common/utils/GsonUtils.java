package com.mindspore.ide.toolkit.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public enum GsonUtils {
    INSTANCE;

    private Gson gson = new GsonBuilder().create();

    /**
     * get gson
     *
     * @return gson
     */
    public Gson getGson() {
        return gson;
    }
}
