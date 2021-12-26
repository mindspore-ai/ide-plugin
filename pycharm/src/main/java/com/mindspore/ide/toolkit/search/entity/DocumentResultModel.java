package com.mindspore.ide.toolkit.search.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DocumentResultModel {
    private List<DocumentValue> hits;

    private int offset;

    private int limit;

    private int nbHits;

    private boolean exhaustiveNbHits;

    private int processingTimeMs;

    private String getHeader;

    @Override
    public String toString() {
        return "DocumentResultModel{" +
                "hits=" + hits +
                ", offset=" + offset +
                ", limit=" + limit +
                ", nbHits=" + nbHits +
                ", exhaustiveNbHits=" + exhaustiveNbHits +
                ", processingTimeMs=" + processingTimeMs +
                ", getHeader='" + getHeader + '\'' +
                '}';
    }
}
