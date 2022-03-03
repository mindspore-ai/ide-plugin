package com.mindspore.ide.toolkit.wizard;

import com.mindspore.ide.toolkit.common.exceptions.MsToolKitException;

public interface ProjectService {
    String createCacheMindSporeDir(String parentDirPath) throws MsToolKitException;
}
