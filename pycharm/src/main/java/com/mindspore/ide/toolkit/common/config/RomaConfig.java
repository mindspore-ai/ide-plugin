package com.mindspore.ide.toolkit.common.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RomaConfig implements Cloneable{

    private String token;

    private SearchResource searchResource;

    private String url;

    @Override
    public RomaConfig clone() {
        try {
            RomaConfig clone = (RomaConfig) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
