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
import com.mindspore.ide.toolkit.common.utils.FileUtils;
import com.mindspore.ide.toolkit.common.utils.NotificationUtils;
import com.mindspore.ide.toolkit.smartcomplete.grpc.PortUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * ModelProcess Test
 *
 * @since 2022-1-27
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({PortUtil.class,
        FileUtils.class,
        ModelProcess.class,
        NotificationUtils.class,
        PluginManagerCore.class})
public class ModelProcessTest {
    @Before
    public void setUp() {
        IdeaPluginDescriptorImpl descriptor = PowerMockito.mock(IdeaPluginDescriptorImpl.class);
        PowerMockito.when(descriptor.getVersion()).thenReturn("test");
        PowerMockito.mockStatic(PluginManagerCore.class);
        PowerMockito.when(PluginManagerCore.getPlugin(Mockito.any())).thenReturn(descriptor);
    }

    @Test
    public void getChannelTest() throws Exception {
        ModelProcess modelProcess = new ModelProcess();
        Assertions.assertNotNull(Whitebox.invokeMethod(modelProcess, "getChannel"));
    }

    @Test
    public void initProcessTest() {
        ModelProcess modelProcess = new ModelProcess();

        CompleteConfig.Model model = PowerMockito.mock(CompleteConfig.Model.class);
        PowerMockito.when(model.getModelVersion()).thenReturn(null);

        CompleteConfig completeConfig = PowerMockito.mock(CompleteConfig.class);
        PowerMockito.when(completeConfig.getModelExeFullPath(model)).thenReturn(null);
        PowerMockito.when(completeConfig.getModelUnzipFolderFullPath(model)).thenReturn(null);

        PowerMockito.mockStatic(FileUtils.class);
        Method getFileMethod = PowerMockito.method(FileUtils.class, "getFile", String.class);
        PowerMockito.suppress(getFileMethod);

        Assertions.assertThrows(NullPointerException.class, () -> {
            Whitebox.invokeMethod(modelProcess, "initProcess", completeConfig, model);
        });
    }

    @Test
    public void initProcessAndPortTest() throws Exception {
        ModelProcess modelProcess = new ModelProcess();

        // 当port为0的时候
        PowerMockito.mockStatic(PortUtil.class);
        PowerMockito.when(PortUtil.findAnIdlePort(Mockito.anyInt())).thenReturn(0);

        CompleteConfig.Model model = PowerMockito.mock(CompleteConfig.Model.class);
        PowerMockito.when(model.getModelVersion()).thenReturn(null);

        CompleteConfig completeConfig = PowerMockito.mock(CompleteConfig.class);
        PowerMockito.when(completeConfig.getModelExeFullPath(model)).thenReturn(null);
        PowerMockito.when(completeConfig.getModelUnzipFolderFullPath(model)).thenReturn(null);

        Assertions.assertDoesNotThrow(() -> {
            Whitebox.invokeMethod(modelProcess, "initProcessAndPort", completeConfig, model);
        });

        // 当port不为0的时候
        PowerMockito.when(PortUtil.findAnIdlePort(Mockito.anyInt())).thenReturn(1);

        // ModelProcess的proc为null的情况
        Method initProcessMethod = PowerMockito.method(ModelProcess.class, "initProcess");
        PowerMockito.suppress(initProcessMethod);
        Whitebox.invokeMethod(modelProcess, "initProcessAndPort", completeConfig, model);

        // ModelProcess的proc不为null的情况
        MyProcess myProcess = new MyProcess();
        Field procField = PowerMockito.field(ModelProcess.class, "proc");
        procField.set(modelProcess, myProcess);

        Assertions.assertTrue(modelProcess.isAlive());

        Whitebox.invokeMethod(modelProcess, "initProcessAndPort", completeConfig, model);

        Assertions.assertTrue(modelProcess.isInited());
        Assertions.assertFalse(modelProcess.isAlive());
    }

    @Test
    public void retrieveCompletionsTest() {
        ModelProcess modelProcess = new ModelProcess();
        Assertions.assertTrue(modelProcess.retrieveCompletions("", "").isEmpty());
    }

    @Test
    public void shutDownModelTest() throws IllegalAccessException {
        ModelProcess modelProcess = new ModelProcess();

        Assertions.assertDoesNotThrow(() -> {
            modelProcess.shutDownModel();
        });

        MyProcess myProcess = new MyProcess();
        Field procField = PowerMockito.field(ModelProcess.class, "proc");
        procField.set(modelProcess, myProcess);
        Assertions.assertDoesNotThrow(() -> {
            modelProcess.shutDownModel();
        });
    }

    @Test
    public void initModelTest() throws Exception {
        ModelProcess modelProcess = new ModelProcess();

        CompleteConfig.Model model = PowerMockito.mock(CompleteConfig.Model.class);
        PowerMockito.when(model.getModelVersion()).thenReturn("123456");
        Method notifyMethod = PowerMockito.method(NotificationUtils.class, "notify",
                NotificationUtils.NotifyGroup.class, NotificationType.class, String.class);
        PowerMockito.suppress(notifyMethod);

        Assertions.assertDoesNotThrow(() -> {
            modelProcess.initModel(Whitebox.newInstance(CompleteConfig.class), model);
        });

        Method initProcessAndPortMethod = PowerMockito.method(ModelProcess.class,
                "initProcessAndPort",
                CompleteConfig.class, CompleteConfig.Model.class);
        PowerMockito.suppress(initProcessAndPortMethod);
        Assertions.assertDoesNotThrow(() -> {
            modelProcess.initModel(Whitebox.newInstance(CompleteConfig.class), model);
        });
    }

    private class MyProcess extends Process {
        @Override
        public OutputStream getOutputStream() {
            return null;
        }

        @Override
        public InputStream getInputStream() {
            return null;
        }

        @Override
        public InputStream getErrorStream() {
            return null;
        }

        @Override
        public int waitFor() throws InterruptedException {
            return 0;
        }

        @Override
        public int exitValue() {
            return 0;
        }

        @Override
        public void destroy() {

        }

        @Override
        public boolean isAlive() {
            return true;
        }
    }
}
