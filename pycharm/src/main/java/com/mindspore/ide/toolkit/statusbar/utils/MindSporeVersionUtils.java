package com.mindspore.ide.toolkit.statusbar.utils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MindSporeVersionUtils {
    public static final List<String> VERSION_LIST = new CopyOnWriteArrayList<>();

    public static void initVersionMap(String initVersion) {
        double startVersion = 1.9;
        double endVersion = Double.parseDouble(initVersion);
        for (double i = startVersion; i <= endVersion; i = i + 0.1) {
            VERSION_LIST.add(String.valueOf(i));
        }
    }

    public static void addVersion(String version) {
        VERSION_LIST.add(version);
    }
}
