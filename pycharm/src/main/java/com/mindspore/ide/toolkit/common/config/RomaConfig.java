package com.mindspore.ide.toolkit.common.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RomaConfig {

    private String token;

    private SearchResource searchResource;

    private String url;
}
