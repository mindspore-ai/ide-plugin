package com.mindspore.ide.toolkit.wizard;

public class OSInfoUtils {
    private static String OS = System.getProperty("os.name").toLowerCase();

    private static OSInfoUtils OSInfoUtils = new OSInfoUtils();

    private EPlatform platform;

    private OSInfoUtils() {
    }

    public static boolean isLinux() {
        return OS.indexOf("linux") >= 0;
    }

    public static boolean isMacOS() {
        return OS.indexOf("mac") >= 0 && OS.indexOf("os") > 0 && OS.indexOf("x") < 0;
    }

    public static boolean isMacOSX() {
        return OS.indexOf("mac") >= 0 && OS.indexOf("os") > 0 && OS.indexOf("x") > 0;
    }

    public static boolean isWindows() {
        return OS.indexOf("windows") >= 0;
    }

    public static boolean isOS2() {
        return OS.indexOf("os/2") >= 0;
    }

    public static boolean isSolaris() {
        return OS.indexOf("solaris") >= 0;
    }

    public static boolean isSunOS() {
        return OS.indexOf("sunos") >= 0;
    }

    public static boolean isMPEiX() {
        return OS.indexOf("mpe/ix") >= 0;
    }

    public static boolean isHPUX() {
        return OS.indexOf("hp-ux") >= 0;
    }

    public static boolean isAix() {
        return OS.indexOf("aix") >= 0;
    }

    public static boolean isOS390() {
        return OS.indexOf("os/390") >= 0;
    }

    public static boolean isFreeBSD() {
        return OS.indexOf("freebsd") >= 0;
    }

    public static boolean isIrix() {
        return OS.indexOf("irix") >= 0;
    }

    public static boolean isDigitalUnix() {
        return OS.indexOf("digital") >= 0 && OS.indexOf("unix") > 0;
    }

    public static boolean isNetWare() {
        return OS.indexOf("netware") >= 0;
    }

    public static boolean isOSF1() {
        return OS.indexOf("osf1") >= 0;
    }

    public static boolean isOpenVMS() {
        return OS.indexOf("openvms") >= 0;
    }

    /**
     * 获取操作系统名字
     *
     * @return 操作系统名
     */
    public static EPlatform getOSname() {
        if (isAix()) {
            OSInfoUtils.platform = EPlatform.AIX;
        } else if (isDigitalUnix()) {
            OSInfoUtils.platform = EPlatform.Digital_Unix;
        } else if (isFreeBSD()) {
            OSInfoUtils.platform = EPlatform.FreeBSD;
        } else if (isHPUX()) {
            OSInfoUtils.platform = EPlatform.HP_UX;
        } else if (isIrix()) {
            OSInfoUtils.platform = EPlatform.Irix;
        } else if (isLinux()) {
            OSInfoUtils.platform = EPlatform.Linux;
        } else if (isMacOS()) {
            OSInfoUtils.platform = EPlatform.Mac_OS;
        } else if (isMacOSX()) {
            OSInfoUtils.platform = EPlatform.Mac_OS_X;
        } else if (isMPEiX()) {
            OSInfoUtils.platform = EPlatform.MPEiX;
        } else if (isNetWare()) {
            OSInfoUtils.platform = EPlatform.NetWare_411;
        } else if (isOpenVMS()) {
            OSInfoUtils.platform = EPlatform.OpenVMS;
        } else if (isOS2()) {
            OSInfoUtils.platform = EPlatform.OS2;
        } else if (isOS390()) {
            OSInfoUtils.platform = EPlatform.OS390;
        } else if (isOSF1()) {
            OSInfoUtils.platform = EPlatform.OSF1;
        } else if (isSolaris()) {
            OSInfoUtils.platform = EPlatform.Solaris;
        } else if (isSunOS()) {
            OSInfoUtils.platform = EPlatform.SunOS;
        } else if (isWindows()) {
            OSInfoUtils.platform = EPlatform.Windows;
        } else {
            OSInfoUtils.platform = EPlatform.Others;
        }
        return OSInfoUtils.platform;
    }
}