package com.mindspore.ide.toolkit.common.utils;

import com.intellij.openapi.projectRoots.Sdk;
import com.mindspore.ide.toolkit.common.beans.PythonCommand;
import com.mindspore.ide.toolkit.common.beans.PythonPackageInfo;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MindSporeUtils {

    public static String createDownloadDirPath(String downloadUrl, String aimDir) {
        return aimDir + File.separator + "download" + File.separator + downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
    }

    public static boolean isPackageExist(@NotNull String packageName, @NotNull Sdk sdk) throws IOException {
        String result = RunExecUtils.runExec(
                Arrays.asList(sdk.getHomePath(),
                        PythonCommand.CHAR_MODULE,
                        PythonCommand.PIP,
                        PythonCommand.SHOW,
                        packageName));
        return StringUtils.isNotBlank(result) && result.contains("Home-page");
    }

    public static Optional<PythonPackageInfo> queryMindSporePackage(String sdkPath) {
        return queryPackageInfo(sdkPath, "mindspore(\\w|\\s|-)*[0,9]{1,3}.[0,9]{1,3}.[0,9]{1,3}");
    }

    public static Optional<PythonPackageInfo> queryPackageInfo(String sdkPath, String regEx) {
        ArrayList<String> cmdList = new ArrayList<>();
        cmdList.add(sdkPath);
        cmdList.add(PythonCommand.CHAR_MODULE);
        cmdList.add(PythonCommand.PIP);
        cmdList.add(PythonCommand.LIST);
        try {
            String info = RunExecUtils.runExec(cmdList);
            info = info.replaceAll("\\s+", " ");
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(info);
            if (matcher.find()) {
                String[] splits = matcher.group().split(" ");
                int minLength = 2;
                if (splits.length >= minLength) {
                    return Optional.of(new PythonPackageInfo(splits[0], splits[splits.length - 1]));
                }
            }
        } catch (IOException e) {
            return Optional.empty();
        }
        return Optional.empty();
    }
}


