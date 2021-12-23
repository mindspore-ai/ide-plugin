package com.mindspore.ide.toolkit.common.enums;

public enum EnumError {
    READ_CONFIGURATION_ERROR("ERROR_000001", "read configuration error, file path: {}"),
    IO_EXCEPTION("ERROR_000002", "io Exception!"),
    CREATE_CACHE_DIR_FAIL("ERROR_000003", "create cache dir fail."),
    NULL_PROJECT("ERROR_000004", "project is null"),
    FILE_CREATE_FAIL("ERROR_000005", "file create fail, expected file path:"),
    ;
    private String errCode;
    private String errMsg;

    EnumError(String errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public String getErrCode() {
        return errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }
}
