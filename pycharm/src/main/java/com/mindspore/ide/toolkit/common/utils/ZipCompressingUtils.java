package com.mindspore.ide.toolkit.common.utils;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipCompressingUtils {
    private static final int CACHE_SIZE = 1024;
    private static final String TEMPORARY_FILE_NAME = "templateModel.zip";

    private ZipCompressingUtils() {
    }

    public static void unzipFile(String filePath, String targetDirPath) throws IOException {
        File pathFile = new File(targetDirPath);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        String tempTargetDirPath = targetDirPath;
        if (!tempTargetDirPath.equals("")) {
            tempTargetDirPath = tempTargetDirPath + File.separator;
        }

        File zipFile = new File(pathFile.getCanonicalPath() + File.separator + TEMPORARY_FILE_NAME);
        try (
                InputStream inputStream = ZipCompressingUtils.class.getResourceAsStream(filePath);
                OutputStream outputStream = new FileOutputStream(zipFile)
        ) {
            if (inputStream == null) {
                throw new FileNotFoundException("unable to find zip file");
            }
            byte[] bytes = new byte[CACHE_SIZE];
            while (inputStream.read(bytes) != -1) {
                outputStream.write(bytes);
            }
            outputStream.flush();
        }
        try (ZipFile zip = new ZipFile(zipFile)) {
            traversalFiles(zip, tempTargetDirPath, false);
        }
        boolean suc = zipFile.delete();
    }

    private static void readInputStreamAndWriteToTargetFile(ZipFile zip, ZipEntry entry, String outPath) throws IOException {
        try (
                InputStream inputStream = zip.getInputStream(entry);
                OutputStream outputStream = new FileOutputStream(outPath);
        ) {
            byte[] buff = new byte[CACHE_SIZE];
            int length;
            while ((length = inputStream.read(buff)) > 0) {
                outputStream.write(buff, 0, length);
            }
        }
    }

    private static void traversalFiles(ZipFile zip, String targetPath, boolean isNeedRename) throws IOException {
        for (Enumeration entries = zip.entries(); entries.hasMoreElements(); ) {
            Object zipEntry = entries.nextElement();
            if (zipEntry instanceof ZipEntry) {
                ZipEntry entry = (ZipEntry) zipEntry;
                String zipEntryName = entry.getName();
                String outPath = isNeedRename ? targetPath + File.separator + zipEntryName :
                        (targetPath + zipEntryName).replace("\\*", File.pathSeparator);
                if (entry.isDirectory()) {
                    boolean isMade = new File(outPath).mkdir();
                    continue;
                }
                readInputStreamAndWriteToTargetFile(zip, entry, outPath);
            }
        }
    }
}
