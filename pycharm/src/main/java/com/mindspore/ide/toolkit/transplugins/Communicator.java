package com.mindspore.ide.toolkit.transplugins;

import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.mindspore.ide.toolkit.common.utils.GsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

public enum Communicator {
    INSTANCE;
    private static final Logger LOG = LoggerFactory.getLogger(Communicator.class);

    public void setParams(Project project) {
        EnvironmentProperty property = null;
        File file = Paths.get(project.getBasePath() + File.separator + ".mindsporeEnv.json").toFile();
        try (InputStreamReader fileReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
             JsonReader reader = new JsonReader(fileReader)) {
            property = GsonUtils.INSTANCE.getGson().fromJson(reader, EnvironmentProperty.class);
        } catch (IOException ioException) {
            LOG.warn("Error of reading from property : file is {}", file.getPath());
        } catch (JsonParseException exception) {
            LOG.warn("This file is not a legal json file : path is {}", file.getPath());
        }
        PropertiesComponent component = PropertiesComponent.getInstance(project);
        if (EnvironmentProperty.valid(property)) {
            component.setValue("mindspore.os", property.getOs());
            LOG.info("mindspore.os : {}", property.getOs());
            component.setValue("mindspore.hardwarePlatform", property.getHardware());
            LOG.info("mindspore.hardwarePlatfor : {}", property.getHardware());
        } else {
            LOG.warn("property is invalid : file is {}", file.getPath());
            component.setValue("mindspore.os", "");
            component.setValue("mindspore.hardwarePlatform", "");
        }
        component.setValue("mindspore.projectPath", project.getBasePath());
        LOG.info("mindspore.projectPath : {}", project.getBasePath());
    }

    public void invokeModelArts(AnActionEvent event) {
        ActionManager.getInstance().getAction("MAToolkit.EditTrainingJobConfig").actionPerformed(event);
    }
}