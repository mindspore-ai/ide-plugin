package com.mindspore.ide.toolkit.common.utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

public class RunExecUtils {

    public static String runExec(List<String> cmd) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        processBuilder.redirectErrorStream(true);
        Process pipListPro = processBuilder.start();
        InputStream inputStream = pipListPro.getInputStream();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("utf-8")))) {
            String str;
            while ((str = reader.readLine()) != null) {
                stringBuilder.append(str);
            }
        } finally {
            inputStream.close();
        }
        pipListPro.destroy();
        return stringBuilder.toString();
    }
}
