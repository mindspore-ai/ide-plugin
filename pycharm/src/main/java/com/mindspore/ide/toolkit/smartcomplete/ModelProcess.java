package com.mindspore.ide.toolkit.smartcomplete;

import com.mindspore.ide.toolkit.common.utils.FileUtils;
import com.mindspore.ide.toolkit.smartcomplete.grpc.CompletionException;
import com.mindspore.ide.toolkit.smartcomplete.grpc.PortUtil;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ModelProcess {
    private static final int READ_INVALID_LINE = 100;

    private static final CompleteConfig completeConfig = CompleteConfig.get();

    private final String modelExeFullPath = completeConfig.getModelExeFullPath();

    private final String modelUnzipFolderFullPath = completeConfig.getModelUnzipFolderFullPath();

    private Process proc;

    private int port;

    private static final Integer DEFAULT_PORT = 50053;

    private BufferedReader procReader;

    private final Object communicateLock = new Object();

    private volatile boolean isInited = false;


    public void initModel() {
        isInited = false;
        try {
            initProcess();
        } catch (IOException ioException) {
            log.info("Init smart complete model failed.", ioException);
        }
    }

    public void shutDownModel() {
        isInited = false;
        try {
            communicateWithModel("smartcoder shutdown");
        } catch (IOException ioException) {
            log.info("Shut down smart complete model failed.", ioException);
        }
        proc.destroy();
        proc = null;
    }

    public String communicateWithModel(String cmd) throws IOException {
        synchronized (communicateLock) {
            String backMsg = "";
            if (!isInited || !isAlive()) {
                return backMsg;
            }
            byte[] cmdByteArr = cmd.getBytes(StandardCharsets.UTF_8);
            proc.getOutputStream().write(cmdByteArr);
            proc.getOutputStream().flush();
            backMsg = procReader.readLine();
            return backMsg == null ? "" : backMsg;
        }
    }

    public boolean isAlive() {
        if (proc == null) {
            return false;
        }
        return proc.isAlive();
    }

    private void initProcess() throws IOException {
        this.port = PortUtil.findAnIdlePort(DEFAULT_PORT);
        if (this.port == 0) {
            log.info("Not find available port");
        } else {
            if (proc != null) {
                proc.destroy();
                proc = null;
            }
            ProcessBuilder builder = new ProcessBuilder(new String[]{modelExeFullPath, "-p", String.valueOf(this.port)});
            builder.directory(FileUtils.getFile(modelUnzipFolderFullPath));
            proc = builder.start();
            procReader = new BufferedReader(new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8));
        }
    }

    public ManagedChannel getChannel() throws CompletionException {
        return ManagedChannelBuilder.forTarget("localhost:" + this.port).usePlaintext().build();
    }
}