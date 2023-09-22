package com.mindspore.ide.toolkit.statusbar.utils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

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
        if (VERSION_LIST.contains(version)) {
            return;
        }
        VERSION_LIST.add(version);
    }

    public static String getBigVersion(String fullVersion) {
        String bigVersion = fullVersion;
        String[] versionArray = fullVersion.split("\\.");
        if (versionArray.length == 3) {
            bigVersion = Arrays.stream(versionArray).limit(2).collect(Collectors.joining("."));
        }
        return bigVersion;
    }
}
