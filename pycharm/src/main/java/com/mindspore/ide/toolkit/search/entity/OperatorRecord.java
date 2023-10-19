package com.mindspore.ide.toolkit.search.entity;

import com.intellij.openapi.application.ApplicationManager;
import com.mindspore.ide.toolkit.search.OperatorMapDataHub;

import java.util.concurrent.ExecutionException;

/**
 * entity for operator search value
 *
 * @since 2022-01-30
 */
public class OperatorRecord {
    /**
     * mindspore api
     */
    private String mindSporeOperator;

    /**
     * mindspore url
     */
    private String mindSporeLink;

    /**
     * mindspore platform
     */
    private String platform;

    /**
     * 版本号
     */
    private String versionText;

    /**
     * PyTorch/TensorFlow api
     */
    private String originalOperator;

    /**
     * PyTorch/TensorFlow url
     */
    private String originalLink;

    /**
     * 说明内容
     */
    private String description;

    /**
     * 说明url
     */
    private String descriptionLink;

    /**
     * PyTorch/TensorFlow type
     */
    private ApiType apiType;

    private boolean inWhiteList = false;

    public boolean isInWhiteList() {
        return inWhiteList;
    }

    public void setInWhiteList(boolean inWhiteList) {
        this.inWhiteList = inWhiteList;
    }

    public OperatorRecord setMindSporeOperator(String mindSporeOperator) {
        this.mindSporeOperator = mindSporeOperator;
        return this;
    }

    public OperatorRecord setMindSporeLink(String mindSporeLink) {
        this.mindSporeLink = mindSporeLink;
        return this;
    }

    public OperatorRecord setVersionText(String versionText) {
        this.versionText = versionText;
        return this;
    }

    public OperatorRecord setOriginalOperator(String originalOperator) {
        this.originalOperator = originalOperator;
        return this;
    }

    public OperatorRecord setOriginalLink(String originalLink) {
        this.originalLink = originalLink;
        return this;
    }

    public OperatorRecord setDescription(String description) {
        this.description = description;
        return this;
    }

    public OperatorRecord setDescriptionLink(String descriptionLink) {
        this.descriptionLink = descriptionLink;
        return this;
    }

    public OperatorRecord setApiType(ApiType apiType) {
        this.apiType = apiType;
        return this;
    }

    public OperatorRecord setPlatform(String platform) {
        this.platform = platform;
        return this;
    }

    public String getMindSporeOperator() {
        return mindSporeOperator;
    }

    public String getMindSporeLink() {
        return mindSporeLink;
    }

    public String getVersionText() {
        return versionText;
    }

    public String getOriginalOperator() {
        return originalOperator;
    }

    public String getOriginalLink() {
        return originalLink;
    }

    public String getDescription() {
        return description;
    }

    public String getDescriptionLink() {
        return descriptionLink;
    }

    public ApiType getApiType() {
        return apiType;
    }

    public String getShowText() {
        return originalOperator + " -> " + mindSporeOperator;
    }

    public String getPlatform() {
        if (platform == null || platform == "") {
            try {
                platform = ApplicationManager.getApplication().executeOnPooledThread(() ->
                OperatorMapDataHub.getPlatformInfo(new LinkInfo(mindSporeOperator, mindSporeLink))).get();
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
            }
        }
        return platform;}
}