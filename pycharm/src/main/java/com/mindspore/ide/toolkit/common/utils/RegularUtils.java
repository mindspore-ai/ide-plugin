package com.mindspore.ide.toolkit.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularUtils {
    private static final String REGEX_CHINESE = "[^\\x00-\\xff]";

    private static final Pattern PATTERN = Pattern.compile(REGEX_CHINESE);

    public static String removeChinese(String code) {
        if (code == null) {
            return "";
        }
        Matcher mat = PATTERN.matcher(code);
        return mat.replaceAll("");
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0 || "null".equals(str);
    }

    /**
     * remove space at end of project path
     *
     * @param path project path
     * @return real file path
     */
    public static String normalizeFilePath(String path) {
        return path.stripTrailing();
    }
}

