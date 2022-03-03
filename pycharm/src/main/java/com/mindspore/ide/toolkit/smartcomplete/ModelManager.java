package com.mindspore.ide.toolkit.smartcomplete;

import com.mindspore.ide.toolkit.common.utils.FileUtils;
import com.mindspore.ide.toolkit.protomessage.CompleteReply;
import com.mindspore.ide.toolkit.smartcomplete.grpc.CompletionException;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.Optional;

/**
 * 模型管理组件，所有涉及到模型的动作，都直接和该类对接
 * 负责组织对ModelFile类和ModelProcess的调用，对外提供启动、关闭和同模型交互的服务
 *
 * @since 2022-1-19
 */
@Slf4j
public enum ModelManager {
    INSTANCE;

    private final CompleteConfig completeConfig = CompleteConfig.get();

    private final CompleteConfig.Model currentModel = completeConfig.getCurrentModel();

    private final CompleteConfig.Model oldModel = completeConfig.getOldModelInDisk().orElse(null);

    private volatile ModelProcess modelProcess;

    private volatile ModelProcess oldModelProcess;

    private final ModelFile modelFile = new ModelFile();

    private final int queueCapacity = 100;

    /**
     * 删除旧模型的超时时间
     */
    private final Long deleteOverTime = 3000L;

    private final ThreadPoolExecutor modelExecutor = new ThreadPoolExecutor(
            2 * Runtime.getRuntime().availableProcessors(),
            4 * Runtime.getRuntime().availableProcessors(),
            0L,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(queueCapacity),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    private final Object modelLock = new Object();

    /**
     * start model
     */
    public void startCompleteModel() {
        modelExecutor.execute(() -> {
            synchronized (modelLock) {
                if (modelProcess != null) {
                    return;
                }

                if (modelFile.modelExeExists()) {
                    // 当前模型存在
                    log.info("Start current model.");
                    startCurrentModel();
                } else if (oldModel == null) {
                    // 当前模型和旧模型都不存在，获取并启动当前模型
                    log.info("Download and start current model.");
                    modelFile.fetchModelFile();
                    startCurrentModel();
                } else {
                    // 当前模型不存在，旧模型存在。启动旧模型，同时获取并启动当前模型
                    log.info("Start old model and download current model.");
                    startOldModel();
                    log.info("Start old model process done, current time is {}.", System.currentTimeMillis());
                    startCurrentModelAndStopOldModel();
                }
            }
        });
    }

    /**
     * shut down
     */
    public void shutDownSmartCompleteModel() {
        if (modelProcess != null) {
            modelProcess.shutDownModel();
        }
        if (oldModelProcess != null) {
            oldModelProcess.shutDownModel();
        }
        modelExecutor.shutdown();
        modelFile.shutdownExecutor();
    }

    /**
     * communicate with model
     *
     * @param before String
     * @param options String
     * @return Optional<CompleteReply>
     * @throws CompletionException completion exception
     */
    public Optional<CompleteReply> communicateWithModel(String before, String options) throws CompletionException {
        if (modelProcess == null && oldModelProcess != null) {
            // 新模型进程未启动，旧模型进程已启动
            if (oldModelProcess.isAlive()) {
                // 如果旧模型is alive，调用旧模型
                return oldModelProcess.retrieveCompletions(before, options);
            } else {
                // 如果旧模型is not alive，返回空
                return Optional.empty();
            }
        } else if (modelProcess == null) {
            return Optional.empty();
        } else if (modelProcess.isInited() && !modelProcess.isAlive()) {
            // 模型进程初始化过，但是非alive，需要重启
            // 该情况表示，模型曾经启动过，但是模型exe文件因某些原因被停掉了，因此要重启
            restartCurrentCompleteModel();
            return Optional.empty();
        } else if (modelProcess.isAlive()) {
            // 如果新模型is alive，调用新模型
            return modelProcess.retrieveCompletions(before, options);
        } else {
            return Optional.empty();
        }
    }

    private void startCurrentModelAndStopOldModel() {
        modelExecutor.execute(() -> {
            synchronized (modelLock) {
                boolean isDownloadSucceed = modelFile.fetchModelFile();
                if (!isDownloadSucceed) {
                    return;
                }
                modelProcess = new ModelProcess();
                modelProcess.initModel(currentModel);
                log.info("Start current model process done, current time is {}.", System.currentTimeMillis());
                // 启动当前模型之后，关闭并删除旧的模型
                shutdownAndDeleteOldModel();
            }
        });
    }

    private void shutdownAndDeleteOldModel() {
        if (oldModelProcess != null) {
            oldModelProcess.shutDownModel();
            oldModelProcess = null;
        }
        log.info("Stop old model process done, current time is {}.", System.currentTimeMillis());
        // 删除旧模型文件
        Long startTime = System.currentTimeMillis();
        while (FileUtils.fileExist(completeConfig.getModelFolderPath())) {
            modelFile.deleteInvalidModelAsync();
            if (System.currentTimeMillis() - startTime > deleteOverTime) {
                break;
            }
        }
    }

    private void restartCurrentCompleteModel() {
        modelExecutor.execute(() -> {
            synchronized (modelLock) {
                if (modelProcess.isInited() && !modelProcess.isAlive()) {
                    if (!modelFile.modelExeExists()) {
                        modelFile.fetchModelFile();
                    }
                    modelProcess.initModel(currentModel);
                }
            }
        });
    }

    private void startCurrentModel() {
        if (modelProcess != null || !modelFile.modelExeExists()) {
            return;
        }
        modelFile.deleteInvalidModelAsync();
        modelProcess = new ModelProcess();
        modelProcess.initModel(currentModel);
    }

    private void startOldModel() {
        if (oldModelProcess != null) {
            return;
        }
        oldModelProcess = new ModelProcess();
        oldModelProcess.initModel(oldModel);
    }
}