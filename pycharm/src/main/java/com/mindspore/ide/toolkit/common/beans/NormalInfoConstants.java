package com.mindspore.ide.toolkit.common.beans;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;
import java.io.File;

public class NormalInfoConstants {
    public static final String MS_VERSION_INFO = File.separator + "jsons" + File.separator + "MSVersionInfo.json";
    public static final String MS_ICON_PATH = File.separator + "icons" + File.separator + "ms_16px.png";
    public static final Icon MS_ICON_12PX = IconLoader.getIcon("/icons/12px.svg", NormalInfoConstants.class);
    public static final Icon MS_ICON_13PX = IconLoader.getIcon("/icons/13px.svg", NormalInfoConstants.class);
    public static final Icon MS_ICON_16PX = IconLoader.getIcon("/icons/16px.svg", NormalInfoConstants.class);
    public static final String MS_DOWNLOAD_RL_PATH = File.separator + "download" + File.separator;
    public static final String MINDSPORE_CPU_DESCRIPTION = "mindspore";
    public static final String MINDSPORE_GPU_DESCRIPTION = "mindspore-gpu";
    public static final String MINDSPORE_ASCEND_DESCRIPTION = "mindspore-ascend";
}
