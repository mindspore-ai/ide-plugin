package com.mindspore.ide.toolkit.common.enums;


import com.mindspore.ide.toolkit.common.beans.NormalInfoConstants;

import java.util.Optional;

public enum EnumHardWarePlatform {
    CPU("CPU", NormalInfoConstants.MINDSPORE_CPU_DESCRIPTION),
    GPU("GPU", NormalInfoConstants.MINDSPORE_GPU_DESCRIPTION),
    ASCEND("ASCEND", NormalInfoConstants.MINDSPORE_ASCEND_DESCRIPTION),
    ;
    private String code;
    private String mindsporeMapping;

    EnumHardWarePlatform(String code, String mindsporeMapping) {
        this.code = code;
        this.mindsporeMapping = mindsporeMapping;
    }

    public String getCode() {
        return code;
    }

    public String getMindsporeMapping() {
        return mindsporeMapping;
    }

    public static Optional<EnumHardWarePlatform> findByCode(String code) {
        for (EnumHardWarePlatform val : EnumHardWarePlatform.values()) {
            if (val.getCode().equals(code)) {
                return Optional.of(val);
            }
        }
        return Optional.empty();
    }
}
