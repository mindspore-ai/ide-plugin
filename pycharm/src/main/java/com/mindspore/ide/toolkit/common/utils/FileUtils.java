package com.mindspore.ide.toolkit.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;

@Slf4j
public class FileUtils {

    public static void writeFile(String filePath, InputStream input) throws IOException {
        Path path = Paths.get(filePath);
        createDirectories(path);
        try (FileChannel outChannel = new RandomAccessFile(filePath,"rw").getChannel();
             ReadableByteChannel inChannel = Channels.newChannel(input);
        ) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while(true){
                if(inChannel.read(buffer) == -1){
                    break;
                }
                buffer.flip();
                outChannel.write(buffer);
                buffer.clear();
            }
        }
    }

    public static boolean hideFile(File file){
        try {
            Path path = file.toPath();
            Files.setAttribute(path,"doc:hidden",Boolean.TRUE);
        }catch (IOException exception){
            log.info("hide file fail");
            return false;
        }
        return  true;
    }

    public static File getFile(String filePath){
        return new File(filePath);
    }

    public static File toFile(String resourceName){
        URL url = FileUtils.class.getResource(resourceName);
        if(url == null){
            return null;
        }
        return new File(url.getFile());
    }

    public static void writeDataToFile(Path path,String data) throws IOException{
        createDirectories(path);
        Files.write(path,data.getBytes(StandardCharsets.UTF_8));
    }

    private static void createDirectories(Path path) throws IOException {
        Path parentPath = path.getParent();
        if(parentPath != null ){
            if(! Files.exists(parentPath)){
                Files.createDirectories(parentPath);
            }
        }
    }

    public static Optional<String> loadDataFromFile(Path path) throws IOException{
        if(Files.exists(path)){
            return Optional.of(new String(Files.readAllBytes(path),StandardCharsets.UTF_8));
        }
        return Optional.empty();
    }

    public static boolean fileExist(String filePath){
        return FileUtils.getFile(filePath).exists();
    }

    public static boolean fileLsExists(String path){
        return Files.exists(Paths.get(path));
    }

    public static boolean isFile(String path) {
        if(path==null||path.equals("")){
            return false;
        }
        File file = new File(path);
        return file.exists();
    }

    public static void createFile(String filePath) throws IOException{
        Path path = Paths.get(filePath);
        Path parentPath = path.getParent();
        if(parentPath != null ){
            Files.createDirectories(parentPath);
        }
        Files.createDirectories(path);
    }

    public static boolean touchFile(String path){
        if(fileLsExists(path)){
            return true;
        }
        try {
            createFile(path);
            return true;
        }catch (IOException exception){
            log.error("create file failed with error{}",exception.getMessage());
            return false;
        }
    }

    public static boolean touchDir(String dirPath){
        Path path = Paths.get(dirPath);
        if(Files.notExists(path)&& !Files.isDirectory(path)){
            try {
                Files.createDirectories(path);
            }catch (IOException exception){
                log.error("create directory failed with error{}",exception.getMessage());
                return false;
            }
        }
        return true;
    }

    public static Optional<String> readFileContent(String filePath) throws IOException{
        if(!fileLsExists(filePath)){
            return Optional.empty();
        }
        Path path = Paths.get(filePath);
        return  Optional.of(new String(Files.readAllBytes(path),StandardCharsets.UTF_8));
    }

    public static void deleteDir(String path) throws IOException{
        Path directory = Paths.get(path);
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException{
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
