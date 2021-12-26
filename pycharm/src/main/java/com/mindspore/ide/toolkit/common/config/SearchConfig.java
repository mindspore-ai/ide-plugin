package com.mindspore.ide.toolkit.common.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchConfig {
    private String ipAddress;

    private int port;

    private String urlBasePath;

    private String mdbSize;

    private String udbSize;
}
