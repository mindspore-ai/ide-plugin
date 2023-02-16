package com.mindspore.ide.toolkit.search.entity;

/**
 * xxx
 *
 * @since 2022-12-14
 */
public class LinkInfo {
    private String text;
    private String url;
    private boolean inBlackList;

    public LinkInfo(String text, String url) {
        this.text = text;
        this.url = url;
    }

    public LinkInfo(String text, String url, boolean inBlackList) {
        this(text, url);
        this.inBlackList = inBlackList;
    }

    public String getText() {
        return text;
    }

    public String getUrl() {
        return url;
    }

    public boolean isInBlackList() {
        return inBlackList;
    }

    public String getVersionString() {
        return isInBlackList() ? "（仅支持2.0及以上版本MindSpore）" : "";
    }

    @Override
    public String toString() {
        return String.format("{%s %s}", text, url);
    }
}
