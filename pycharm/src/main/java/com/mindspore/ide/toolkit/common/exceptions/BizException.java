package com.mindspore.ide.toolkit.common.exceptions;

public class BizException extends Exception{

    private static final long serialVersionUID = -5538457983467477957L;

    private String errorMessage;
    private String errorCode;

    public BizException(String errorMessage) {
        this(null, errorMessage);
    }

    public BizException(String errorCode, String errorMessage) {
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
