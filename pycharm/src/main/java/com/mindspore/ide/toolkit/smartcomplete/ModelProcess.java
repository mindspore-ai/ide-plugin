package com.mindspore.ide.toolkit.smartcomplete;

import com.intellij.notification.NotificationType;
import com.mindspore.ide.toolkit.common.utils.FileUtils;
import com.mindspore.ide.toolkit.common.utils.NotificationUtils;
import com.mindspore.ide.toolkit.protomessage.CompleteReply;
import com.mindspore.ide.toolkit.smartcomplete.grpc.CompletionException;
import com.mindspore.ide.toolkit.smartcomplete.grpc.PortUtil;
import com.mindspore.ide.toolkit.smartcomplete.grpc.SmartCompletionClient;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * 模型进程
 * 负责创建进程同模型exe进行命令行的交互
 *
 * @since 2022-1-19
 */
@Slf4j
public class ModelProcess {
    private Process proc;

    private int port;

    private static final Integer DEFAULT_PORT = 50053;

    private BufferedReader procReader;

    private final Object communicateLock = new Object();

    private volatile boolean isInited = false;

    /**
     * 初始化指定模型
     *
     * @param completeConfig 模型配置
     * @param model 待初始化的模型
     */
    public void initModel(CompleteConfig completeConfig, CompleteConfig.Model model) {
        isInited = false;
        try {
            initProcess(completeConfig, model);
        } catch (IOException ioException) {
            NotificationUtils.notify(NotificationUtils.NotifyGroup.SMART_COMPLETE,
                    NotificationType.ERROR,
                    "Init smart complete model failed. Model version is " + model.getModelVersion());
            log.error("Init smart complete model failed. Model version is {}.", model.getModelVersion(), ioException);
        }
        NotificationUtils.notify(NotificationUtils.NotifyGroup.SMART_COMPLETE,
                NotificationType.INFORMATION,
                "Init smart complete model succeed. Model version is " + model.getModelVersion());
    }

    /**
     * 关闭模型服务
     */
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

    /**
     * 同模型交互
     *
     * @param cmd 待模型执行的命令
     * @return String
     * @throws IOException io exception
     */
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

    /**
     * 获取补全结果
     *
     * @param before String
     * @param options String
     * @return Optional<CompleteReply>
     * @throws CompletionException completion exception
     */
    public Optional<CompleteReply> retrieveCompletions(String before, String options) throws CompletionException {
        ManagedChannel channel = getChannel();
        Optional<CompleteReply> reply;
        try {
            SmartCompletionClient client = new SmartCompletionClient(channel);
            Optional<CompleteReply> completions = client.retrieveCompletions(before, options);
            reply = completions;
        } finally {
            channel.shutdownNow();
        }
        return reply;
    }

    /**
     * is proc alive
     *
     * @return boolean
     */
    public boolean isAlive() {
        if (proc == null) {
            return false;
        }
        return proc.isAlive();
    }

    /**
     * 模型是否初始化
     *
     * @return true or false
     */
    public boolean isInited() {
        return isInited;
    }

    private void initProcess(CompleteConfig completeConfig, CompleteConfig.Model model) throws IOException {
        this.port = PortUtil.findAnIdlePort(DEFAULT_PORT);
        if (this.port == 0) {
            log.info("Cannot find available port, init complete model failed. Model version is {}.",
                    model.getModelVersion());
        } else {
            if (proc != null) {
                proc.destroy();
                proc = null;
            }
            ProcessBuilder builder = new ProcessBuilder(new String[]{completeConfig.getModelExeFullPath(model),
                    "-p", String.valueOf(this.port)});
            builder.directory(FileUtils.getFile(completeConfig.getModelUnzipFolderFullPath(model)));
            proc = builder.start();
            procReader = new BufferedReader(new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8));
            isInited = true;
        }
    }

    private ManagedChannel getChannel() throws CompletionException {
        return ManagedChannelBuilder.forTarget("localhost:" + this.port).usePlaintext().build();
    }
}