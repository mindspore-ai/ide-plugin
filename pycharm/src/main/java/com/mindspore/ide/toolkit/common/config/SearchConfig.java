package com.mindspore.ide.toolkit.common.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchConfig implements Cloneable{
    private String ipAddress;

    private int port;

    private String urlBasePath;

    private String mdbSize;

    private String udbSize;

    @Override
    public SearchConfig clone() {
        try {
            SearchConfig clone = (SearchConfig) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
