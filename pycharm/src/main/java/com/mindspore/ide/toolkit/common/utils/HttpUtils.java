package com.mindspore.ide.toolkit.common.utils;

import com.intellij.util.net.HttpConfigurable;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpUtils {
    private static final int CONNECT_TIMEOUT_TIME = 15000;

    private static final String CHARSET_UTF_8 = "UTF-8";

    public static CloseableHttpResponse doGet(String url, Map<String, String> headerMap) throws IOException {
        if (url == null || url.isEmpty()) {
            return null;
        }
        HttpGet request = new HttpGet();
        request.setURI(URI.create(url));
        if (!headerMap.isEmpty()) {
            headerMap.forEach(request::setHeader);
        }
        return HttpClients.createDefault().execute(request);
    }

    public static HttpResponse doPost(String url, Map<String, String> headerMap, Map<String, String> bodymap)
            throws IOException {
        HttpPost request = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(CONNECT_TIMEOUT_TIME)
                .setConnectionRequestTimeout(CONNECT_TIMEOUT_TIME)
                .build();
        request.setConfig(requestConfig);
        if (headerMap != null && !headerMap.isEmpty()) {
            headerMap.forEach(request::setHeader);
        }
        List<BasicNameValuePair> content = new ArrayList<>();
        if (headerMap != null && !bodymap.isEmpty()) {
            Iterator<Entry<String, String>> iterator = bodymap.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, String> elem = iterator.next();
                content.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
            }
        }
        request.setEntity(new UrlEncodedFormEntity(content, "UTF-8"));
        return HttpClients.createDefault().execute(request);
    }

    public static void download(String url, String filePath) throws IOException {
        download(url, filePath, 600000);
    }

    public static void download(String url, String filePath, int timeOut) throws IOException {
        File file = new File(filePath);
        boolean mkDirs = file.getParentFile().mkdirs();
        if(!mkDirs){
            return;
        }
        HttpURLConnection httpUrlConnect = HttpConfigurable.getInstance().openHttpConnection(url);
        httpUrlConnect.setReadTimeout(timeOut);
        httpUrlConnect.connect();
        try (
                ReadableByteChannel readableByteChannel = Channels.newChannel(httpUrlConnect.getInputStream());
                FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                FileChannel fileChannel = fileOutputStream.getChannel()
        ) {
            long index = 0;
            long memory = 20971520;
            while (fileChannel.size() < httpUrlConnect.getContentLength()) {
                fileChannel.transferFrom(readableByteChannel, index, memory);
                index = fileChannel.size();
            }
        }
    }
}