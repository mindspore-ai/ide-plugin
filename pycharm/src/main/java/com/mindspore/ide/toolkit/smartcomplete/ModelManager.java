package com.mindspore.ide.toolkit.smartcomplete;

import com.mindspore.ide.toolkit.smartcomplete.grpc.CompletionException;
import io.grpc.ManagedChannel;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public enum ModelManager {
    INSTANCE;

    private final ModelProcess modelProcess = new ModelProcess();

    private final ModelFile modelFile = new ModelFile();

    private final int queueCapacity = 100;

    private final ThreadPoolExecutor restartModelExecutor = new ThreadPoolExecutor(
            2 * Runtime.getRuntime().availableProcessors(),
            4 * Runtime.getRuntime().availableProcessors(),
            0L,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(queueCapacity),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    public void initSmartCompleteModel() {
        if (!modelFile.modelExeExists()) {
            modelFile.fetchModelFile();
        }
        modelProcess.initModel();
    }

    public void shutDownSmartCompleteModel() {
        modelProcess.shutDownModel();
        restartModelExecutor.shutdown();
    }

    public ManagedChannel getChannel()throws CompletionException {
        return modelProcess.getChannel();
    }
}