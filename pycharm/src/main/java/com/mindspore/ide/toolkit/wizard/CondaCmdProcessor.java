package com.mindspore.ide.toolkit.wizard;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.projectRoots.Sdk;
import com.jetbrains.python.sdk.flavors.PyCondaRunKt;
import com.mindspore.ide.toolkit.common.utils.NotificationUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CondaCmdProcessor {
    public static CondaResponse executeCondaCmd(String condaPath, List<String> cmdList) {
        try {
            final GeneralCommandLine commandLine = new GeneralCommandLine(cmdList);
            log.info("Execute conda command: {}.", commandLine.getCommandLineString());
            final ProcessOutput result = PyCondaRunKt.runConda(condaPath, cmdList);
            return new CondaResponse(result.getExitCode(), result.getStdout(), result.getStderr(), commandLine.getCommandLineString());
        } catch (ExecutionException executionException) {
            return new CondaResponse(-1, "", executionException.getMessage(), String.join(" ", cmdList));
        }
    }

    public static CondaResponse executeCondaCmd(Sdk sdk, List<String> cmdList) {
        try {
            final GeneralCommandLine commandLine = new GeneralCommandLine(cmdList);
            log.info("Execute conda command: {}.", commandLine.getCommandLineString());
            final ProcessOutput result = PyCondaRunKt.runConda(sdk, cmdList);
            return new CondaResponse(result.getExitCode(), result.getStdout(), result.getStderr(), commandLine.getCommandLineString());
        } catch (ExecutionException executionException) {
            return new CondaResponse(-1, "", executionException.getMessage(), String.join(" ", cmdList));
        }
    }

    public static int parseCondaResponse(CondaResponse response, String title) {
        log.info("{} {}.\n------exitCode: {}\n------stdout: {}\n------stderr: {}",
                title,
                response.getExitCode() == 0 ? "succeed" : "failed",
                response.getExitCode(), response.getStdout(), response.getStderr());
        if (response.getExitCode() == 0) {
            NotificationUtils.notify(NotificationUtils.NotifyGroup.NEW_PROJECT,
                    NotificationType.INFORMATION,
                    title + " succeed");
        }
        return response.getExitCode();
    }

    @Getter
    @Setter
    public static class CondaResponse {
        private int exitCode;
        private String stdout;
        private String stderr;
        private String cmd;

        public CondaResponse(int exitCode, String stdout, String stderr, String cmd) {
            this.exitCode = exitCode;
            this.stdout = stdout;
            this.stderr = stderr;
            this.cmd = cmd;
        }
    }
}
