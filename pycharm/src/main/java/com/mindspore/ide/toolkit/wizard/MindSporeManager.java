package com.mindspore.ide.toolkit.wizard;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public enum MindSporeManager {
    INSTANCE;

    private ConcurrentMap<String, Object> managerMap = new ConcurrentHashMap<>();

    public <T> Optional<T> get(String key, Class<T> tClass) {
        Object manager = managerMap.get(key);
        if (manager == null) {
            return Optional.empty();
        }
        return Optional.of(tClass.cast(manager));
    }
}

