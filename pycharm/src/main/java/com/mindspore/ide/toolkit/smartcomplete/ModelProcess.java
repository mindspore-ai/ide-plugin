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

import java.io.IOException;
import java.util.Optional;

/**
 * 模型进程
 * 负责创建进程同模型exe进行命令行的交互
 *
 * @since 2022-1-19
 */
@Slf4j
public class ModelProcess {
    private final CompleteConfig completeConfig = CompleteConfig.get();

    private Process proc;

    private int port;

    private static final Integer DEFAULT_PORT = 50053;

    private volatile boolean isInited = false;

    /**
     * 初始化指定模型
     *
     * @param model 待初始化的模型
     */
    public void initModel(CompleteConfig.Model model) {
        isInited = false;
        String notifyContent = "Init smart complete model %s."
                + " Plugin version is " + model.getPluginVersion()
                + ". Model version is " + model.getModelVersion() + ".";
        try {
            initProcessAndPort(model);
        } catch (IOException ioException) {
            log.info("Init smart complete model failed. Plugin version is {}. Model version is {}.",
                    model.getPluginVersion(), model.getModelVersion(), ioException);
        }
        if (isInited) {
            NotificationUtils.notify(NotificationUtils.NotifyGroup.SMART_COMPLETE,
                    NotificationType.INFORMATION,
                    String.format(notifyContent, "succeed"));
        } else {
            NotificationUtils.notify(NotificationUtils.NotifyGroup.SMART_COMPLETE,
                    NotificationType.ERROR,
                    String.format(notifyContent, "failed"));
        }
    }

    /**
     * 关闭模型服务
     */
    public void shutDownModel() {
        isInited = false;
        if (proc == null) {
            return;
        }
        proc.destroy();
        proc = null;
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
        try {
            SmartCompletionClient client = new SmartCompletionClient(channel);
            return client.retrieveCompletions(before, options);
        } finally {
            channel.shutdownNow();
        }
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

    private void initProcessAndPort(CompleteConfig.Model model) throws IOException {
        this.port = PortUtil.findAnIdlePort(DEFAULT_PORT);
        if (this.port == 0) {
            log.info("Cannot find available port, init complete model failed. Model version is {}.",
                    model.getModelVersion());
        } else {
            if (proc != null) {
                proc.destroy();
                proc = null;
            }
            initProcess(model);
            isInited = true;
        }
    }

    private void initProcess(CompleteConfig.Model model) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(new String[]{completeConfig.getModelExeFullPath(model),
                "-p", String.valueOf(this.port)});
        builder.directory(FileUtils.getFile(completeConfig.getModelUnzipFolderFullPath(model)));
        proc = builder.start();
    }

    private ManagedChannel getChannel() throws CompletionException {
        return ManagedChannelBuilder.forTarget("localhost:" + this.port).usePlaintext().build();
    }
}