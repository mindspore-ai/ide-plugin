package com.mindspore.ide.toolkit.search;

import com.intellij.util.io.HttpRequests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 获取md数据
 *
 * @since 2022-08-10
 */
public class MdDataGet {
    private static final String NEW_LINE = "\n";

    private static final Logger LOG = LoggerFactory.getLogger(MdDataGet.class);

    private String mdVersion = "";

    public String pytorchMdStr = "";

    public String tensorflowMdStr = "";

    public MdDataGet(MsVersionDataConfig.MsVersionData msVersionData) {
        mdVersion = msVersionData.getMdVersion();
        pytorchMdStr = getPytorchMdStr(msVersionData.getPytorchMdUrl());
        tensorflowMdStr = getTensorflowMdStr(msVersionData.getTensorflowMdUrl());
    }

    /**
     * torch
     *
     * @param url url
     * @return md内容
     */
    public String getPytorchMdStr(String url) {
        return getMdData(url, 3000);
    }

    /**
     * tf
     *
     * @param url url
     * @return md内容
     */
    public String getTensorflowMdStr(String url) {
        return getMdData(url, 3000);
    }

    /**
     * 通过url获取链接md里面文字内容
     *
     * @param url md链接
     * @param timeout 超时时间
     * @return 文字内容
     */
    private String getMdData(String url, int timeout) {
        String mdData;
        try {
            mdData = HttpRequests.request(url).connectTimeout(timeout).readString();
        } catch (IOException ioException) {
            mdData = "";
            LOG.warn("get md data failed: {}", ioException.toString());
        }
        return mdData;
    }
}
