package com.mindspore.ide.toolkit.common.utils;

import java.util.regex.Pattern;

public class RegularUtils {
    private static final String REGEX = "[a-zA-Z0-9._]+";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    /**
     * 判断api内容
     *
     * @param str str
     * @return true or false
     */
    public static boolean isApi(String str) {
        return PATTERN.matcher(str).matches();
    }

    /**
     * 非空
     *
     * @param str str
     * @return true or false
     */
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

