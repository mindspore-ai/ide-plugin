package com.mindspore.ide.toolkit.search.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentValue {
    private int id;

    private String title;

    private String path;

    private String url;

    private String file_type;

    private String content;

    @Override
    public String toString(){
        return title;
    }
}
