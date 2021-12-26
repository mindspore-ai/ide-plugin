package com.mindspore.ide.toolkit.common.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResource {
    private String windows;

    private String linux;

    private String mac;
}
