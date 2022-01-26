/*
 * Copyright 2021-2022 Huawei Technologies Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mindspore.ide.toolkit.smartcomplete;

import com.intellij.ide.plugins.IdeaPluginDescriptorImpl;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationNamesInfo;
import com.mindspore.ide.toolkit.common.ResourceManager;
import com.mindspore.ide.toolkit.common.utils.FileUtils;
import com.mindspore.ide.toolkit.common.utils.NotificationUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * ModelFile Test
 *
 * @since 2022-1-27
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({NotificationUtils.class,
        FileUtils.class, ResourceManager.class,
        PluginManagerCore.class,
        ApplicationNamesInfo.class})
public class ModelFileTest {
    @Before
    public void setUp() {
        Method notifyMethod = PowerMockito.method(NotificationUtils.class, "notify",
                NotificationUtils.NotifyGroup.class, NotificationType.class, String.class);
        PowerMockito.suppress(notifyMethod);

        IdeaPluginDescriptorImpl descriptor = PowerMockito.mock(IdeaPluginDescriptorImpl.class);
        PowerMockito.when(descriptor.getVersion()).thenReturn("test");
        PowerMockito.mockStatic(PluginManagerCore.class);
        PowerMockito.when(PluginManagerCore.getPlugin(Mockito.any())).thenReturn(descriptor);

        ApplicationNamesInfo applicationNamesInfo = PowerMockito.mock(ApplicationNamesInfo.class);
        PowerMockito.when(applicationNamesInfo.getFullProductNameWithEdition()).thenReturn("test");
        PowerMockito.mockStatic(ApplicationNamesInfo.class);
        PowerMockito.when(ApplicationNamesInfo.getInstance()).thenReturn(applicationNamesInfo);
    }

    @Test
    public void deleteInvalidModelTest() {
        CompleteConfig completeConfig = CompleteConfig.get();
        String directoryName = "test";
        String modelFolderPath = "modelFolderPath";

        ModelFile modelFile = new ModelFile();
        Assertions.assertDoesNotThrow(() -> {
            Whitebox.invokeMethod(modelFile,
                    "deleteInvalidModel",
                    completeConfig, directoryName, modelFolderPath);
        });

        modelFile.shutdownExecutor();
    }

    @Test
    public void deleteInvalidModelAsyncTest() throws IOException {
        CompleteConfig completeConfig = CompleteConfig.get();
        ModelFile modelFile = new ModelFile();

        Set<String> directoryNameSet = new HashSet<>();
        directoryNameSet.add("test");
        PowerMockito.mockStatic(FileUtils.class);
        PowerMockito.when(FileUtils.listAllDirectory(Mockito.any()))
                .thenReturn(directoryNameSet);
        Assertions.assertDoesNotThrow(() -> {
            modelFile.deleteInvalidModelAsync(completeConfig);
        });
        modelFile.shutdownExecutor();
    }

    @Test
    public void fetchModelFileTest() {
        ModelFile modelFile = new ModelFile();
        Assertions.assertFalse(modelFile.fetchModelFile());

        PowerMockito.mockStatic(ResourceManager.class);
        PowerMockito.when(ResourceManager
                .downloadResource(Mockito.any(),
                        Mockito.any(),
                        Mockito.any())).thenReturn(true);

        Method unzipResourceMethod = PowerMockito.method(ResourceManager.class,
                "unzipResource",
                String.class, String.class);
        PowerMockito.suppress(unzipResourceMethod);
        Assertions.assertTrue(modelFile.fetchModelFile());
    }

    @Test
    public void modelExeExistsTest() {
        ModelFile modelFile = new ModelFile();
        Assertions.assertFalse(modelFile.modelExeExists());
    }
}
