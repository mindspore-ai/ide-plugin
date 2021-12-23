package com.mindspore.ide.toolkit.common.exceptions;

public class CommonException extends RuntimeException {

    private static final long serialVersionUID = 596918168928427615L;

    protected String errorMessage;
    protected String errorCode;

    public CommonException(String errorMessage) {
        this(null, errorMessage);
    }

    public CommonException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
