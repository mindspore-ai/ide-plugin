package com.mindspore.ide.toolkit.statusbar.utils;

import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.util.NlsSafe;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

public class InputVersionCheck implements InputValidator {
    private static final String REGEX = "^\\d+(\\.\\d+){1,2}$";

    @Override
    public boolean checkInput(@NlsSafe String inputString) {
        return true;
    }

    @Override
    public boolean canClose(@NlsSafe String inputString) {
        if (StringUtils.isEmpty(inputString)) {
            return false;
        }
        boolean result = inputString.matches(REGEX);
        if (!result) {
            return false;
        }
        List<String> versions = MindSporeVersionUtils.VERSION_LIST;
        String versionString = inputString;
        String[] versionArray = inputString.split("\\.");
        if (versionArray.length == 3) {
            versionString = String.join(".", versionArray);
        }
        if (versions.contains(versionString)) {
            return false;
        } else {
            return true;
        }
    }
}
