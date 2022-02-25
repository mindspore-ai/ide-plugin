package com.mindspore.ide.toolkit.common;

import com.intellij.util.io.ZipUtil;
import com.mindspore.ide.toolkit.common.utils.FileUtils;
import com.mindspore.ide.toolkit.common.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class ResourceManager {
    public static Optional<Path> getResource(String uri) {
        return Optional.empty();
    }

    public static boolean updateResource(Path oldPath, String uri) {
        return false;
    }

    public static boolean cleanResource(Path resourcePath) {
        return false;
    }

    /**
     * Download resource from server.
     *
     * @param uri uri of resource
     * @param resourceLocation local path of resource
     * @param token token of resource
     * @param timeout timeout of download
     * @return true if download success, false otherwise
     */
    public static boolean downloadResource(String uri, String resourceLocation, String token, int timeout) {
        boolean downloadSucceed = false;
        try (CloseableHttpResponse response = HttpUtils.doGet(uri, buildDownloadHeader(token), timeout)) {
            if (response == null) {
                downloadSucceed = false;
            } else if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                FileUtils.writeFile(resourceLocation, response.getEntity().getContent());
                downloadSucceed = true;
            } else {
                downloadSucceed = false;
            }
        } catch (IOException ioException) {
            log.info("Download resource failed,", ioException);
        }
        return downloadSucceed;
    }

    public static void unzipResource(String zipFileLocation, String unzipFileLocation) {
        try {
            ZipUtil.extract(FileUtils.getFile(zipFileLocation), FileUtils.getFile(unzipFileLocation), null, true);
        } catch (IOException ioException) {
            log.info("Unzip resource failed,", ioException);
        }
    }

    private static Map<String, String> buildDownloadHeader(String token) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
        headerMap.put("csb-token", token);
        return headerMap;
    }
}