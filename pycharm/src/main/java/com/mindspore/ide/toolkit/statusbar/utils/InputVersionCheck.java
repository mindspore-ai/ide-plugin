package com.mindspore.ide.toolkit.statusbar.utils;

import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.util.NlsSafe;
import org.apache.commons.lang.StringUtils;

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

        List<String> versions = MindSporeVersionUtils.VERSION_LIST;
        if (versions.contains(inputString)) {
            return false;
        }

        return inputString.matches(REGEX);
    }
}
