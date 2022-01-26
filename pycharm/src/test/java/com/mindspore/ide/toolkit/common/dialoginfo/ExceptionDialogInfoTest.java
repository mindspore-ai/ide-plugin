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

package com.mindspore.ide.toolkit.common.dialoginfo;

import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.SystemInfo;
import com.jetbrains.python.packaging.PyExecutionException;
import com.jetbrains.python.sdk.PythonSdkType;
import com.mindspore.ide.toolkit.common.enums.EnumError;
import com.mindspore.ide.toolkit.common.enums.EnumProperties;
import com.mindspore.ide.toolkit.common.exceptions.MsToolKitException;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Field;

/**
 * ExceptionDialogInfo Test
 *
 * @since 2022-1-27
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Messages.class, PythonSdkType.class, SystemInfo.class})
public class ExceptionDialogInfoTest {
    @Test
    public void showDialogTest() {
        ExceptionDialogInfo dialogInfo = new ExceptionDialogInfo.Builder()
                .isSuccessful(true)
                .description("description")
                .command("command")
                .output("output")
                .solution("solution")
                .title("test")
                .build();
        Assertions.assertDoesNotThrow(() -> {
            dialogInfo.showDialog();
        });

        dialogInfo.setSuccessful(false);
        Assertions.assertThrows(NullPointerException.class, () -> {
            dialogInfo.showDialog();
        });
    }

    @Test
    public void showDialogSyncTest() throws Exception {
        ExceptionDialogInfo dialogInfo = new ExceptionDialogInfo.Builder()
                .description("description")
                .command("command")
                .output("output")
                .solution("solution")
                .build();

        PowerMockito.mockStatic(Messages.class);
        PowerMockito.when(Messages.showYesNoDialog(Mockito.anyString(),
                Mockito.anyString(), Mockito.any())).thenReturn(Messages.YES);

        Assertions.assertThrows(LinkageError.class, () -> {
            Whitebox.invokeMethod(dialogInfo, "showDialogSync");
        });
        dialogInfo.setTitle("test");
        Assertions.assertThrows(LinkageError.class, () -> {
            Whitebox.invokeMethod(dialogInfo, "showDialogSync");
        });
    }

    @Test
    public void getPyExecutionErrorSolutionFromReasonTest() throws Exception {
        String solution = Whitebox.invokeMethod(ExceptionDialogInfo.class,
                "getPyExecutionErrorSolutionFromReason",
                "CondaHTTPError", null);
        Assertions.assertEquals(EnumProperties.EXCEPTION_SOLUTION_PROPERTIES
                .getProperty("check.network"), solution);

        solution = Whitebox.invokeMethod(ExceptionDialogInfo.class,
                "getPyExecutionErrorSolutionFromReason",
                "proxy", null);
        Assertions.assertEquals(EnumProperties.EXCEPTION_SOLUTION_PROPERTIES
                .getProperty("check.conda.proxy"), solution);

        solution = Whitebox.invokeMethod(ExceptionDialogInfo.class,
                "getPyExecutionErrorSolutionFromReason",
                "SyntaxError", null);
        Assertions.assertEquals(EnumProperties.EXCEPTION_SOLUTION_PROPERTIES.getProperty("use.right.python"), solution);

        PowerMockito.mockStatic(PythonSdkType.class);
        PowerMockito.when(PythonSdkType.getLanguageLevelForSdk(Mockito.any())).thenReturn(null);
        solution = Whitebox.invokeMethod(ExceptionDialogInfo.class,
                "getPyExecutionErrorSolutionFromReason",
                "SyntaxError", Whitebox.newInstance(Sdk.class));
        Assertions.assertEquals(EnumProperties.EXCEPTION_SOLUTION_PROPERTIES
                .getProperty("use.right.python.version", "null"), solution);

        solution = Whitebox.invokeMethod(ExceptionDialogInfo.class, "getPyExecutionErrorSolutionFromReason", "", null);
        Assertions.assertNull(solution);
    }

    @Test
    public void containsInOutOrErrTest() throws Exception {
        PyExecutionException pyExecutionException = PowerMockito.mock(PyExecutionException.class);
        PowerMockito.when(pyExecutionException.getStdout()).thenReturn("abc");
        PowerMockito.when(pyExecutionException.getStderr()).thenReturn("def");

        boolean isContains = Whitebox.invokeMethod(ExceptionDialogInfo.class,
                "containsInOutOrErr",
                pyExecutionException, "sss");
        Assertions.assertFalse(isContains);
    }

    @Test
    public void getPyExecutionErrorSolutionTest() throws Exception {
        PyExecutionException pyExecutionException = PowerMockito.mock(PyExecutionException.class);

        // 测试 if (reason != null) 分支
        String result = Whitebox.invokeMethod(ExceptionDialogInfo.class,
                "getPyExecutionErrorSolution",
                pyExecutionException, "CondaHTTPError", null);
        Assertions.assertEquals(EnumProperties.EXCEPTION_SOLUTION_PROPERTIES.getProperty("check.network"), result);

        // 测试  if (SystemInfo.isLinux &&  分支
        PowerMockito.when(pyExecutionException.getStdout()).thenReturn("pyconfig.h");
        PowerMockito.when(pyExecutionException.getStderr()).thenReturn("pyconfig.h");
        Field field = PowerMockito.field(SystemInfo.class, "isLinux");
        field.set(SystemInfo.class, true);
        result = Whitebox.invokeMethod(ExceptionDialogInfo.class,
                "getPyExecutionErrorSolution",
                pyExecutionException, null, null);
        Assertions.assertEquals(EnumProperties.EXCEPTION_SOLUTION_PROPERTIES
                .getProperty("install.python.into.computer"), result);

        // 测试 if ("pip".equals(pyExecutionException.getCommand())) {
        field.set(SystemInfo.class, false);
        PowerMockito.when(pyExecutionException.getCommand()).thenReturn("pip");
        // sdk为null的情况
        result = Whitebox.invokeMethod(ExceptionDialogInfo.class,
                "getPyExecutionErrorSolution",
                pyExecutionException, null, null);
        Assertions.assertEquals(EnumProperties.EXCEPTION_SOLUTION_PROPERTIES
                .getProperty("has.pip.in.python.interpreter"), result);
        // sdk不为null的情况
        Sdk sdk = PowerMockito.mock(Sdk.class);
        String homePath = "homePath";
        PowerMockito.when(sdk.getHomePath()).thenReturn(homePath);
        result = Whitebox.invokeMethod(ExceptionDialogInfo.class,
                "getPyExecutionErrorSolution",
                pyExecutionException, null, sdk);
        Assertions.assertEquals(EnumProperties.EXCEPTION_SOLUTION_PROPERTIES
                .getProperty("has.pip.in.python.interpreter.locate", homePath), result);

        // 所有条件都不满足，return null
        PowerMockito.when(pyExecutionException.getCommand()).thenReturn("11111");
        result = Whitebox.invokeMethod(ExceptionDialogInfo.class,
                "getPyExecutionErrorSolution",
                pyExecutionException, null, null);
        Assertions.assertNull(result);
    }

    @Test
    public void findErrorReasonTest() throws Exception {
        String errorStr = "000 error:123 456";
        String result = Whitebox.invokeMethod(ExceptionDialogInfo.class, "findErrorReason", errorStr);
        Assertions.assertEquals(errorStr, result);

        errorStr = "000 123 456";
        result = Whitebox.invokeMethod(ExceptionDialogInfo.class, "findErrorReason", errorStr);
        Assertions.assertNull(result);
    }

    @Test
    public void parsePyExecutionExceptionTest() throws Exception {
        String unknownDesp = "Unknown exception";
        ExceptionDialogInfo exceptionDialogInfo = Whitebox.invokeMethod(ExceptionDialogInfo.class,
                "parsePyExecutionException", new RuntimeException());
        Assertions.assertEquals(unknownDesp, exceptionDialogInfo.getDescription());

        PyExecutionException pyExecutionException = PowerMockito.mock(PyExecutionException.class);
        PowerMockito.when(pyExecutionException.getStdout()).thenReturn("pyconfig.h");
        PowerMockito.when(pyExecutionException.getStderr()).thenReturn("pyconfig.h");
        exceptionDialogInfo = Whitebox.invokeMethod(ExceptionDialogInfo.class,
                "parsePyExecutionException", pyExecutionException);
        Assertions.assertNull(exceptionDialogInfo.getDescription());
    }

    @Test
    public void parseExceptionTest() {
        PyExecutionException pyExecutionException = PowerMockito.mock(PyExecutionException.class);
        PowerMockito.when(pyExecutionException.getStdout()).thenReturn("pyconfig.h");
        PowerMockito.when(pyExecutionException.getStderr()).thenReturn("pyconfig.h");

        ExceptionDialogInfo exceptionDialogInfo = ExceptionDialogInfo.parseException(pyExecutionException);
        Assertions.assertNull(exceptionDialogInfo.getDescription());

        MsToolKitException msToolKitException = new MsToolKitException(EnumError.CONDA_EXECUTABLE_NOT_SPECIFIED);
        exceptionDialogInfo = ExceptionDialogInfo.parseException(msToolKitException);
        Assertions.assertEquals(EnumError.CONDA_EXECUTABLE_NOT_SPECIFIED.getSolution(),
                exceptionDialogInfo.getSolution());

        String msg = "test";
        RuntimeException runtimeException = new RuntimeException(msg);
        exceptionDialogInfo = ExceptionDialogInfo.parseException(runtimeException);
        Assertions.assertEquals(msg, exceptionDialogInfo.getDescription());

        String unknownDesp = "Unknown exception";
        runtimeException = new RuntimeException();
        exceptionDialogInfo = ExceptionDialogInfo.parseException(runtimeException);
        Assertions.assertEquals(unknownDesp, exceptionDialogInfo.getDescription());
    }
}
