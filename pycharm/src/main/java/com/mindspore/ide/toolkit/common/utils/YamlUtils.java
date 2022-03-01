package com.mindspore.ide.toolkit.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
public enum YamlUtils {
    INSTANCE;

    public <T> Optional<T> readResourceFile(String resourceFile , Class<T> targetClass){
        try (InputStream configStream = getClass().getClassLoader().getResourceAsStream(resourceFile)){
           return Optional.of(parse(configStream,targetClass));
        }catch (IOException | YAMLException exception){
            log.info("failed to read yaml file {}",resourceFile,exception);
        }
        return Optional.empty();
    }

    public <T> T parse(InputStream yamlStream ,Class<T> targetClass){
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);
        Yaml yaml = new Yaml(new CustomClassLoaderConstructor(targetClass.getClassLoader()),representer);
        return yaml.loadAs(yamlStream,targetClass);
    }

    public static <T> Optional<T> read(String configPath,Class<T> configClass){
        Yaml yaml = new Yaml(new Constructor(configClass));
        try (InputStream in = Files.newInputStream(Paths.get(configPath)) ) {
            return  Optional.of(yaml.load(in));
        }catch (IOException | YAMLException exception){
          log.error("Init MindSpore config file failed!",exception);
          return Optional.empty();
        }
    }

    public <T> Optional<T> readConfigFile(String configPath,Class<T> configClass){
        try (InputStream in = Files.newInputStream(Paths.get(configPath))){
            return Optional.of(parse(in,configClass));
        }catch (IOException | YAMLException exception){
            log.error("Init MindSpore config file failed !",exception);
            return Optional.empty();
        }
    }

    /**
     * 读取本地的yaml文件，并转换为对应的类
     *
     * @param localFile 本地文件
     * @param targetClass 目标类类型
     * @param <T> 目标类
     * @return 实例
     */
    public <T> Optional<T> readLocalFile(String localFile, Class<T> targetClass) {
        try (InputStream configStream = Files.newInputStream(Paths.get(localFile))) {
            return Optional.of(parse(configStream, targetClass));
        } catch (IOException ioException) {
            log.error("read local yaml file failed.", ioException);
        }
        return Optional.empty();
    }
}
