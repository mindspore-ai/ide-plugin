package com.mindspore.ide.toolkit.common.exceptions;

import com.mindspore.ide.toolkit.common.enums.EnumError;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class MsToolKitException extends Exception {
    private String errMsg;
    private String errCode;
    private String solution;

    public MsToolKitException(String errMsg) {
        super(errMsg);
    }

    public MsToolKitException(@NotNull EnumError enumError) {
        super(enumError.getErrorMessage());
        this.errMsg = enumError.getErrorMessage();
        this.errCode = enumError.getErrorCode();
        this.solution = enumError.getSolution();
    }
}
