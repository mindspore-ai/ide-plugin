package com.mindspore.ide.toolkit.common.utils;

import java.util.Locale;

public class OsUtils {
    public enum OsType {
        WINDOWS("win"), LINUX("nux"), OTHER("");
        private String desc;

        OsType(String desc) {
            this.desc = desc;
        }
    }

    public static OsType getOperatingSystemType() {
        String osDesc = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        OsType resType = OsType.OTHER;
        if (osDesc.indexOf(OsType.WINDOWS.desc) >= 0) {
            resType = OsType.WINDOWS;
        } else if (osDesc.indexOf(OsType.LINUX.desc) >= 0) {
            resType = OsType.LINUX;
        }
        return resType;
    }

    public static String getDescriptionOPfCurrentOperatingSystem() {
        return getOperatingSystemType().desc;
    }

    public static String getOsType(){
        return "";
    }
}
